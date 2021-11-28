package com.roomsbooking.backend.utils;

import com.roomsbooking.dto.DetailedRoomPayload;
import java.util.List;

public class PaginationUtils {

    /**
     * Method responsible for getting room list with specified max number of images per room.
     *
     * @param rooms         list of {@link DetailedRoomPayload} objects
     * @param imageQuantity max number of images per room
     * @return list of {@link DetailedRoomPayload} objects
     */
    public static List<DetailedRoomPayload> limitImagesPerRoom(List<DetailedRoomPayload> rooms,
        Integer imageQuantity) {
        for (DetailedRoomPayload room : rooms) {
            room.setPhotos(
                room.getPhotos().subList(0, getToPositionOrMaxSize(imageQuantity, room)));
        }
        return rooms;
    }

    private static int getToPositionOrMaxSize(Integer imageQuantity, DetailedRoomPayload room) {
        return imageQuantity > room.getPhotos().size() ? room.getPhotos().size() : imageQuantity;
    }
}
