package com.roomsbooking.backend.service;

import com.roomsbooking.backend.exception.RoomException;
import com.roomsbooking.backend.mapper.RoomMapper;
import com.roomsbooking.backend.model.Room;
import com.roomsbooking.backend.repository.RoomRepository;
import com.roomsbooking.dto.AddRoomRequest;
import com.roomsbooking.dto.RoomPayload;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service responsible for managing objects of type {@link Room}.
 */
@Slf4j
@AllArgsConstructor
@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final RoomMapper roomMapper;

    /**
     * Method responsible for saving a new room.
     *
     * @param addRoomRequest object of type {@link AddRoomRequest}
     * @return object of type {@link RoomPayload}
     */
    public RoomPayload createRoom(AddRoomRequest addRoomRequest) {
        log.info("Add room request \n" + addRoomRequest.toString());
        Room room = roomMapper.toRoom(addRoomRequest);

        if (room.getSingleBedQuantity() + room.getDoubleBedQuantity()
            + room.getKingSizeBedQuantity() < 1) {
            throw RoomException.lackOfBeds();
        }

        room.getResort().getRooms().stream()
            .map(Room::getRoomNumber)
            .filter(number -> number.equals(addRoomRequest.getRoomNumber()))
            .findFirst()
            .ifPresent(number -> {
                throw RoomException.roomNumberAlreadyTaken(number);
            });

        Room savedRoom = roomRepository.save(room);
        log.info("Saved a ned room " + savedRoom);
        return roomMapper.toRoomPayload(roomRepository.save(savedRoom));
    }
}
