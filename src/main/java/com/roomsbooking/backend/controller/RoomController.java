package com.roomsbooking.backend.controller;

import com.roomsbooking.backend.service.RoomService;
import com.roomsbooking.controller.RoomApi;
import com.roomsbooking.dto.AddRoomRequest;
import com.roomsbooking.dto.DetailedRoomPayload;
import com.roomsbooking.dto.RoomPayload;
import io.swagger.annotations.Api;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<List<DetailedRoomPayload>> getAllRooms(Integer imageQuantity) {
        return ResponseEntity.ok(roomService.getAllRooms(imageQuantity));
    }

    @Override
    public ResponseEntity<DetailedRoomPayload> getRoom(String resortName, Integer roomNumber) {
        return ResponseEntity.ok(roomService.getRoom(resortName, roomNumber));
    }
}
