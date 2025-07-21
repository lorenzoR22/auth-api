package com.example.auth_api.Exceptions.Customs;

public class UserAlreadyExistsException extends RuntimeException{
    public UserAlreadyExistsException(String emailOrUsername) {
        super("Ya existe un usuario con: '"+emailOrUsername+"'");
    }

}
