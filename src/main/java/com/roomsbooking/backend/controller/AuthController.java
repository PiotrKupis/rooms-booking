package com.roomsbooking.backend.controller;


import com.rooms_booking.controller.AuthApi;
import com.rooms_booking.dto.LoginRequest;
import com.rooms_booking.dto.LoginResponse;
import com.rooms_booking.dto.RegisterRequest;
import com.rooms_booking.dto.RegisterResponse;
import com.roomsbooking.backend.service.AuthService;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(tags = {"auth"})
@AllArgsConstructor
public class AuthController implements AuthApi {

    private final AuthService authService;

    @Override
    public ResponseEntity<RegisterResponse> register(RegisterRequest registerRequest) {
        return ResponseEntity.ok(authService.register(registerRequest));
    }

    @Override
    public ResponseEntity<LoginResponse> login(LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }
}
