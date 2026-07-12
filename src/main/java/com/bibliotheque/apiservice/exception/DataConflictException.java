package com.bibliotheque.apiservice.exception;

public class DataConflictException extends RuntimeException {
  public DataConflictException(String message) {
    super(message);
  }
}
