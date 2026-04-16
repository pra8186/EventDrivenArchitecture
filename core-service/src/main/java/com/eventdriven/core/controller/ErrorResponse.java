package com.eventdriven.core.controller;

import java.time.LocalDateTime;

/**
 * Consistent JSON error response returned by {@link GlobalExceptionHandler}.
 *
 * <p>Shape: {@code { "error": "...", "status": 400, "timestamp": "..." }}
 */
public class ErrorResponse {

    private final String error;
    private final int status;
    private final LocalDateTime timestamp;

    public ErrorResponse(String error, int status) {
        this.error = error;
        this.status = status;
        this.timestamp = LocalDateTime.now();
    }

    public String getError() { return error; }
    public int getStatus() { return status; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
