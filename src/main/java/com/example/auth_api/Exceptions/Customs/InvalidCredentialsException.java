package com.example.auth_api.Exceptions.Customs;

public class InvalidCredentialsException extends RuntimeException{
    public InvalidCredentialsException() {
        super("El email o la contrasena son incorrectos.");
    }
}
