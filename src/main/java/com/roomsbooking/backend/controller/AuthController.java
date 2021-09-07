package com.roomsbooking.backend.controller;


import com.rooms_booking.controller.AuthApi;
import com.rooms_booking.dto.RegisterRequest;
import com.rooms_booking.dto.RegisterResponse;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(tags = {"auth"})
@AllArgsConstructor
public class AuthController implements AuthApi {

    @Override
    public ResponseEntity<RegisterResponse> register(RegisterRequest registerRequest) {
        System.out.println(registerRequest.toString());
        RegisterResponse registerResponse = new RegisterResponse();
        registerResponse.setName("Jan");
        registerResponse.setSurname("Kowalski");
        registerResponse.setEmail("kowalski@gmail.com");
        return ResponseEntity.ok(registerResponse);
    }
}
