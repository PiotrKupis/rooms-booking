package com.roomsbooking.backend.exception;

import com.roomsbooking.backend.exception.core.BaseException;
import com.roomsbooking.backend.exception.core.ConflictException;
import com.roomsbooking.backend.exception.core.OperationException;

public class ReservationException extends BaseException {

    protected ReservationException(BaseException exception) {
        super(exception, exception.getErrorKey());
    }

    public static ReservationException unavailableTimePeriod() {
        return new ReservationException(
            ConflictException.conflict("Passed time period is unavailable",
                ErrorKey.CONFLICT_UNAVAILABLE_TIME_PERIOD_ERROR));
    }

    public static ReservationException incorrectDateFormat() {
        return new ReservationException(
            OperationException.operationError("Incorrect date format",
                ErrorKey.OPERATION_INCORRECT_FORMAT_ERROR));
    }

    public static ReservationException incorrectDateRange() {
        return new ReservationException(
            OperationException.operationError("Incorrect date range",
                ErrorKey.OPERATION_INCORRECT_RANGE_ERROR));
    }

    public static ReservationException paymentFailed() {
        return new ReservationException(
            OperationException.operationError("Payment failed", ErrorKey.STRIPE_PAYMENT_FAILED));
    }
}
