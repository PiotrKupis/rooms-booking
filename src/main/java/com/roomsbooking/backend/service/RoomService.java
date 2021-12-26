package com.roomsbooking.backend.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.roomsbooking.backend.exception.PhotoException;
import com.roomsbooking.backend.exception.RoomException;
import com.roomsbooking.backend.mapper.RoomMapper;
import com.roomsbooking.backend.model.Photo;
import com.roomsbooking.backend.model.Resort;
import com.roomsbooking.backend.model.Room;
import com.roomsbooking.backend.repository.RoomRepository;
import com.roomsbooking.backend.utils.DateUtils;
import com.roomsbooking.backend.utils.PaginationUtils;
import com.roomsbooking.dto.AddRoomRequest;
import com.roomsbooking.dto.DetailedRoomPayload;
import com.roomsbooking.dto.RoomPayload;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final Cloudinary cloudinary;

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
        log.info("Getting room nr " + roomNumber + " of " + resortName + " resort");
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
        return getSpecificRoom(roomNumber, resortService.getResortByName(resortName));
    }

    /**
     * Method responsible for getting current user's room by its resort and room number.
     *
     * @param resortName name of resort connected with the room
     * @param roomNumber number of the specific room
     * @return object of type {@link Room}
     */
    public Room getCurrentUserRoom(String resortName, Integer roomNumber) {
        return getSpecificRoom(roomNumber, resortService.getCurrentUserResort(resortName));
    }

    /**
     * Method responsible for updating current user's room.
     *
     * @param resortName name of resort connected with the room
     * @param roomNumber number of the specific room
     * @return object of type {@link RoomPayload}
     */
    @CacheEvict(value = "room", allEntries = true)
    @Transactional
    public RoomPayload updateRoom(String resortName, Integer roomNumber,
        AddRoomRequest addRoomRequest) {
        log.info("Updating room nr " + roomNumber + " of " + resortName + " resort");
        if (addRoomRequest.getSingleBedQuantity() + addRoomRequest.getDoubleBedQuantity()
            + addRoomRequest.getKingSizeBedQuantity() < 1) {
            throw RoomException.lackOfBeds();
        }

        Room room = getCurrentUserRoom(resortName, roomNumber);
        room.getResort().getRooms().stream()
            .map(Room::getRoomNumber)
            .filter(number -> !number.equals(room.getRoomNumber()))
            .filter(number -> number.equals(addRoomRequest.getRoomNumber()))
            .findFirst()
            .ifPresent(number -> {
                throw RoomException.roomNumberAlreadyTaken(number);
            });

        room.setRoomNumber(addRoomRequest.getRoomNumber());
        room.setPrice(new BigDecimal(addRoomRequest.getPrice()));
        room.setPriceCurrency(addRoomRequest.getPriceCurrency());
        room.setRoomAmenities(roomMapper.toRoomAmenities(addRoomRequest.getRoomAmenities()));
        room.setSingleBedQuantity(addRoomRequest.getSingleBedQuantity());
        room.setDoubleBedQuantity(addRoomRequest.getDoubleBedQuantity());
        room.setKingSizeBedQuantity(addRoomRequest.getKingSizeBedQuantity());
        room.setMaxResidentsNumber(addRoomRequest.getMaxResidentsNumber());

        return roomMapper.toRoomPayload(room);
    }

    /**
     * Method responsible for deleting current user's room.
     *
     * @param resortName name of resort connected with the room
     * @param roomNumber number of the specific room
     * @return message about successful deleting a room
     */
    @CacheEvict(value = "room", allEntries = true)
    public String deleteRoom(String resortName, Integer roomNumber) {
        log.info("Deleting room nr " + roomNumber + " of " + resortName + " resort");
        Room room = getCurrentUserRoom(resortName, roomNumber);

        try {
            Date today = DateUtils.dateFormat.parse(DateUtils.dateFormat.format(new Date()));
            room.getReservations().stream()
                .filter(r -> r.getEndDate().after(today) || r.getEndDate().equals(today))
                .findFirst()
                .ifPresent(r -> {
                    throw RoomException.unrealizedReservations(roomNumber);
                });
        } catch (ParseException e) {
            e.printStackTrace();
        }

        room.getPhotos().forEach(this::deletePhoto);
        roomRepository.deleteById(room.getId());
        return "\"Deleted room\"";
    }

    private void deletePhoto(Photo photo) {
        try {
            log.info("Deleting photo");
            cloudinary.uploader().destroy(photo.getCloudinaryId(), ObjectUtils.emptyMap());
        } catch (IOException e) {
            throw PhotoException.errorOfPhotoProcessing();
        }
    }

    private Room getSpecificRoom(Integer roomNumber, Resort resort) {
        return resort
            .getRooms().stream()
            .filter(r -> r.getRoomNumber().equals(roomNumber))
            .findFirst()
            .orElseThrow(() -> RoomException.roomWithNumberNotFound(roomNumber));
    }
}
