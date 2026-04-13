package com.labg.account.controller;

import com.labg.account.model.dtos.request.ActualizacionSaldoRequest;
import com.labg.account.model.dtos.request.CrearCuentaRequest;
import com.labg.account.model.dtos.response.CuentaResponse;
import com.labg.account.model.dtos.response.ValidacionFondosResponse;
import com.labg.account.service.CuentaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cuentas")
@RequiredArgsConstructor
@Tag(name = "Cuentas", description = "Endpoints para gestion de cuentas bancarias")
public class CuentaController {
    private final CuentaService cuentaService;

    @PostMapping
    @Operation(summary = "Crear nueva cuenta", description = "Abre una nueva cuenta bancaria para un cliente")
    public ResponseEntity<Map<String, Object>> crearCuenta(@Valid @RequestBody CrearCuentaRequest request) {
        CuentaResponse cuenta = cuentaService.crearCuenta(request);

        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Cuenta creada exitosamente");
        response.put("cuenta", cuenta);

        return ResponseEntity.created(URI.create("/api/cuentas/" + cuenta.getId()))
                .body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener cuenta por ID", description = "Retorna los datos de una cuenta específica")
    public ResponseEntity<CuentaResponse> obtenerCuenta(@PathVariable Long id) {
        CuentaResponse cuenta = cuentaService.obtenerCuentaPorId(id);
        return ResponseEntity.ok(cuenta);
    }

    @GetMapping("/numero/{numeroCuenta}")
    @Operation(summary = "Obtener cuenta por numero de cuenta", description = "Retorna los datos de una cuenta específica")
    public ResponseEntity<CuentaResponse> obtenerCuentaPorNumero(@PathVariable String numeroCuenta) {
        CuentaResponse cuenta = cuentaService.obtenerCuentaPorNumero(numeroCuenta);
        return ResponseEntity.ok(cuenta);
    }

    @GetMapping("/cliente/{clienteId}")
    @Operation(summary = "Listar cuentas de un cliente", description = "Retorna todas las cuentas de un cliente")
    public ResponseEntity<List<CuentaResponse>> listarCuentasPorCliente(@PathVariable Long clienteId) {
        List<CuentaResponse> cuentas = cuentaService.listarCuentasPorCliente(clienteId);
        return ResponseEntity.ok(cuentas);
    }

    @GetMapping("/{id}/saldo")
    @Operation(summary = "Consultar saldo", description = "Retorna el saldo actual de una cuenta")
    public ResponseEntity<Map<String, Object>> consultarSaldo(@PathVariable Long id) {
        BigDecimal saldo = cuentaService.consultarSaldo(id);

        Map<String, Object> response = new HashMap<>();
        response.put("cuentaId", id);
        response.put("saldo", saldo);
        response.put("moneda", "USD");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/acreditar")
    @Operation(summary = "Acreditar saldo a cuenta", description = "Se anexa un credito a la cuenta")
    public ResponseEntity<CuentaResponse> acreditarCuenta(
            @PathVariable Long id,
            @RequestBody ActualizacionSaldoRequest request) {
        CuentaResponse cuenta = cuentaService.acreditarCuenta(id, request.getMonto(), request.getTransaccionId());
        return ResponseEntity.ok(cuenta);
    }

    @PostMapping("/{id}/debitar")
    @Operation(summary = "Debitar saldo a cuenta", description = "Se anexa un debito a la cuenta")
    public ResponseEntity<CuentaResponse> debitarCuenta(
            @PathVariable Long id,
            @RequestBody ActualizacionSaldoRequest request) {
        CuentaResponse cuenta = cuentaService.debitarCuenta(id, request.getMonto(), request.getTransaccionId());
        return ResponseEntity.ok(cuenta);
    }

    @GetMapping("/{id}/validar-fondos")
    @Operation(summary = "Validar fondos de cuenta", description = "Retorna si la cuenta tiene el saldo suficiente para una transacción")
    public ResponseEntity<ValidacionFondosResponse> validarFondos(
            @PathVariable Long id,
            @RequestParam BigDecimal monto) {
        ValidacionFondosResponse response = cuentaService.validarFondos(id, monto);
        return ResponseEntity.ok(response);
    }
}
