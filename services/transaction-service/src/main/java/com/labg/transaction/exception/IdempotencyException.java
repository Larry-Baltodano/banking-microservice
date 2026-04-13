package com.labg.transaction.exception;

public class IdempotencyException extends TransaccionException {
    public IdempotencyException(String idempotencyKey) {
        super("La transacción con clave " + idempotencyKey + " ya fue procesada");
    }
}
