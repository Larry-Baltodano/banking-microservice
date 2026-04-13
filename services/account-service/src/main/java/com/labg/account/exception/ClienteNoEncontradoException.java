package com.labg.account.exception;

public class ClienteNoEncontradoException extends RuntimeException {
    public ClienteNoEncontradoException(Long id) {
        super("Cliente no encontrado con ID: " + id);
    }

    public ClienteNoEncontradoException(String email) {
        super("Cliente no encontrado con email: " + email);
    }
}
