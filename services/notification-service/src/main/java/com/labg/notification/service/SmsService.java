package com.labg.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmsService {
    public void enviarSms(String telefono, String mensaje) {
        log.info("Enviando SMS a: {} - Mensaje: {}", telefono, mensaje);

        try {
            log.info("SMS enviado (simulado) a: {}", telefono);
        } catch (Exception e) {
            log.error("Error enviando SMS: {}", e.getMessage());
        }
    }
}
