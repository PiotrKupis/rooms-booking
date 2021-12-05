package com.roomsbooking.backend.controller;


import com.roomsbooking.backend.service.UserService;
import com.roomsbooking.controller.UserApi;
import com.roomsbooking.dto.UserPayload;
import io.swagger.annotations.Api;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(tags = {"user"})
@AllArgsConstructor
public class UserController implements UserApi {

    private final UserService userService;

    @Override
    public ResponseEntity<List<UserPayload>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @Override
    public ResponseEntity<UserPayload> changeRole(String email, String role) {
        return ResponseEntity.ok(userService.changeRole(email, role));
    }
}
