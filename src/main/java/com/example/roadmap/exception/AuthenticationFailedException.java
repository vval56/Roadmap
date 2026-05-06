package com.example.roadmap.exception;

public class AuthenticationFailedException extends RuntimeException {

  public AuthenticationFailedException(String message) {
    super(message);
  }
}
