package com.roomsbooking.backend.service;

import com.roomsbooking.backend.exception.ResortException;
import com.roomsbooking.backend.exception.RoomException;
import com.roomsbooking.backend.mapper.ImageMapper;
import com.roomsbooking.backend.mapper.RoomMapper;
import com.roomsbooking.backend.model.Image;
import com.roomsbooking.backend.model.Resort;
import com.roomsbooking.backend.model.Room;
import com.roomsbooking.backend.repository.ImageRepository;
import com.roomsbooking.backend.repository.ResortRepository;
import com.roomsbooking.backend.repository.RoomRepository;
import com.roomsbooking.dto.AddRoomRequest;
import com.roomsbooking.dto.DetailedRoomPayload;
import com.roomsbooking.dto.ImagePayload;
import com.roomsbooking.dto.RoomPayload;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service responsible for managing objects of type {@link Room}.
 */
@Slf4j
@AllArgsConstructor
@Service
public class RoomService {

    private final AuthService authService;
    private final RoomRepository roomRepository;
    private final ResortRepository resortRepository;
    private final ImageRepository imageRepository;
    private final RoomMapper roomMapper;
    private final ImageMapper imageMapper;

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
     * Method responsible for adding a new photo of a room.
     *
     * @param resortName name of a resort connected with a specific room
     * @param roomNumber number of a specific room
     * @param image      room photo to be saved
     * @return success message
     */
    @CacheEvict(value = "room", allEntries = true)
    public String addRoomImage(String resortName, Integer roomNumber, MultipartFile image) {
        log.info(
            "Adding a new photo to room nr " + roomNumber + " of " + resortName + " resort");

        Resort resort = authService.getCurrentUser().getResorts().stream()
            .filter(r -> r.getResortName().equals(resortName))
            .findFirst()
            .orElseThrow(() -> ResortException.resortNotFound(resortName));

        Room room = resort.getRooms().stream()
            .filter(r -> r.getRoomNumber().equals(roomNumber))
            .findFirst()
            .orElseThrow(() -> RoomException.roomWithNumberNotFound(roomNumber));

        Image newImage;
        try {
            newImage = imageMapper.toImage(image, room);
        } catch (IOException e) {
            throw RoomException.errorOfPhotoProcessing();
        }

        newImage = imageRepository.save(newImage);
        log.info("Saved a new photo: " + newImage.getName());
        return "\"Added room photo\"";
    }

    /**
     * Method responsible for getting photos of a specific room.
     *
     * @param resortName name of a resort connected with a specific room
     * @param roomNumber number of a specific room
     * @return list of {@link ImagePayload} objects
     */
    @Cacheable("room")
    public List<ImagePayload> getRoomImages(String resortName, Integer roomNumber) {
        log.info(
            "Getting photos of room nr " + roomNumber + " of " + resortName + " resort");
        Room room = getRoomFromDatabase(resortName, roomNumber);
        return room.getImages().stream()
            .map(imageMapper::toImagePayload)
            .collect(Collectors.toList());
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
            for (DetailedRoomPayload room : rooms) {
                room.setImages(
                    room.getImages().subList(0, getToPositionOrMaxSize(imageQuantity, room)));
            }
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
        Room room = getRoomFromDatabase(resortName, roomNumber);
        return roomMapper.toDetailedRoomPayload(room);
    }

    private int getToPositionOrMaxSize(Integer imageQuantity, DetailedRoomPayload room) {
        return imageQuantity > room.getImages().size() ? room.getImages().size() : imageQuantity;
    }

    private Room getRoomFromDatabase(String resortName, Integer roomNumber) {
        Resort resort = resortRepository.findByResortName(resortName)
            .orElseThrow(() -> ResortException.resortNotFound(resortName));

        return resort.getRooms().stream()
            .filter(r -> r.getRoomNumber().equals(roomNumber))
            .findFirst()
            .orElseThrow(() -> RoomException.roomWithNumberNotFound(roomNumber));
    }
}
