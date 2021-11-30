package com.roomsbooking.backend.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.roomsbooking.backend.exception.PhotoException;
import com.roomsbooking.backend.mapper.PhotoMapper;
import com.roomsbooking.backend.model.Photo;
import com.roomsbooking.backend.model.Room;
import com.roomsbooking.backend.repository.PhotoRepository;
import com.roomsbooking.dto.AddPhotoRequest;
import com.roomsbooking.dto.PhotoPayload;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * Service responsible for managing object of type {@link Photo}.
 */
@Slf4j
@AllArgsConstructor
@Service
public class PhotoService {

    private final RoomService roomService;
    private final PhotoRepository photoRepository;
    private final PhotoMapper photoMapper;
    private final Cloudinary cloudinary;

    /**
     * Method responsible for adding a new room photo.
     *
     * @param resortName      name of a resort connected with a specific room
     * @param roomNumber      number of a specific room
     * @param addPhotoRequest room photo to be saved
     * @return object of type {@link PhotoPayload}
     */
    @CacheEvict(value = "room", allEntries = true)
    public PhotoPayload addRoomImage(String resortName, Integer roomNumber,
        AddPhotoRequest addPhotoRequest) {
        try {
            log.info("Adding a photo to room nr " + roomNumber + " of " + resortName + " resort");
            Room room = roomService.getCurrentUserRoom(resortName, roomNumber);
            if (!isPhotoPositionAvailable(room, addPhotoRequest.getPosition())) {
                throw PhotoException.photoPositionTaken(addPhotoRequest.getPosition());
            }

            Map response = cloudinary.uploader()
                .upload(addPhotoRequest.getData(), ObjectUtils.emptyMap());

            Photo photo = photoMapper.toPhoto(addPhotoRequest);
            photo.setCloudinaryId((String) response.get("public_id"));
            photo.setUrl((String) response.get("url"));
            photo.setRoom(room);
            photo = photoRepository.save(photo);
            log.info("Saved a new photo");
            return photoMapper.toPhotoPayload(photo);
        } catch (IOException e) {
            throw PhotoException.errorOfPhotoProcessing();
        }
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
        return room.getPhotos().stream()
            .map(photoMapper::toPhotoPayload)
            .collect(Collectors.toList());
    }

    private boolean isPhotoPositionAvailable(Room room, Integer position) {
        return room.getPhotos().stream()
            .noneMatch(photo -> photo.getPosition().equals(position));
    }
}
