package com.aaron212.onlinelibrarymanagement.backend.exception;

public class UnauthorizedOperationException extends RuntimeException {
    public UnauthorizedOperationException(String message) {
        super(message);
    }

    public UnauthorizedOperationException(String operation, String reason) {
        super(String.format("Unauthorized operation: %s. Reason: %s", operation, reason));
    }
}