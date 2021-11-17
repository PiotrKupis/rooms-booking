package com.roomsbooking.backend.service;

import com.roomsbooking.backend.exception.ReservationException;
import com.roomsbooking.backend.exception.RoomException;
import com.roomsbooking.backend.mapper.ImageMapper;
import com.roomsbooking.backend.mapper.ReservationMapper;
import com.roomsbooking.backend.mapper.RoomMapper;
import com.roomsbooking.backend.model.Address;
import com.roomsbooking.backend.model.Image;
import com.roomsbooking.backend.model.Room;
import com.roomsbooking.backend.repository.ImageRepository;
import com.roomsbooking.backend.repository.RoomRepository;
import com.roomsbooking.dto.AddRoomRequest;
import com.roomsbooking.dto.DetailedRoomPayload;
import com.roomsbooking.dto.ImagePayload;
import com.roomsbooking.dto.RoomPayload;
import com.roomsbooking.dto.SearchPayload;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
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

    private final ResortService resortService;
    private final RoomRepository roomRepository;
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

        Room room = resortService.getCurrentUserResort(resortName)
            .getRooms().stream()
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
        Room room = getRoomOfResort(resortName, roomNumber);
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
        Room room = getRoomOfResort(resortName, roomNumber);
        return roomMapper.toDetailedRoomPayload(room);
    }

    /**
     * Method responsible for getting list of specific rooms.
     *
     * @param searchPayload object of type {@link SearchPayload}
     * @param pageNumber    specific part of the searched results
     * @param roomsPerPage  number of rooms to return
     * @return list of {@link SearchPayload} objects
     */
    @Cacheable("room")
    public List<DetailedRoomPayload> searchRooms(SearchPayload searchPayload, Integer pageNumber,
        Integer roomsPerPage) {
        try {
            log.info("Searching for the specific rooms");
            Date startDate = ReservationMapper.dateFormat.parse(searchPayload.getStartDate());
            Date endDate = ReservationMapper.dateFormat.parse(searchPayload.getEndDate());

            List<DetailedRoomPayload> rooms = roomRepository.findAll().stream()
                .filter(room -> areLocationsMatching(searchPayload, room))
                .filter(room -> isResidentNumberMatching(searchPayload, room))
                .filter(room -> isRoomAvailable(startDate, endDate, room))
                .map(roomMapper::toDetailedRoomPayload)
                .collect(Collectors.toList());

            if (pageNumber != null && roomsPerPage != null) {
                rooms = rooms.stream()
                    .skip((long) (pageNumber - 1) * roomsPerPage)
                    .limit((long) roomsPerPage)
                    .collect(Collectors.toList());
            }

            if (rooms.isEmpty()) {
                throw RoomException.matchingRoomsNotFound();
            }
            return rooms;
        } catch (ParseException e) {
            throw ReservationException.incorrectDateFormat();
        }
    }

    private int getToPositionOrMaxSize(Integer imageQuantity, DetailedRoomPayload room) {
        return imageQuantity > room.getImages().size() ? room.getImages().size() : imageQuantity;
    }

    private Room getRoomOfResort(String resortName, Integer roomNumber) {
        return resortService.getResortByName(resortName)
            .getRooms().stream()
            .filter(r -> r.getRoomNumber().equals(roomNumber))
            .findFirst()
            .orElseThrow(() -> RoomException.roomWithNumberNotFound(roomNumber));
    }

    private boolean isRoomAvailable(Date startDate, Date endDate, Room room) {
        return room.getReservations().stream()
            .noneMatch(reservation ->
                ReservationService.areDateRangesUnavailable(startDate, endDate, reservation));
    }

    private boolean isResidentNumberMatching(SearchPayload searchPayload, Room room) {
        return room.getMaxResidentsNumber().equals(searchPayload.getResidentsNumber());
    }

    private boolean areLocationsMatching(SearchPayload searchPayload, Room room) {
        return getRoomAddress(room).getCountry().equals(searchPayload.getLocation())
            || getRoomAddress(room).getCity().equals(searchPayload.getLocation());
    }

    private Address getRoomAddress(Room room) {
        return room.getResort().getAddress();
    }
}
