package com.roomsbooking.backend.exception.core;

import com.roomsbooking.backend.exception.ErrorKey;

/**
 * Class responsible for creating not found exception.
 */
public class NotFoundException extends BaseException {

    private NotFoundException(String message) {
        super(message, ErrorKey.NOT_FOUND_ERROR);
    }

    private NotFoundException(String message, String errorKey) {
        super(message, errorKey);
    }

    public static NotFoundException notFound(String message) {
        return new NotFoundException(message);
    }

    public static NotFoundException notFound(String message, String errorKey) {
        return new NotFoundException(message, errorKey);
    }
}
