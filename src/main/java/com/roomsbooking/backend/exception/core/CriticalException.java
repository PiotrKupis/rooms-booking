package com.roomsbooking.backend.exception.core;

import com.roomsbooking.backend.exception.ErrorKey;

/**
 * Class responsible for creating critical exception.
 */
public class CriticalException extends BaseException {

    private CriticalException(String message) {
        super(message, ErrorKey.CRITICAL_ERROR);
    }

    public static CriticalException critical(String message) {
        return new CriticalException(message);
    }
}
