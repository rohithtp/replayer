package com.example.replayer.api.controller;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Maps domain exceptions to HTTP error responses. */
@RestControllerAdvice
public class GlobalExceptionHandler {

  /** Maps validation errors to 400 Bad Request. */
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Map<String, String>> handleValidation(IllegalArgumentException ex) {
    return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
  }

  /** Maps out-of-range event index to 404 Not Found. */
  @ExceptionHandler(IndexOutOfBoundsException.class)
  public ResponseEntity<Map<String, String>> handleNotFound(IndexOutOfBoundsException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
  }
}
