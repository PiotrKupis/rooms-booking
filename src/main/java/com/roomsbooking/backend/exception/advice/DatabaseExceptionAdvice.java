package com.roomsbooking.backend.exception.advice;

import com.roomsbooking.backend.exception.ErrorKey;
import com.roomsbooking.backend.exception.core.BaseException;
import com.roomsbooking.backend.exception.core.ConflictException;
import com.roomsbooking.backend.exception.dto.ErrorResponse;
import java.util.List;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.JDBCConnectionException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Class responsible for handling database connection and data validation exceptions.
 */
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
class DatabaseExceptionAdvice {

    /**
     * Method responsible for handling SQL constraint violations.
     *
     * @param exception ConstraintViolationException thrown when arguments do not match sql
     *                  constraints
     * @return {@link ErrorResponse} object
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public ErrorResponse constraintViolation(ConstraintViolationException exception)
        throws BaseException {
        String constraint = exception.getConstraintName();
        if (exception.getMessage().contains("Duplicate")) {
            throw ConflictException.uniqueField(constraint);
        }
        return ErrorResponse.singleError(formatErrorKey(constraint),
            exception.getSQLException().getMessage());
    }

    /**
     * Method responsible for handling database connection exceptions.
     *
     * @param exception JDBCConnectionException thrown when database connection failed
     * @return {@link ErrorResponse} object
     */
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    @ExceptionHandler(JDBCConnectionException.class)
    public ErrorResponse databaseConnectionFailed(JDBCConnectionException exception) {
        return ErrorResponse.singleError(ErrorKey.DB_CONNECTION_ERROR,
            "Database connection failed");
    }

    private String formatErrorKey(String constraintName) {
        List<String> list = List.of(constraintName.split("\\."));
        String fieldName = list.get(list.size() - 1);
        return String.format("%s.%s", ErrorKey.DB_CONSTRAINT_VIOLATION_ERROR, fieldName);
    }
}

