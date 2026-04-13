package com.labg.account.exception;

public class DuplicateEmailException extends RuntimeException{
    public DuplicateEmailException(String email) {
        super("El email '" + email + "' ya está registrado");
    }
}
