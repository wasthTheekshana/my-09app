package com.example.airline.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // A more generic handler for unexpected runtime exceptions
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        logger.error("An unexpected runtime error occurred", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + ex.getMessage());
    }

    // Handlers for specific authentication exceptions (using RuntimeException placeholders for now)
    @ExceptionHandler({UserAlreadyExistsException.class}) // Replace RuntimeException with UserAlreadyExistsException when created
    public ResponseEntity<String> handleUserAlreadyExistsException(RuntimeException ex) {
        logger.warn("User already exists: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler({InvalidCredentialsException.class, InvalidTokenException.class}) // Replace RuntimeException with specific exceptions when created
    public ResponseEntity<String> handleAuthenticationException(RuntimeException ex) {
        logger.warn("Authentication error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }

    // Add more specific exception handlers as needed
    // @ExceptionHandler(UserNotFoundException.class)
    // public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException ex) {
    //     return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    // }

    // @ExceptionHandler(InvalidTokenException.class)
}