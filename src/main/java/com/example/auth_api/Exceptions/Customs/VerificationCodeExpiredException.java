package com.example.auth_api.Exceptions.Customs;

public class VerificationCodeExpiredException extends RuntimeException{
    public VerificationCodeExpiredException() {
        super("El codigo de verificacion ya expiro.");
    }
}
