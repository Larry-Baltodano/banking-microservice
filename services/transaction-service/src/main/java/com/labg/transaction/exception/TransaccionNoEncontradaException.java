package com.labg.transaction.exception;

public class TransaccionNoEncontradaException extends TransaccionException {
    public TransaccionNoEncontradaException(Long id) {
        super("Transacción no encontrada con ID: " + id);
    }

    public TransaccionNoEncontradaException(String idempotencyKey) {
        super("Transacción no encontrada con clave de idempotencia: " + idempotencyKey);
    }
}
