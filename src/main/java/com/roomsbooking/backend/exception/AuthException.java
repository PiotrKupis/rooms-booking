package com.roomsbooking.backend.exception;

import com.roomsbooking.backend.exception.core.BaseException;
import com.roomsbooking.backend.exception.core.ConflictException;
import com.roomsbooking.backend.exception.core.CriticalException;
import com.roomsbooking.backend.exception.core.OperationException;

public class AuthException extends BaseException {

    protected AuthException(BaseException exception) {
        super(exception, exception.getErrorKey());
    }

    public static AuthException passwordsDoNotMatch() {
        return new AuthException(
            OperationException.operationError("Passwords do not match",
                ErrorKey.OPERATION_PASSWORDS_DO_NOT_MATCH_ERROR));
    }

    public static AuthException emailAlreadyTaken() {
        return new AuthException(
            ConflictException.conflict("Email is already taken",
                ErrorKey.CONFLICT_EMAIL_ALREADY_TAKEN_ERROR));
    }

    public static AuthException rolesIssue() {
        return new AuthException(
            CriticalException.critical("Problem with default user roles"));
    }

    public static AuthException jwtKeystoreError() {
        return new AuthException(
            CriticalException.critical("Problem with jwt keystore",
                ErrorKey.CRITICAL_JWT_KEYSTORE_ERROR));
    }
}
