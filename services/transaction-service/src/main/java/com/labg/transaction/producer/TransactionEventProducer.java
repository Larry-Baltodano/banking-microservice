package com.labg.transaction.producer;

import com.labg.transaction.model.dto.response.TransactionEvent;
import com.labg.transaction.model.entity.Transaccion;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransactionEventProducer {
    private final KafkaTemplate<String, TransactionEvent> kafkaTemplate;
    private static final String TOPIC_COMPLETADAS = "transacciones-completadas";
    private static final String TOPIC_FALLIDAS = "transacciones-fallidas";

    public void publicarTransaccionCompletada(Transaccion transaccion) {
        TransactionEvent event = buildEvent(transaccion);

        kafkaTemplate.send(TOPIC_COMPLETADAS, event.getTransaccionId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Evento de transacción completada publicado: {}", event.getTransaccionId());
                    } else {
                        log.error("Error publicando evento de transacción completada: {}", ex.getMessage());
                    }
                });
    }

    public void publicarTransaccionFallida(Transaccion transaccion) {
        TransactionEvent event = buildEvent(transaccion);

        kafkaTemplate.send(TOPIC_FALLIDAS, event.getTransaccionId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Evento de transacción fallida publicado: {}", event.getTransaccionId());
                    } else {
                        log.error("Error publicando evento de transacción fallida: {}", ex.getMessage());
                    }
                });
    }

    private TransactionEvent buildEvent(Transaccion transaccion) {
        return TransactionEvent.builder()
                .transaccionId(transaccion.getId())
                .cuentaOrigenId(transaccion.getCuentaOrigenId())
                .cuentaDestinoId(transaccion.getCuentaDestinoId())
                .monto(transaccion.getMonto())
                .tipo(transaccion.getTipo())
                .estado(transaccion.getEstado())
                .descripcion(transaccion.getDescripcion())
                .fecha(transaccion.getFechaCompletada().toString() != null ?
                        transaccion.getFechaCompletada().toString() : transaccion.getFechaCreacion().toString())
                .errorMensaje(transaccion.getErrorMensaje())
                .build();
    }
}
