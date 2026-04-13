package com.labg.transaction.controller;

import com.labg.transaction.model.dto.request.TransferenciaRequest;
import com.labg.transaction.model.dto.response.TransferenciaResponse;
import com.labg.transaction.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/transacciones")
@RequiredArgsConstructor
@Tag(name = "Transacciones", description = "Endpoints para transferencias y consulta de transacciones")
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping("/transferencias")
    @Operation(summary = "Realizar transferencia", description = "Transfiere dinero entre cuentas bancarias")
    public ResponseEntity<TransferenciaResponse> realizarTransferencia(
            @Valid @RequestBody TransferenciaRequest request,
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey) {
        if (idempotencyKey == null || idempotencyKey.isEmpty()) {
            idempotencyKey = UUID.randomUUID().toString();
        }

        TransferenciaResponse response = transactionService.procesarTransferencia(request, idempotencyKey);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar estado de transacción", description = "Retorna el estado de una transferencia")
    public ResponseEntity<TransferenciaResponse> obtenerEstado(@PathVariable Long id) {
        TransferenciaResponse response = transactionService.obtenerEstadoTransaccion(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/idempotency/{key}")
    public ResponseEntity<TransferenciaResponse> obtenerPotIdempotencyKey(@PathVariable String key) {
        TransferenciaResponse response = transactionService.obtenerEstadoTransaccionPorIdempotencyKey(key);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/reversar")
    @Operation(summary = "Reversar transacción", description = "Revierte una transferencia previamente realizada")
    public ResponseEntity<Map<String, String>> reversarTransaccion(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        String motivo = request.getOrDefault("motivo", "Reverso solicitado");
        transactionService.revertirTransaccion(id, motivo);

        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "Transacción revertida exitosamente");
        response.put("transaccionId", id.toString());

        return ResponseEntity.ok(response);
    }
}
