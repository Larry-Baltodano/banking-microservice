package com.labg.account.exception;

public class CuentaNoEncontradaException extends RuntimeException{
    public CuentaNoEncontradaException(Long id) {
        super("Cuenta no encontrada con ID: " + id);
    }

    public CuentaNoEncontradaException(String numeroCuenta) {
        super("Cuenta no encontrado con número: " + numeroCuenta);
    }
}
