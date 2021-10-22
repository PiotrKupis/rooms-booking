package com.roomsbooking.backend.exception;

import com.roomsbooking.backend.exception.core.BaseException;
import com.roomsbooking.backend.exception.core.ConflictException;
import com.roomsbooking.backend.exception.core.NotFoundException;

public class ResortException extends BaseException {

    protected ResortException(BaseException exception) {
        super(exception, exception.getErrorKey());
    }

    public static ResortException resortNameAlreadyTaken() {
        return new ResortException(ConflictException.conflict("Resort name already taken",
            ErrorKey.CONFLICT_RESORT_NAME_ALREADY_TAKEN_ERROR));
    }

    public static ResortException resortNotFound(String resortName) {
        return new ResortException(
            NotFoundException.notFound("Resort: " + resortName + " not found",
                ErrorKey.NOT_FOUND_RESORT_ERROR));
    }
}
