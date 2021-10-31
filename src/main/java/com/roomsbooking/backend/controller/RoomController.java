package com.roomsbooking.backend.controller;

import com.roomsbooking.backend.service.RoomService;
import com.roomsbooking.controller.RoomApi;
import com.roomsbooking.dto.AddRoomRequest;
import com.roomsbooking.dto.ImagePayload;
import com.roomsbooking.dto.RoomPayload;
import io.swagger.annotations.Api;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Api(tags = "room")
@AllArgsConstructor
public class RoomController implements RoomApi {

    private final RoomService roomService;

    @Override
    public ResponseEntity<RoomPayload> createRoom(AddRoomRequest addRoomRequest) {
        return ResponseEntity.ok(roomService.createRoom(addRoomRequest));
    }

    @Override
    public ResponseEntity<String> addImage(String resortName, Integer roomNumber,
        MultipartFile image) {
        return ResponseEntity.ok(roomService.addRoomImage(resortName, roomNumber, image));
    }

    @Override
    public ResponseEntity<List<ImagePayload>> getRoomImages(String resortName, Integer roomNumber) {
        return ResponseEntity.ok(roomService.getRoomImages(resortName, roomNumber));
    }

    @Override
    public ResponseEntity<List<RoomPayload>> getAllRooms() {
        return ResponseEntity.ok(roomService.getAllRooms());
    }
}
