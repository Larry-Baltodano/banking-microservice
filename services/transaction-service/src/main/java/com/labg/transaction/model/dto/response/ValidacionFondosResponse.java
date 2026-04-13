package com.labg.transaction.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidacionFondosResponse {
    private boolean valida;
    private boolean fondosSuficientes;
    private BigDecimal saldoDisponible;
    private String mensaje;
    private String estadoCuenta;

    public static ValidacionFondosResponse exitosa(BigDecimal saldo) {
        return ValidacionFondosResponse.builder()
                .valida(true)
                .fondosSuficientes(true)
                .saldoDisponible(saldo)
                .mensaje("OK")
                .build();
    }

    public static ValidacionFondosResponse saldoInsuficiente(BigDecimal saldo) {
        return ValidacionFondosResponse.builder()
                .valida(false)
                .fondosSuficientes(false)
                .saldoDisponible(saldo)
                .mensaje("Saldo insuficiente")
                .build();
    }

    public static ValidacionFondosResponse cuentaNoEncontrada() {
        return ValidacionFondosResponse.builder()
                .valida(false)
                .fondosSuficientes(false)
                .mensaje("Cuenta no encontrada")
                .build();
    }
}
