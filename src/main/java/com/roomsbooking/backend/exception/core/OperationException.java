package com.roomsbooking.backend.exception.core;

import com.roomsbooking.backend.exception.ErrorKey;

/**
 * Class responsible for creating operation exception.
 */
public class OperationException extends BaseException {

    private OperationException(String message) {
        super(message, ErrorKey.OPERATION_ERROR);
    }

    public static OperationException operationError(String message) {
        return new OperationException(message);
    }
}
