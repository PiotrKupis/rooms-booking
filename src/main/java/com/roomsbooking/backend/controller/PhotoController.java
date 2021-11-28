package com.roomsbooking.backend.controller;

import com.roomsbooking.backend.service.PhotoService;
import com.roomsbooking.controller.PhotoApi;
import com.roomsbooking.dto.PhotoPayload;
import io.swagger.annotations.Api;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Api(tags = "photo")
@AllArgsConstructor
public class PhotoController implements PhotoApi {

    private final PhotoService photoService;

    @Override
    public ResponseEntity<List<PhotoPayload>> getRoomPhotos(String resortName, Integer roomNumber) {
        return ResponseEntity.ok(photoService.getRoomImages(resortName, roomNumber));
    }

    @Override
    public ResponseEntity<String> addRoomPhoto(String resortName, Integer roomNumber,
        MultipartFile photo) {
        return ResponseEntity.ok(photoService.addRoomImage(resortName, roomNumber, photo));
    }
}
