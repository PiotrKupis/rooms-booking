package com.roomsbooking.backend.exception.core;

import com.roomsbooking.backend.exception.ErrorKey;

/**
 * Class responsible for creating not found exception.
 */
public class NotFoundException extends BaseException {

    private NotFoundException(String message) {
        super(message, ErrorKey.NOT_FOUND_ERROR);
    }

    public static NotFoundException notFound(String message) {
        return new NotFoundException(message);
    }
}
