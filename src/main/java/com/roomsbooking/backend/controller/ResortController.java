package com.roomsbooking.backend.controller;

import com.roomsbooking.backend.service.ResortService;
import com.roomsbooking.controller.ResortApi;
import com.roomsbooking.dto.ResortPayload;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(tags = "resort")
@AllArgsConstructor
public class ResortController implements ResortApi {

    private final ResortService resortService;

    @Override
    public ResponseEntity<ResortPayload> createResort(ResortPayload resortPayload) {
        return ResponseEntity.ok(resortService.createResort(resortPayload));
    }
}
