package com.labg.transaction.exception;

public class CuentaNoEncontradaException extends TransaccionException {
    public CuentaNoEncontradaException(Long cuentaId) {
        super("Cuenta no encontrada: " + cuentaId);
    }
}
