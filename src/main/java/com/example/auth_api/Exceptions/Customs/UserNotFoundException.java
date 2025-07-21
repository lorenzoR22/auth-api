package com.example.auth_api.Exceptions.Customs;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(String email) {
        super("No se encontro usuario con el email: "+email);
    }
}
