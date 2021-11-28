package com.roomsbooking.backend.service;

import com.roomsbooking.backend.exception.RoomException;
import com.roomsbooking.backend.mapper.RoomMapper;
import com.roomsbooking.backend.model.Room;
import com.roomsbooking.backend.repository.RoomRepository;
import com.roomsbooking.backend.utils.PaginationUtils;
import com.roomsbooking.dto.AddRoomRequest;
import com.roomsbooking.dto.DetailedRoomPayload;
import com.roomsbooking.dto.RoomPayload;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * Service responsible for managing objects of type {@link Room}.
 */
@Slf4j
@AllArgsConstructor
@Service
public class RoomService {

    private final ResortService resortService;
    private final RoomRepository roomRepository;
    private final RoomMapper roomMapper;

    /**
     * Method responsible for saving a new room.
     *
     * @param addRoomRequest object of type {@link AddRoomRequest}
     * @return object of type {@link RoomPayload}
     */
    @CacheEvict(value = "room", allEntries = true)
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

    /**
     * Method responsible for getting all rooms.
     *
     * @return list of {@link DetailedRoomPayload} objects
     */
    @Cacheable("room")
    public List<DetailedRoomPayload> getAllRooms(Integer imageQuantity) {
        log.info("Getting all rooms with photos");
        List<DetailedRoomPayload> rooms = roomRepository.findAll().stream()
            .map(roomMapper::toDetailedRoomPayload)
            .collect(Collectors.toList());

        if (imageQuantity != null) {
            PaginationUtils.limitImagesPerRoom(rooms, imageQuantity);
        }
        return rooms;
    }

    /**
     * Method responsible for getting a specific room with photos.
     *
     * @param resortName name of a resort connected with a specific room
     * @param roomNumber number of a specific room
     * @return object of type {@link DetailedRoomPayload}
     */
    @Cacheable("room")
    public DetailedRoomPayload getRoom(String resortName, Integer roomNumber) {
        log.info(
            "Getting room nr " + roomNumber + " of " + resortName + " resort");
        Room room = getRoomOfResort(resortName, roomNumber);
        return roomMapper.toDetailedRoomPayload(room);
    }

    /**
     * Method responsible for getting room object by its resort and room number.
     *
     * @param resortName name of resort connected with the room
     * @param roomNumber number of the specific room
     * @return object of type {@link Room}
     */
    public Room getRoomOfResort(String resortName, Integer roomNumber) {
        return resortService.getResortByName(resortName)
            .getRooms().stream()
            .filter(r -> r.getRoomNumber().equals(roomNumber))
            .findFirst()
            .orElseThrow(() -> RoomException.roomWithNumberNotFound(roomNumber));
    }
}
