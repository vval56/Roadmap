package com.example.roadmap.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ApiErrorResponse> handleNotFound(
      ResourceNotFoundException ex, HttpServletRequest request) {
    return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI(), List.of());
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ApiErrorResponse> handleDataIntegrityViolation(
      DataIntegrityViolationException ex, HttpServletRequest request) {
    return buildResponse(HttpStatus.CONFLICT,
        "Request conflicts with existing data or database constraints",
        request.getRequestURI(), List.of());
  }

  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<ApiErrorResponse> handleNoResourceFound(
      NoResourceFoundException ex, HttpServletRequest request) {
    return buildResponse(HttpStatus.NOT_FOUND, "Requested resource was not found",
        request.getRequestURI(), List.of());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex, HttpServletRequest request) {
    List<ApiFieldError> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
        .map(this::toApiFieldError)
        .toList();
    return buildResponse(HttpStatus.BAD_REQUEST, "Validation failed",
        request.getRequestURI(), fieldErrors);
  }

  @ExceptionHandler(HandlerMethodValidationException.class)
  public ResponseEntity<ApiErrorResponse> handleHandlerMethodValidation(
      HandlerMethodValidationException ex, HttpServletRequest request) {
    List<ApiFieldError> fieldErrors = new ArrayList<>();
    ex.getParameterValidationResults().forEach(result ->
        result.getResolvableErrors().forEach(error ->
            fieldErrors.add(new ApiFieldError(
                result.getMethodParameter().getParameterName(),
                error.getDefaultMessage(),
                null
            )))
    );
    return buildResponse(HttpStatus.BAD_REQUEST, "Validation failed",
        request.getRequestURI(), fieldErrors);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ApiErrorResponse> handleConstraintViolation(
      ConstraintViolationException ex, HttpServletRequest request) {
    List<ApiFieldError> fieldErrors = ex.getConstraintViolations().stream()
        .map(violation -> new ApiFieldError(
            violation.getPropertyPath().toString(),
            violation.getMessage(),
            violation.getInvalidValue()))
        .toList();
    return buildResponse(HttpStatus.BAD_REQUEST, "Validation failed",
        request.getRequestURI(), fieldErrors);
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ApiErrorResponse> handleTypeMismatch(
      MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
    String expectedType = ex.getRequiredType() == null ? "unknown" : ex.getRequiredType().getSimpleName();
    String message = "Parameter '%s' must be of type %s".formatted(ex.getName(), expectedType);
    List<ApiFieldError> fieldErrors = List.of(new ApiFieldError(ex.getName(), message, ex.getValue()));
    return buildResponse(HttpStatus.BAD_REQUEST, "Invalid request parameter",
        request.getRequestURI(), fieldErrors);
  }

  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<ApiErrorResponse> handleMissingParameter(
      MissingServletRequestParameterException ex, HttpServletRequest request) {
    List<ApiFieldError> fieldErrors = List.of(new ApiFieldError(
        ex.getParameterName(),
        "Required request parameter is missing",
        null
    ));
    return buildResponse(HttpStatus.BAD_REQUEST, "Missing request parameter",
        request.getRequestURI(), fieldErrors);
  }

  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<ApiErrorResponse> handleMethodNotSupported(
      HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
    String message = "HTTP method '%s' is not supported for this endpoint".formatted(ex.getMethod());
    return buildResponse(HttpStatus.METHOD_NOT_ALLOWED, message, request.getRequestURI(), List.of());
  }

  @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
  public ResponseEntity<ApiErrorResponse> handleMediaTypeNotSupported(
      HttpMediaTypeNotSupportedException ex, HttpServletRequest request) {
    String mediaType = ex.getContentType() == null ? "unknown" : ex.getContentType().toString();
    String message = "Content-Type '%s' is not supported".formatted(mediaType);
    return buildResponse(HttpStatus.UNSUPPORTED_MEDIA_TYPE, message,
        request.getRequestURI(), List.of());
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ApiErrorResponse> handleNotReadable(
      HttpMessageNotReadableException ex, HttpServletRequest request) {
    return buildResponse(HttpStatus.BAD_REQUEST, "Malformed JSON request",
        request.getRequestURI(), List.of());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiErrorResponse> handleUnexpected(
      Exception ex, HttpServletRequest request) {
    if (isConflictException(ex)) {
      return buildResponse(HttpStatus.CONFLICT,
          "Request conflicts with existing data or database constraints",
          request.getRequestURI(), List.of());
    }
    log.error("Unhandled exception for path={}", request.getRequestURI(), ex);
    return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected server error",
        request.getRequestURI(), List.of());
  }

  private ApiFieldError toApiFieldError(FieldError error) {
    return new ApiFieldError(error.getField(), error.getDefaultMessage(), error.getRejectedValue());
  }

  private ResponseEntity<ApiErrorResponse> buildResponse(HttpStatus status, String message,
                                                         String path, List<ApiFieldError> fieldErrors) {
    ApiErrorResponse body = new ApiErrorResponse(
        OffsetDateTime.now(),
        status.value(),
        status.getReasonPhrase(),
        message,
        path,
        fieldErrors
    );
    return ResponseEntity.status(status).body(body);
  }

  private boolean isConflictException(Throwable throwable) {
    Throwable current = throwable;
    while (current != null) {
      if (current instanceof DataIntegrityViolationException) {
        return true;
      }

      String className = current.getClass().getName();
      if ("org.hibernate.exception.ConstraintViolationException".equals(className)
          || "org.postgresql.util.PSQLException".equals(className)) {
        return true;
      }

      String message = current.getMessage();
      if (message != null) {
        String normalized = message.toLowerCase();
        if (normalized.contains("duplicate key")
            || normalized.contains("unique constraint")
            || normalized.contains("violates unique")
            || normalized.contains("constraint [")) {
          return true;
        }
      }
      current = current.getCause();
    }
    return false;
  }
}
