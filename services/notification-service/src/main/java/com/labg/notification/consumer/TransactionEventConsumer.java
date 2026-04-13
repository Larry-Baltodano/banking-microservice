package com.labg.notification.consumer;

import com.labg.notification.model.TransactionEvent;
import com.labg.notification.service.EmailService;
import com.labg.notification.service.SmsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransactionEventConsumer {
    private final EmailService emailService;
    private final SmsService smsService;

    @KafkaListener(topics = "transacciones-completadas", groupId = "notification-service-group")
    public void consumirTransaccionCompletada(TransactionEvent event) {
        log.info("-> Evento recibido - Transacción COMPLETADA: {}", event.getTransaccionId());

        String emailOrigen = "cliente" + event.getCuentaOrigenId() + "@bank.com";
        String asuntoOrigen = "Transferencia realizada - $" + event.getMonto();
        String cuerpoOrigen = String.format(
                "Estimado cliente,\n\nSe ha realizado una transferencia desde su cuenta:\n"+
                        "Monto: $%.2f\n" +
                        "Cuenta destino: %d\n" +
                        "Fecha: %s\n\nGracias por confiar en nosotros.",
                event.getMonto(), event.getCuentaDestinoId(), event.getFecha()
        );
        emailService.enviarEmail(emailOrigen, asuntoOrigen, cuerpoOrigen);

        String smsDestino = "Transferencia recibida de $" + event.getMonto();
        smsService.enviarSms("+50599999999", smsDestino);
    }

    @KafkaListener(topics = "transacciones-fallidas", groupId = "notification-service-group")
    public void consumirTransaccionFallida(TransactionEvent event) {
        log.info("-X Evento recibido - Transacción FALLIDA: {}", event.getTransaccionId());

        String emailOrigen = "cliente" + event.getCuentaOrigenId() + "@bank.com";
        String asunto = "Transferencia fallida";
        String cuerpo = String.format(
                "Estimado cliente, \n\nLa transferencia de $%.2f no pudo completarse.\n" +
                        "Motivo: %s\n\nPor favor, intente nuevamente.",
                event.getMonto(), event.getErrorMensaje()
        );
        emailService.enviarEmail(emailOrigen, asunto, cuerpo);
    }
}
