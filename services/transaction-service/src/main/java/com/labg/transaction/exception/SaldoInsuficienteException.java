package com.labg.transaction.exception;

import java.math.BigDecimal;

public class SaldoInsuficienteException extends TransaccionException{
    public SaldoInsuficienteException(Long cuentaId, BigDecimal saldoActual, BigDecimal montoRequerido) {
        super(String.format("Saldo insuficiente en cuenta %d. Saldo: $%.2f, Requerido: $%.2f",
                cuentaId, saldoActual, montoRequerido));
    }
}
