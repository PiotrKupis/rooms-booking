package com.roomsbooking.backend.service;

import com.rooms_booking.dto.RegisterRequest;
import com.rooms_booking.dto.RegisterResponse;
import org.springframework.stereotype.Service;

/**
 * Service responsible for managing users.
 */
@Service
public class AuthService {

    private boolean doPasswordsMatch(String password, String repeatedPassword) {
        return password.endsWith(repeatedPassword);
    }

    public RegisterResponse register(RegisterRequest registerRequest) {
        return null;
    }
}
