package com.roomsbooking.backend.exception;

import com.roomsbooking.backend.exception.core.BaseException;
import com.roomsbooking.backend.exception.core.ConflictException;

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
}
