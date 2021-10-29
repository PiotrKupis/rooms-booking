package com.roomsbooking.backend.service;

import com.roomsbooking.backend.exception.ResortException;
import com.roomsbooking.backend.exception.RoomException;
import com.roomsbooking.backend.mapper.RoomMapper;
import com.roomsbooking.backend.model.Image;
import com.roomsbooking.backend.model.Resort;
import com.roomsbooking.backend.model.Room;
import com.roomsbooking.backend.repository.ImageRepository;
import com.roomsbooking.backend.repository.RoomRepository;
import com.roomsbooking.dto.AddRoomRequest;
import com.roomsbooking.dto.RoomPayload;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final ImageRepository imageRepository;
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

    /**
     * Method responsible for adding a new photo of a room.
     *
     * @param resortName name of a resort connected with a specific room
     * @param roomNumber number of a room, to which the photo is added
     * @param image      room photo to be saved
     * @return success message
     */
    public String addRoomImage(String resortName, Integer roomNumber, MultipartFile image) {
        try {
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

            Image newImage = Image.builder()
                .name(image.getName())
                .type(image.getContentType())
                .bytes(compressBytes(image.getBytes()))
                .room(room)
                .build();
            newImage = imageRepository.save(newImage);
            log.info("Saved a new photo: " + newImage.getName());
        } catch (IOException e) {
            throw RoomException.errorOfPhotoProcessing();
        }

        return "\"Added room photo\"";
    }

    private byte[] compressBytes(byte[] data) {
        Deflater deflater = new Deflater();
        deflater.setInput(data);
        deflater.finish();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        try {
            byte[] buffer = new byte[1024];
            while (!deflater.finished()) {
                int count = deflater.deflate(buffer);
                outputStream.write(buffer, 0, count);
            }
            outputStream.close();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return outputStream.toByteArray();
    }

    private byte[] decompressBytes(byte[] data) {
        Inflater inflater = new Inflater();
        inflater.setInput(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        try {
            byte[] buffer = new byte[1024];
            while (!inflater.finished()) {
                int count = inflater.inflate(buffer);
                outputStream.write(buffer, 0, count);
            }
            outputStream.close();
        } catch (IOException | DataFormatException e) {
            log.error(e.getMessage());
        }
        return outputStream.toByteArray();
    }
}
