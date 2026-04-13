package com.labg.transaction.service.impl;

import com.labg.transaction.client.AccountServiceClient;
import com.labg.transaction.exception.SaldoInsuficienteException;
import com.labg.transaction.exception.TransaccionException;
import com.labg.transaction.exception.TransaccionNoEncontradaException;
import com.labg.transaction.model.dto.request.ActualizacionSaldoRequest;
import com.labg.transaction.model.dto.request.TransferenciaRequest;
import com.labg.transaction.model.dto.response.TransactionEvent;
import com.labg.transaction.model.dto.response.TransferenciaResponse;
import com.labg.transaction.model.dto.response.ValidacionFondosResponse;
import com.labg.transaction.model.entity.Reverso;
import com.labg.transaction.model.entity.Transaccion;
import com.labg.transaction.model.enums.EstadoReverso;
import com.labg.transaction.model.enums.EstadoTransaccion;
import com.labg.transaction.model.enums.TipoTransaccion;
import com.labg.transaction.producer.TransactionEventProducer;
import com.labg.transaction.repository.ReversoRepository;
import com.labg.transaction.repository.TransaccionRepository;
import com.labg.transaction.service.TransactionService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TransactionServiceImpl implements TransactionService {
    private final TransaccionRepository transaccionRepository;
    private final ReversoRepository reversoRepository;
    private final AccountServiceClient accountServiceClient;
    private final TransactionEventProducer eventProducer;

    @Override
    @Transactional
    public TransferenciaResponse procesarTransferencia(TransferenciaRequest request, String idempotencyKey) {
        log.info("Procesando transferencia: origen={}, destino={}, monto={}, idempotencyKey={}",
                request.getCuentaOrigenId(), request.getCuentaDestinoId(), request.getMonto(), idempotencyKey);

        if (transaccionRepository.existsByIdempotencyKey(idempotencyKey)) {
            log.warn("Transacción duplicada detectada: {}", idempotencyKey);
            Transaccion existente = transaccionRepository.findByIdempotencyKey(idempotencyKey).get();
            return buildResponse(existente, "Transacción ya procesada anteriormente");
        }

        if (request.getCuentaOrigenId().equals(request.getCuentaDestinoId())) {
            throw new TransaccionException("No se puede transferir a la misma cuenta");
        }

        Transaccion transaccion = crearTransaccionPendiente(request, idempotencyKey);

        try {
            transaccion.setEstado(EstadoTransaccion.VALIDANDO_FONDOS);
            transaccion = transaccionRepository.save(transaccion);

            ValidacionFondosResponse validacion = validarFondosConCircuitBreaker(
                    request.getCuentaOrigenId(), request.getMonto());

            if (!validacion.isFondosSuficientes()) {
                throw new SaldoInsuficienteException(
                        request.getCuentaOrigenId(),
                        validacion.getSaldoDisponible(),
                        request.getMonto()
                );
            }

            transaccion.setEstado(EstadoTransaccion.DEBITANDO_ORIGEN);
            transaccion = transaccionRepository.save(transaccion);

            debitarCuentaConCircuitBreaker(
                    request.getCuentaOrigenId(),
                    request.getMonto(),
                    transaccion.getId().toString()
            );

            transaccion.setEstado(EstadoTransaccion.ACREDITANDO_DESTINO);
            transaccion = transaccionRepository.save(transaccion);

            acreditarCuentaConCircuitBreaker(
                    request.getCuentaDestinoId(),
                    request.getMonto(),
                    transaccion.getId().toString()
            );

            transaccion.setEstado(EstadoTransaccion.COMPLETADA);
            transaccion.setFechaCompletada(LocalDateTime.now());
            transaccion = transaccionRepository.save(transaccion);

            try {
                eventProducer.publicarTransaccionCompletada(transaccion);
            } catch (Exception kaskaEx) {
                log.error("Error publicando evento kafka (transferencia exitosa): {}", kaskaEx.getMessage());
            }

            log.info("Transferencia completada exitosamente: id={}", transaccion.getId());

            return buildResponse(transaccion, "Transferencia realizada exitosamente");
        } catch (Exception e) {
            log.error("Error procesando transferencia: {}", e.getMessage());

            transaccion.setEstado(EstadoTransaccion.FALLIDA);
            transaccion.setErrorMensaje(e.getMessage());
            transaccion = transaccionRepository.save(transaccion);

            try {
                eventProducer.publicarTransaccionFallida(transaccion);
            } catch (Exception kafkaEx) {
                log.error("Error publicando evento kafka (transferencia fallida): {}", kafkaEx.getMessage());
            }

            throw new TransaccionException("Error procesando transferencia: " + e.getMessage(), e);
        }
    }

    @CircuitBreaker(name = "accountService", fallbackMethod = "ValidacionFondosFallback")
    private ValidacionFondosResponse validarFondosConCircuitBreaker(Long cuentaId, BigDecimal monto) {
        return accountServiceClient.validarFondos(cuentaId, monto);
    }

    private ValidacionFondosResponse validacionFondosFallback(Long cuentaId, BigDecimal monto, Exception e) {
        log.error("Fallback: Error validando fondos para cuenta {}: {}", cuentaId, e.getMessage());
        return ValidacionFondosResponse.builder()
                .valida(false)
                .fondosSuficientes(false)
                .mensaje("Servicio de cuentas no disponible")
                .build();
    }

    @CircuitBreaker(name = "accountService", fallbackMethod = "debitarCuentaFallback")
    private void debitarCuentaConCircuitBreaker(Long cuentaId, BigDecimal monto, String transaccionId) {
        ActualizacionSaldoRequest request = ActualizacionSaldoRequest.builder()
                .monto(monto)
                .transaccionId(transaccionId)
                .esReverso(false)
                .build();
        accountServiceClient.debitarCuenta(cuentaId, request);
    }

    private void debitarCuentaFallback(Long cuentaId, BigDecimal monto, String transaccionId, Exception e) {
        log.error("Fallback: Error debitando cuenta {}: {}", cuentaId, e.getMessage());
        throw new TransaccionException("No se pudo debitar la cuenta origen: " + e.getMessage());
    }

    @CircuitBreaker(name = "accountService", fallbackMethod = "acreditarCuentaFallback")
    private void acreditarCuentaConCircuitBreaker(Long cuentaId, BigDecimal monto, String transaccionId) {
        ActualizacionSaldoRequest request = ActualizacionSaldoRequest.builder()
                .monto(monto)
                .transaccionId(transaccionId)
                .esReverso(false)
                .build();
        accountServiceClient.acreditarCuenta(cuentaId, request);
    }

    private void acreditarCuentaFallback(Long cuentaId, BigDecimal monto, String transaccionId, Exception e) {
        log.error("Fallback: Error acreditando cuenta {}: {}", cuentaId, e.getMessage());
        throw new TransaccionException("No se pudo acreditar la cuenta destino: " + e.getMessage());
    }

    private Transaccion crearTransaccionPendiente(TransferenciaRequest request, String idempotencyKey) {
        Transaccion transaccion = Transaccion.builder()
                .idempotencyKey(idempotencyKey)
                .cuentaOrigenId(request.getCuentaOrigenId())
                .cuentaDestinoId(request.getCuentaDestinoId())
                .monto(request.getMonto())
                .tipo(TipoTransaccion.TRANSFERENCIA)
                .estado(EstadoTransaccion.PENDIENTE)
                .descripcion(request.getDescripcion())
                .referencia(request.getReferencia())
                .metadata(new HashMap<>())
                .build();

        return transaccionRepository.save(transaccion);
    }

    @Override
    public TransferenciaResponse obtenerEstadoTransaccion(Long transaccionId) {
        Transaccion transaccion = transaccionRepository.findById(transaccionId)
                .orElseThrow(() -> new TransaccionNoEncontradaException(transaccionId));
        return buildResponse(transaccion, null);
    }

    @Override
    public TransferenciaResponse obtenerEstadoTransaccionPorIdempotencyKey(String idempotencyKey) {
        Transaccion transaccion = transaccionRepository.findByIdempotencyKey(idempotencyKey)
                .orElseThrow(() -> new TransaccionNoEncontradaException(idempotencyKey));
        return buildResponse(transaccion, null);
    }

    @Override
    @Transactional
    public void revertirTransaccion(Long transaccionId, String motivo) {
        log.info("Revirtiendo transacción: {}, motivo: {}", transaccionId, motivo);

        if (reversoRepository.existsByTransaccionOriginalId(transaccionId)) {
            throw new TransaccionException("La transacción ya fue revertida");
        }

        Transaccion original = transaccionRepository.findById(transaccionId)
                .orElseThrow(() -> new TransaccionNoEncontradaException(transaccionId));

        if (original.getEstado() != EstadoTransaccion.COMPLETADA) {
            throw new TransaccionException("Solo se pueden revertir transacciones completadas");
        }

        Reverso reverso = Reverso.builder()
                .transaccionOriginalId(transaccionId)
                .motivo(motivo)
                .estado(EstadoReverso.PENDIENTE)
                .build();
        reverso = reversoRepository.save(reverso);

        try {
            reverso.setEstado(EstadoReverso.EN_PROCESO);
            reverso = reversoRepository.save(reverso);

            accountServiceClient.debitarCuenta(
                    original.getCuentaDestinoId(),
                    ActualizacionSaldoRequest.builder()
                            .monto(original.getMonto())
                            .transaccionId(transaccionId.toString())
                            .esReverso(true)
                            .build()
            );

            accountServiceClient.acreditarCuenta(
                    original.getCuentaOrigenId(),
                    ActualizacionSaldoRequest.builder()
                            .monto(original.getMonto())
                            .transaccionId(transaccionId.toString())
                            .esReverso(true)
                            .build()
            );

            original.setEstado(EstadoTransaccion.REVERTIDA);
            transaccionRepository.save(original);

            reverso.setEstado(EstadoReverso.COMPLETADO);
            reverso.setFechaCompletado(LocalDateTime.now());
            reversoRepository.save(reverso);

            log.info("Reverso completado exitosamente para transacción: {}", transaccionId);
        } catch (Exception e) {
            log.error("Error en reverso de transacción: {}", transaccionId, e);
            reverso.setEstado(EstadoReverso.FALLIDO);
            reverso.setErrorMensaje(e.getMessage());
            reversoRepository.save(reverso);

            throw new TransaccionException("Error en reverso: " + e.getMessage(), e);
        }
    }

    @Override
    public Transaccion obtenerTransaccionEntity(Long id) {
        return transaccionRepository.findById(id)
                .orElseThrow(() -> new TransaccionNoEncontradaException(id));
    }

    private TransferenciaResponse buildResponse(Transaccion transaccion, String mensaje) {
        return TransferenciaResponse.builder()
                .transaccionId(transaccion.getId())
                .cuentaOrigenId(transaccion.getCuentaOrigenId())
                .cuentaDestinoId(transaccion.getCuentaDestinoId())
                .monto(transaccion.getMonto())
                .estado(transaccion.getEstado())
                .mensaje(mensaje != null ? mensaje : transaccion.getEstado().getDescripcion())
                .fecha(transaccion.getFechaCompletada() != null ?
                        transaccion.getFechaCompletada() : transaccion.getFechaCreacion())
                .idempotencyKey(transaccion.getIdempotencyKey())
                .build();

    }

    @Autowired
    private KafkaTemplate<String, TransactionEvent> kafkaTemplate;

    public void publicarTransaccionesCompletadas(Transaccion transaccion) {
        log.info("Intentando publicar eventos para transacción: {}", transaccion.getId());
        try {
            TransactionEvent event = buildEvent(transaccion);
            kafkaTemplate.send("transacciones-completadas", event);
            log.info("Evento publicado exitosamente");
        } catch (Exception e) {
            log.error("Error publicando evento: {}", e.getMessage(), e);
        }
    }

    private TransactionEvent buildEvent(Transaccion transaccion) {
        return TransactionEvent.builder()
                .transaccionId(transaccion.getId())
                .cuentaDestinoId(transaccion.getCuentaDestinoId())
                .cuentaOrigenId(transaccion.getCuentaOrigenId())
                .monto(transaccion.getMonto())
                .tipo(transaccion.getTipo())
                .estado(transaccion.getEstado())
                .descripcion(transaccion.getDescripcion())
                .fecha(transaccion.getFechaCreacion().toString())
                .errorMensaje(transaccion.getErrorMensaje())
                .build();
    }
}
