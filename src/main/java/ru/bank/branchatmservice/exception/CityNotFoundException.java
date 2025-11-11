package ru.bank.branchatmservice.exception;

public class CityNotFoundException extends RuntimeException {
  public CityNotFoundException(String message) {
    super(message);
  }
}
