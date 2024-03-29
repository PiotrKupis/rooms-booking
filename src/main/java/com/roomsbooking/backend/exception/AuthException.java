package com.roomsbooking.backend.exception;

import com.roomsbooking.backend.exception.core.BaseException;
import com.roomsbooking.backend.exception.core.ConflictException;
import com.roomsbooking.backend.exception.core.CriticalException;
import com.roomsbooking.backend.exception.core.NotFoundException;
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

    public static AuthException jwtKeystoreError() {
        return new AuthException(
            CriticalException.critical("Problem with jwt keystore",
                ErrorKey.CRITICAL_JWT_KEYSTORE_ERROR));
    }

    public static AuthException jwtExpired() {
        return new AuthException(OperationException.operationError("JWT token has expired",
            ErrorKey.OPERATION_JWT_EXPIRED_ERROR));
    }

    public static AuthException badCredentials() {
        return new AuthException(OperationException.operationError("Bad credentials",
            ErrorKey.OPERATION_BAD_CREDENTIALS_ERROR));
    }

    public static AuthException notFoundRefreshToken() {
        return new AuthException(NotFoundException.notFound("Not found passed refresh token"));
    }
}
