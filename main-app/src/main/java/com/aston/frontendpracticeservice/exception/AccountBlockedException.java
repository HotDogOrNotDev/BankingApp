package com.aston.frontendpracticeservice.exception;

public class AccountBlockedException extends RuntimeException {

    public AccountBlockedException() {
        super("One of the accounts is blocked");
    }

}
