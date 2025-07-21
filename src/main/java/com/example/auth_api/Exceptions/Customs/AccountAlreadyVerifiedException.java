package com.example.auth_api.Exceptions.Customs;

public class AccountAlreadyVerifiedException extends RuntimeException{
    public AccountAlreadyVerifiedException(String email) {
        super("Ya se encuentra verificada la cuenta: "+email);
    }
}
