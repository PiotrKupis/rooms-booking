package com.roomsbooking.backend.controller;


import com.roomsbooking.backend.service.AuthService;
import com.roomsbooking.controller.AuthApi;
import com.roomsbooking.dto.AuthenticationResponse;
import com.roomsbooking.dto.LoginRequest;
import com.roomsbooking.dto.RefreshTokenPayload;
import com.roomsbooking.dto.RegisterRequest;
import com.roomsbooking.dto.UserPayload;
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
    public ResponseEntity<UserPayload> register(RegisterRequest registerRequest) {
        return ResponseEntity.ok(authService.register(registerRequest));
    }

    @Override
    public ResponseEntity<AuthenticationResponse> login(LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @Override
    public ResponseEntity<RefreshTokenPayload> logOut(RefreshTokenPayload refreshTokenPayload) {
        return ResponseEntity.ok(authService.logOut(refreshTokenPayload));
    }

    @Override
    public ResponseEntity<AuthenticationResponse> refreshToken(
        RefreshTokenPayload refreshTokenPayload) {
        return ResponseEntity.ok(authService.refreshToken(refreshTokenPayload));
    }
}
