package com.roomsbooking.backend.controller;

import com.roomsbooking.backend.service.ReservationService;
import com.roomsbooking.controller.ReservationApi;
import com.roomsbooking.dto.ReservationPayload;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(tags = "reservation")
@AllArgsConstructor
public class ReservationController implements ReservationApi {

    private final ReservationService reservationService;

    @Override
    public ResponseEntity<ReservationPayload> addReservation(
        ReservationPayload reservationPayload) {
        return ResponseEntity.ok(reservationService.addReservation(reservationPayload));
    }
}
