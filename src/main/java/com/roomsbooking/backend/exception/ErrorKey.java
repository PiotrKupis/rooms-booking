package com.roomsbooking.backend.exception;

/**
 * Class responsible for keeping error keys.
 */
public class ErrorKey {

    public static final String NOT_FOUND_ERROR = "error.general.not.found";
    public static final String NOT_FOUND_USER_ERROR = "error.user.not.found";
    public static final String NOT_FOUND_RESORT_ERROR = "error.resort.not.found";
    public static final String NOT_FOUND_ROOM_ERROR = "error.room.not.found";

    public static final String CRITICAL_ERROR = "error.general.critical";
    public static final String CRITICAL_JWT_KEYSTORE_ERROR = "error.jwt.keystore";
    public static final String CRITICAL_PHOTO_PROCESSING_ERROR = "error.photo.processing";
    public static final String STRIPE_PAYMENT_FAILED = "error.credit.card.payment.failed";

    public static final String OPERATION_ERROR = "error.general.operation";
    public static final String OPERATION_PASSWORDS_DO_NOT_MATCH_ERROR = "error.passwords.do.not.match";
    public static final String OPERATION_ARGUMENT_VALIDATION_ERROR = "error.validation.argument";
    public static final String OPERATION_UNEXPECTED_VALUE_ERROR = "error.unexpected.value";
    public static final String OPERATION_JWT_EXPIRED_ERROR = "error.jwt.expired";
    public static final String OPERATION_BAD_CREDENTIALS_ERROR = "error.bad.credentials";
    public static final String OPERATION_ROOM_EQUIPMENT_ERROR = "error.room.equipment";
    public static final String OPERATION_INCORRECT_FORMAT_ERROR = "error.incorrect.format";
    public static final String OPERATION_INCORRECT_RANGE_ERROR = "error.incorrect.range";

    public static final String CONFLICT_ERROR = "error.general.conflict";
    public static final String CONFLICT_EMAIL_ALREADY_TAKEN_ERROR = "error.email.already.taken";
    public static final String CONFLICT_RESORT_NAME_ALREADY_TAKEN_ERROR = "error.resort.name.already.taken";
    public static final String CONFLICT_UNIQUE_VALUE_ERROR = "error.unique.value";
    public static final String CONFLICT_ROOM_NUMBER_ALREADY_TAKEN_ERROR = "error.room.number.already.taken";
    public static final String CONFLICT_ROOM_UNREALIZED_RESERVATIONS = "error.room.unrealized.reservations";
    public static final String CONFLICT_UNAVAILABLE_TIME_PERIOD_ERROR = "error.unavailable.time.period";
    public static final String CONFLICT_PHOTO_POSITION_TAKEN_ERROR = "error.photo.position.taken";

    public static final String DB_CONNECTION_ERROR = "error.db.connection";
    public static final String DB_CONSTRAINT_VIOLATION_ERROR = "error.db.constraint.violation";

    private ErrorKey() {
    }
}
