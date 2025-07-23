package com.kamleads.management.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when an authenticated user attempts to access a resource
 * they are not authorized for. Maps to HTTP 403 Forbidden.
 * Note: For unauthenticated access, Spring Security's default 401 Unauthorized is used.
 */
@ResponseStatus(HttpStatus.FORBIDDEN) // Use FORBIDDEN for authenticated but unauthorized access
public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }

    public ForbiddenException(String message, Throwable cause) {
        super(message, cause);
    }
}