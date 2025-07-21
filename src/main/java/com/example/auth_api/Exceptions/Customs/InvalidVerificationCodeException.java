package com.example.auth_api.Exceptions.Customs;

public class InvalidVerificationCodeException extends RuntimeException{
    public InvalidVerificationCodeException() {
        super("El codigo de verificacion es invalido.");
    }
}
