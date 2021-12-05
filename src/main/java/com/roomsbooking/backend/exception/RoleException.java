package com.roomsbooking.backend.exception;

import com.roomsbooking.backend.exception.core.BaseException;
import com.roomsbooking.backend.exception.core.CriticalException;
import com.roomsbooking.backend.exception.core.NotFoundException;

public class RoleException extends BaseException {

    protected RoleException(BaseException exception) {
        super(exception, exception.getErrorKey());
    }

    public static RoleException defaultRoleIssue() {
        return new RoleException(CriticalException.critical("Problem with default user role"));
    }

    public static RoleException roleNotFound(String role) {
        return new RoleException(NotFoundException.notFound("Not found role: " + role));
    }
}
