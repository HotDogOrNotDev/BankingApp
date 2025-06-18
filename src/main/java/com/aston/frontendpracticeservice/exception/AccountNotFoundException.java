package com.aston.frontendpracticeservice.exception;

public class AccountNotFoundException extends RuntimeException {

    public AccountNotFoundException() {
        super();
    }

    public AccountNotFoundException(String message) {
        super("Account with account number " + message + " not found");
    }

}
