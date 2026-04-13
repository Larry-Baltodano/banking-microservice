package com.labg.gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {
    @GetMapping("/account")
    public ResponseEntity<Map<String, Object>> accountFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "FALLBACK");
        response.put("service", "account-service");
        response.put("message", "Servicio de cuentas no disponible");
        response.put("timestamp", LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    @GetMapping("/transaction")
    public ResponseEntity<Map<String, Object>> transactionFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "FALLBACK");
        response.put("service", "transaction-service");
        response.put("message", "Servicio de transacciones no disponible");
        response.put("timestamp", LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }
}
