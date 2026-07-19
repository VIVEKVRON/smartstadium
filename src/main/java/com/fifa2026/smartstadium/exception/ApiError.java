package com.fifa2026.smartstadium.exception;

import java.time.Instant;
import java.util.List;

/**
 * Standardized API Error response.
 */
public record ApiError(
    Instant timestamp,
    int status,
    String message,
    String path,
    List<String> errors
) {}
