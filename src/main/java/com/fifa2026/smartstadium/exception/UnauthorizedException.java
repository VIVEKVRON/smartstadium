package com.fifa2026.smartstadium.exception;

/**
 * Exception thrown when user is unauthorized.
 */
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
