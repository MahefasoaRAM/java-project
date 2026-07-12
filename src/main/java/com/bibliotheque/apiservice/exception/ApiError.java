package com.bibliotheque.apiservice.exception;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiError {
  private LocalDateTime timestamp;
  private int status;
  private String message;
  private String error;

  public ApiError() {
    this.timestamp = LocalDateTime.now();
  }

  public ApiError(int status, String error, String message) {
    this();
    this.status = status;
    this.error = error;
    this.message = message;
  }

  public LocalDateTime getTimestamp() {
    return timestamp;
  }

  public int getStatus() {
    return status;
  }

  public String getMessage() {
    return message;
  }

  public String getError() {
    return error;
  }
}
