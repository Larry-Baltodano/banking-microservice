package com.labg.gateway.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@Tag(name = "Administración", description = "Endpoints exclusivos para administradores")
public class AdminController {
    @GetMapping("/dashboard")
    @Operation(summary = "Dashboard de administración")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> dashboard() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Bienvenido al panel de administración");
        response.put("services", Map.of(
                "account-service", "http://localhost:8081",
                "transaction-service", "http://localhost:8082",
                "notification-service", "http://localhost:8084"
        ));
        response.put("status", "all systems operational");
        return ResponseEntity.ok(response);
    }
}
