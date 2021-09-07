package com.roomsbooking.backend.exception.core;

import com.roomsbooking.backend.exception.ErrorKey;

/**
 * Class responsible for creating conflict exception.
 */
public class ConflictException extends BaseException {

    private ConflictException(String message) {
        super(message, ErrorKey.CONFLICT_ERROR);
    }

    private ConflictException(String message, String errorKey) {
        super(message, errorKey);
    }

    public static ConflictException conflict(String message) {
        return new ConflictException(message);
    }

    public static ConflictException uniqueField(String field) {
        return new ConflictException(String.format("Field %s must be unique", field),
            ErrorKey.UNIQUE_VALUE_ERROR);
    }
}
