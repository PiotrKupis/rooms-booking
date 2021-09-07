package com.roomsbooking.backend.exception.advice;


import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonMappingException.Reference;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.ValueInstantiationException;
import com.roomsbooking.backend.exception.ErrorKey;
import com.roomsbooking.backend.exception.core.ConflictException;
import com.roomsbooking.backend.exception.core.CriticalException;
import com.roomsbooking.backend.exception.core.NotFoundException;
import com.roomsbooking.backend.exception.core.OperationException;
import com.roomsbooking.backend.exception.dto.ErrorResponse;
import com.roomsbooking.backend.exception.dto.SingleError;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Class responsible for handling application exceptions.
 */
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RestExceptionAdvice {

    /**
     * Method responsible for handling attempts to access to non-existing objects.
     *
     * @param exception {@link NotFoundException} object
     * @return {@link ErrorResponse} object
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public ErrorResponse notFound(NotFoundException exception) {
        return ErrorResponse.singleError(exception.getErrorKey(), exception.getMessage());
    }

    /**
     * Method responsible for handling critical exceptions.
     *
     * @param exception {@link CriticalException} object
     * @return {@link ErrorResponse} object
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(CriticalException.class)
    public ErrorResponse critical(CriticalException exception) {
        return ErrorResponse.singleError(exception.getErrorKey(), exception.getMessage());
    }

    /**
     * Method responsible for handling bad requests.
     *
     * @param exception {@link OperationException} object
     * @return {@link ErrorResponse} object
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(OperationException.class)
    public ErrorResponse badRequest(OperationException exception) {
        return ErrorResponse.singleError(exception.getErrorKey(), exception.getMessage());
    }

    /**
     * Method responsible for handling application conflicts.
     *
     * @param exception {@link ConflictException} object
     * @return {@link ErrorResponse} object
     */
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(ConflictException.class)
    public ErrorResponse conflict(ConflictException exception) {
        return ErrorResponse.singleError(exception.getErrorKey(), exception.getMessage());
    }

    /**
     * Method responsible for handling java bean validation exceptions.
     *
     * @param exception MethodArgumentNotValidException thrown when arguments do not match
     *                  javax.validation.constraints
     * @return {@link ErrorResponse} object
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse beanValidationError(MethodArgumentNotValidException exception) {
        return ErrorResponse.multipleErrors(
            exception.getFieldErrors().stream()
                .map(this::mapFieldErrorToSingleError)
                .collect(Collectors.toSet()));
    }

    /**
     * Method responsible for handling data transfer object validation exceptions.
     *
     * @param exception HttpMessageNotReadableException thrown when arguments do not match model
     * @return {@link ErrorResponse} object
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ErrorResponse unexpectedValue(HttpMessageNotReadableException exception) {
        Throwable cause = exception.getCause();
        String message = "Unexpected value!";
        String fieldName = "";

        if (cause instanceof InvalidFormatException) {
            InvalidFormatException invalidFormatException = (InvalidFormatException) cause;
            fieldName = getFieldName(invalidFormatException);
            message = invalidFormatException.getOriginalMessage();
        }

        if (cause instanceof ValueInstantiationException) {
            ValueInstantiationException valueInstantiationException = (ValueInstantiationException) cause;
            fieldName = getFieldName(valueInstantiationException);
            message = Optional.ofNullable(exception.getCause().getCause())
                .filter(IllegalArgumentException.class::isInstance)
                .map(Throwable::getMessage)
                .orElse("Unexpected value!");
        }

        String errorKey = ErrorKey.UNEXPECTED_VALUE_ERROR;
        if (!fieldName.isEmpty()) {
            errorKey += "." + fieldName;
        }
        return ErrorResponse.singleError(errorKey, message);
    }

    private String getFieldName(JsonMappingException invalidFormatException) {
        List<Reference> path = invalidFormatException.getPath();
        return path.stream()
            .map(Reference::getFieldName)
            .findFirst()
            .orElse("");
    }

    private SingleError mapFieldErrorToSingleError(FieldError fieldError) {
        String errorKey = String.format("%s.%s.%s",
            ErrorKey.ARGUMENT_VALIDATION_ERROR,
            fieldError.getField(),
            fieldError.getCode());
        return new SingleError(errorKey, fieldError.getDefaultMessage());
    }
}
