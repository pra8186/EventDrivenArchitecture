package com.eventdriven.controller;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * Global exception handler that catches exceptions thrown from any
 * {@code @RestController} and returns a consistent {@link ErrorResponse}
 * JSON body with {@code error}, {@code status}, and {@code timestamp}.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Resource not found — 404.
     * Handles {@link NoSuchElementException} thrown by service layer
     * when a user, profile, state, or entry ID doesn't exist.
     */
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NoSuchElementException ex) {
        ErrorResponse body = new ErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND.value());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    /**
     * Validation errors — 400.
     * Handles {@link MethodArgumentNotValidException} triggered by {@code @Valid}
     * on request DTOs when {@code @NotBlank}, {@code @Email}, {@code @Size}, etc. fail.
     * Collects all field errors into a single comma-separated message.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .sorted()
                .collect(Collectors.joining(", "));
        ErrorResponse body = new ErrorResponse(message, HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * Duplicate key / constraint violation — 409 Conflict.
     * Handles {@link DataIntegrityViolationException} thrown by JPA/Hibernate
     * when a unique constraint (email, SSN, user+year) is violated.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDuplicate(DataIntegrityViolationException ex) {
        String message = "Duplicate or constraint violation";
        Throwable root = ex.getRootCause();
        if (root != null && root.getMessage() != null) {
            String detail = root.getMessage();
            if (detail.contains("email")) {
                message = "A user with this email already exists";
            } else if (detail.contains("ssn_encrypted")) {
                message = "A user with this SSN already exists";
            } else if (detail.contains("idx_tax_profiles_user_year")) {
                message = "A tax profile for this user and year already exists";
            } else {
                message = "Duplicate record: " + detail.substring(0, Math.min(detail.length(), 200));
            }
        }
        ErrorResponse body = new ErrorResponse(message, HttpStatus.CONFLICT.value());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    /**
     * Invalid enum or argument — 400.
     * Handles {@link IllegalArgumentException} thrown when parsing invalid
     * enum values (e.g. bad FilingStatus or WorkType).
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleBadArgument(IllegalArgumentException ex) {
        ErrorResponse body = new ErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * Catch-all for unhandled exceptions — 500.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        ErrorResponse body = new ErrorResponse(
                "Internal server error: " + ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
