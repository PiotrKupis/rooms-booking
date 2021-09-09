package com.roomsbooking.backend.exception.core;

import lombok.Getter;

/**
 * Base class for custom exceptions.
 */
@Getter
public class BaseException extends RuntimeException {

    private final String errorKey;

    protected BaseException(String message, String errorKey) {
        super(message);
        this.errorKey = errorKey;
    }

    protected BaseException(Throwable cause, String errorKey) {
        super(cause);
        this.errorKey = errorKey;
    }
}
