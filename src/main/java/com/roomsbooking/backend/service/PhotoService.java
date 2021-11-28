package com.roomsbooking.backend.service;

import com.roomsbooking.backend.exception.RoomException;
import com.roomsbooking.backend.mapper.PhotoMapper;
import com.roomsbooking.backend.model.Image;
import com.roomsbooking.backend.model.Room;
import com.roomsbooking.backend.repository.PhotoRepository;
import com.roomsbooking.dto.PhotoPayload;
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
 * Service responsible for managing search operations.
 */
@Slf4j
@AllArgsConstructor
@Service
public class PhotoService {

    private final ResortService resortService;
    private final RoomService roomService;
    private final PhotoRepository photoRepository;
    private final PhotoMapper photoMapper;

    /**
     * Method responsible for adding a new room photo.
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

        Room room = resortService.getCurrentUserResort(resortName)
            .getRooms().stream()
            .filter(r -> r.getRoomNumber().equals(roomNumber))
            .findFirst()
            .orElseThrow(() -> RoomException.roomWithNumberNotFound(roomNumber));

        Image newImage;
        try {
            newImage = photoMapper.toImage(image, room);
        } catch (IOException e) {
            throw RoomException.errorOfPhotoProcessing();
        }
        newImage = photoRepository.save(newImage);
        log.info("Saved a new photo: " + newImage.getName());
        return "\"Added room photo\"";
    }

    /**
     * Method responsible for getting photos of a specific room.
     *
     * @param resortName name of a resort connected with a specific room
     * @param roomNumber number of a specific room
     * @return list of {@link PhotoPayload} objects
     */
    @Cacheable("room")
    public List<PhotoPayload> getRoomImages(String resortName, Integer roomNumber) {
        log.info(
            "Getting photos of room nr " + roomNumber + " of " + resortName + " resort");
        Room room = roomService.getRoomOfResort(resortName, roomNumber);
        return room.getImages().stream()
            .map(photoMapper::toPhotoPayload)
            .collect(Collectors.toList());
    }
}
