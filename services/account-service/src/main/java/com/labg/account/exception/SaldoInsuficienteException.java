package com.labg.account.exception;

import java.math.BigDecimal;

public class SaldoInsuficienteException extends RuntimeException {
    public SaldoInsuficienteException(Long cuentaId, BigDecimal saldo, BigDecimal monto) {
        super(String.format("Saldo insuficiente en cuenta %d. Saldo actual: $%.2f, Monto requerido: $%.2f",
                cuentaId, saldo, monto));
    }
}
