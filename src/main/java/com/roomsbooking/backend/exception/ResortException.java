package com.roomsbooking.backend.exception;

import com.roomsbooking.backend.exception.core.BaseException;
import com.roomsbooking.backend.exception.core.ConflictException;

public class ResortException extends BaseException {

    protected ResortException(BaseException exception) {
        super(exception, exception.getErrorKey());
    }

    public static ResortException resortNameAlreadyTaken() {
        return new ResortException(ConflictException.conflict("Resort name already taken",
            ErrorKey.CONFLICT_RESORT_NAME_ALREADY_TAKEN_ERROR));
    }
}
