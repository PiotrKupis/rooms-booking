package com.roomsbooking.backend.exception.dto;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;
import lombok.Value;

/**
 * Class responsible for keeping a dto of application errors.
 */
@Value
public class ErrorResponse {

    LocalDateTime dateTime;
    Set<SingleError> errors;

    public ErrorResponse(Set<SingleError> errors) {
        this.errors = errors;
        this.dateTime = LocalDateTime.now();
    }

    /**
     * Method responsible for creating a data transfer object with single application error.
     *
     * @param errorKey error key
     * @param message  description of an error
     * @return {@link ErrorResponse} object
     */
    public static ErrorResponse singleError(String errorKey, String message) {
        return new ErrorResponse(Collections.singleton(new SingleError(errorKey, message)));
    }

    /**
     * Method responsible for creating a data transfer object with multiple application errors.
     *
     * @param errors collection of {@link SingleError} objects
     * @return {@link ErrorResponse} object
     */
    public static ErrorResponse multipleErrors(Set<SingleError> errors) {
        return new ErrorResponse(errors);
    }
}
