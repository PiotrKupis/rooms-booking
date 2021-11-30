package com.roomsbooking.backend.exception;

import com.roomsbooking.backend.exception.core.BaseException;
import com.roomsbooking.backend.exception.core.ConflictException;
import com.roomsbooking.backend.exception.core.CriticalException;

public class PhotoException extends BaseException {

    protected PhotoException(BaseException exception) {
        super(exception, exception.getErrorKey());
    }

    public static PhotoException photoPositionTaken(Integer position) {
        return new PhotoException(
            ConflictException.conflict(
                "Photo position: " + position + " is already taken",
                ErrorKey.CONFLICT_PHOTO_POSITION_TAKEN_ERROR));
    }

    public static PhotoException errorOfPhotoProcessing() {
        return new PhotoException(
            CriticalException.critical("Error with processing room photo",
                ErrorKey.CRITICAL_PHOTO_PROCESSING_ERROR));
    }
}
