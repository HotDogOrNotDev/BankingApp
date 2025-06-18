package com.aston.frontendpracticeservice.exception;

public class NotEnoughMoneyException extends RuntimeException {

  public NotEnoughMoneyException() {
    super("Not enough money on the account");
  }

}