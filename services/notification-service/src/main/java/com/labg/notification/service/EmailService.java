package com.labg.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    private final JavaMailSender mailSender;

    public void enviarEmail(String destino, String asunto, String cuerpo) {
        log.info("Enviando email a: {} - Asunto: {}", destino, asunto);

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(destino);
            message.setSubject(asunto);
            message.setText(cuerpo);
            message.setFrom("notificaciones@bank.com");

            //mailSender.send(message);

            log.info("Email enviado (simulado) a: {}", destino);
        } catch (Exception e) {
            log.error("Error enviando email: {}", e.getMessage());
        }
    }
}
