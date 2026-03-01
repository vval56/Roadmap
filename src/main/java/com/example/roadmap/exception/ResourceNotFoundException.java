package com.example.roadmap.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


/**
 * ResourceNotFoundException component.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

  
  public ResourceNotFoundException(String message) {
    super(message);
  }
}
