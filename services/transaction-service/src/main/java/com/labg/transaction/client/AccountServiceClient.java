package com.labg.transaction.client;

import com.labg.transaction.model.dto.request.ActualizacionSaldoRequest;
import com.labg.transaction.model.dto.response.ValidacionFondosResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@FeignClient(name = "account-service", url = "${account-service.url}")
public interface AccountServiceClient {
    @GetMapping("/cuentas/{id}/validar-fondos")
    ValidacionFondosResponse validarFondos(
            @PathVariable("id") Long cuentaId,
            @RequestParam("monto")BigDecimal monto
    );

    @PostMapping("/cuentas/{id}/debitar")
    void debitarCuenta(
            @PathVariable("id") Long cuentaId,
            @RequestBody ActualizacionSaldoRequest request
    );

    @PostMapping("/cuentas/{id}/acreditar")
    void acreditarCuenta(
            @PathVariable("id") Long cuentaId,
            @RequestBody ActualizacionSaldoRequest request
    );

    @GetMapping("/cuentas/{id}/saldo")
    BigDecimal consultarSaldo(@PathVariable("id") Long cuentaId);
}
