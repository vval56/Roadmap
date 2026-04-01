package com.example.roadmap.exception;

public class BusinessRuleViolationException extends RuntimeException {

  public BusinessRuleViolationException(String message) {
    super(message);
  }
}
