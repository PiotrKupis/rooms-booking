package com.roomsbooking.backend.exception.advice;

import com.roomsbooking.backend.exception.ErrorKey;
import com.roomsbooking.backend.exception.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Class responsible for handling default exceptions.
 */
@RestControllerAdvice
public class DefaultExceptionAdvice {

    /**
     * Method responsible for handling unknown exceptions.
     *
     * @param exception exception
     * @return {@link ErrorResponse} object
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorResponse exception(Exception exception) {
        return ErrorResponse.singleError(ErrorKey.CRITICAL_ERROR, exception.getMessage());
    }
}
