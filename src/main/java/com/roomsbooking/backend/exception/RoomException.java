package com.roomsbooking.backend.exception;

import com.roomsbooking.backend.exception.core.BaseException;
import com.roomsbooking.backend.exception.core.ConflictException;
import com.roomsbooking.backend.exception.core.CriticalException;
import com.roomsbooking.backend.exception.core.NotFoundException;
import com.roomsbooking.backend.exception.core.OperationException;

public class RoomException extends BaseException {

    protected RoomException(BaseException exception) {
        super(exception, exception.getErrorKey());
    }

    public static RoomException roomNumberAlreadyTaken(Integer roomNumber) {
        return new RoomException(
            ConflictException.conflict(
                "Room number " + roomNumber + " in the resort is already taken",
                ErrorKey.CONFLICT_ROOM_NUMBER_ALREADY_TAKEN_ERROR));
    }

    public static RoomException lackOfBeds() {
        return new RoomException(
            OperationException.operationError("Room must have at least one bed",
                ErrorKey.OPERATION_ROOM_EQUIPMENT_ERROR));
    }

    public static RoomException roomWithNumberNotFound(Integer number) {
        return new RoomException(
            NotFoundException.notFound("Room with number " + number + " not found",
                ErrorKey.NOT_FOUND_ROOM_ERROR));
    }

    public static RoomException errorOfPhotoProcessing() {
        return new RoomException(
            CriticalException.critical("Error with processing room photo",
                ErrorKey.CRITICAL_PHOTO_PROCESSING_ERROR));
    }
}
