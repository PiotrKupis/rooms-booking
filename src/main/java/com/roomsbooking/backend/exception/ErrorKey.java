package com.roomsbooking.backend.exception;

/**
 * Class responsible for keeping error keys.
 */
public class ErrorKey {

    public static final String NOT_FOUND_ERROR = "error.general.not.found";
    public static final String CRITICAL_ERROR = "error.general.critical";
    public static final String OPERATION_ERROR = "error.general.operation";
    public static final String CONFLICT_ERROR = "error.general.conflict";

    public static final String OPERATION_PASSWORDS_DO_NOT_MATCH_ERROR = "error.passwords.do.not.match";
    public static final String CONFLICT_EMAIL_ALREADY_TAKEN_ERROR = "error.email.already.taken";

    public static final String UNIQUE_VALUE_ERROR = "error.unique.value";
    public static final String ARGUMENT_VALIDATION_ERROR = "error.validation.argument";
    public static final String UNEXPECTED_VALUE_ERROR = "error.unexpected.value";

    public static final String DB_CONNECTION_ERROR = "error.db.connection";
    public static final String DB_CONSTRAINT_VIOLATION_ERROR = "error.db.constraint.violation";

    private ErrorKey() {
    }
}
