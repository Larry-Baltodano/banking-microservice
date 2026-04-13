package com.labg.account.service.impl;

import com.labg.account.exception.CuentaNoEncontradaException;
import com.labg.account.exception.SaldoInsuficienteException;
import com.labg.account.model.dtos.request.CrearCuentaRequest;
import com.labg.account.model.dtos.response.CuentaResponse;
import com.labg.account.model.dtos.response.ValidacionFondosResponse;
import com.labg.account.model.entity.Cliente;
import com.labg.account.model.entity.Cuenta;
import com.labg.account.model.enums.EstadoCuenta;
import com.labg.account.model.enums.TipoCuenta;
import com.labg.account.repository.CuentaRepository;
import com.labg.account.service.ClienteService;
import com.labg.account.service.CuentaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Year;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CuentaServiceImpl implements CuentaService {
    private final CuentaRepository cuentaRepository;
    private final ClienteService clienteService;

    @Override
    @Transactional
    public CuentaResponse crearCuenta(CrearCuentaRequest request) {
        log.info("Creando nueva cuenta para cliente ID: {}", request.getClienteId());

        Cliente cliente = clienteService.obtenerClienteEntity(request.getClienteId());

        TipoCuenta tipo = TipoCuenta.valueOf(request.getTipoCuenta());
        if (cuentaRepository.existsByClienteIdAndTipoCuenta(cliente.getId(), tipo)) {
            throw new RuntimeException("El cliente ya tiene una cuenta de tipo " + tipo);
        }

        String numeroCuenta = generarNumeroCuenta(cliente, tipo);

        Cuenta cuenta = Cuenta.builder()
                .numeroCuenta(numeroCuenta)
                .tipoCuenta(tipo)
                .saldo(BigDecimal.ZERO)
                .moneda(request.getMoneda() != null ? request.getMoneda() : "USD")
                .estado(EstadoCuenta.ACTIVA)
                .cliente(cliente)
                .build();

        cuenta = cuentaRepository.save(cuenta);
        log.info("Cuenta creada exitosamente: {} para cliente: {}", numeroCuenta, cliente.getNombre());

        return convertToDTO(cuenta);
    }

    @Override
    @Cacheable(value = "cuentas", key = "#id")
    public CuentaResponse obtenerCuentaPorId(Long id) {
        Cuenta cuenta = cuentaRepository.findById(id)
                .orElseThrow(() -> new CuentaNoEncontradaException(id));
        return convertToDTO(cuenta);
    }

    @Override
    public CuentaResponse obtenerCuentaPorNumero(String numeroCuenta) {
        Cuenta cuenta = cuentaRepository.findByNumeroCuenta(numeroCuenta)
                .orElseThrow(() -> new CuentaNoEncontradaException(numeroCuenta));
        return convertToDTO(cuenta);
    }

    @Override
    public List<CuentaResponse> listarCuentasPorCliente(Long clienteId) {
        return cuentaRepository.findByClienteId(clienteId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    @CacheEvict(value = "cuentas", key = "#cuentaId")
    public CuentaResponse debitarCuenta(Long cuentaId, BigDecimal monto, String transaccionId) {
        log.info("Debitando cuenta {} por monto {} (transacción: {})", cuentaId, monto, transaccionId);

        Cuenta cuenta = cuentaRepository.findByIdWithLock(cuentaId)
                .orElseThrow(() -> new CuentaNoEncontradaException(cuentaId));

        if (cuenta.getSaldo().compareTo(monto) < 0) {
            throw new SaldoInsuficienteException(cuentaId, cuenta.getSaldo(), monto);
        }

        cuenta.debitar(monto);
        cuenta = cuentaRepository.save(cuenta);

        log.info("Cuenta debitada exitosamente. Nuevo saldo: {}", cuenta.getSaldo());

        return convertToDTO(cuenta);
    }

    @Override
    @Transactional
    @CacheEvict(value = "cuentas", key = "#cuentaId")
    public CuentaResponse acreditarCuenta(Long cuentaId, BigDecimal monto, String transaccionId) {
        log.info("Acreditando cuenta {} por monto {} (transacción: {})", cuentaId, monto, transaccionId);

        Cuenta cuenta = cuentaRepository.findByIdWithLock(cuentaId)
                .orElseThrow(() -> new CuentaNoEncontradaException(cuentaId));

        cuenta.acreditar(monto);
        cuenta = cuentaRepository.save(cuenta);

        log.info("Cuenta acreditada exitosamente. Nuevo saldo: {}", cuenta.getSaldo());

        return convertToDTO(cuenta);
    }

    @Override
    public BigDecimal consultarSaldo(Long cuentaId) {
        Cuenta cuenta = cuentaRepository.findById(cuentaId)
                .orElseThrow(() -> new CuentaNoEncontradaException(cuentaId));
        return cuenta.getSaldo();
    }

    @Override
    public ValidacionFondosResponse validarFondos(Long cuentaId, BigDecimal monto) {
        Cuenta cuenta = cuentaRepository.findById(cuentaId)
                .orElseThrow(() -> new CuentaNoEncontradaException(cuentaId));

        if (cuenta.getSaldo().compareTo(monto) >= 0) {
            return ValidacionFondosResponse.exitosa(cuenta.getSaldo());
        } else {
            return ValidacionFondosResponse.saldoInsuficiente(cuenta.getSaldo());
        }
    }

    @Override
    public Cuenta obtenerCuentaEntity(Long id) {
        return cuentaRepository.findById(id)
                .orElseThrow(() -> new CuentaNoEncontradaException(id));
    }

    private String generarNumeroCuenta(Cliente cliente, TipoCuenta tipo) {
        String prefijo = tipo == TipoCuenta.AHORROS ? "AH" : "CT";
        String año = String.valueOf(Year.now().getValue());
        String clienteId = String.format("%05d", cliente.getId());
        String aleatorio = String.format("%04d", new Random().nextInt(10000));

        return String.format("%s-%s-%s-%s", prefijo, año, clienteId, aleatorio);
    }

    private CuentaResponse convertToDTO(Cuenta cuenta) {
        return CuentaResponse.builder()
                .id(cuenta.getId())
                .numeroCuenta(cuenta.getNumeroCuenta())
                .tipoCuenta(cuenta.getTipoCuenta().name())
                .saldo(cuenta.getSaldo())
                .moneda(cuenta.getMoneda())
                .estado(cuenta.getEstado().name())
                .clienteId(cuenta.getCliente().getId())
                .clienteNombre(cuenta.getCliente().getNombre())
                .fechaApertura(cuenta.getFechaApertura())
                .build();
    }
}
