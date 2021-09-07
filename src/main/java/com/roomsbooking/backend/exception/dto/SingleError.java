package com.roomsbooking.backend.exception.dto;

import lombok.Value;

/**
 * Class responsible for keeping a single object of application error.
 */
@Value
public class SingleError {

    String errorKey;
    String message;
}
