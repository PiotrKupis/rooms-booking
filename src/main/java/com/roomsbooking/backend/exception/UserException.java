package com.roomsbooking.backend.exception;

import com.roomsbooking.backend.exception.core.BaseException;
import com.roomsbooking.backend.exception.core.NotFoundException;

public class UserException extends BaseException {

    protected UserException(BaseException exception) {
        super(exception, exception.getErrorKey());
    }

    public static UserException userNotFound(String email) {
        return new UserException(
            NotFoundException.notFound("User with email: " + email + " not found",
                ErrorKey.NOT_FOUND_USER_ERROR));
    }
}
