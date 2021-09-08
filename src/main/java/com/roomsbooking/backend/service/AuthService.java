package com.roomsbooking.backend.service;

import com.rooms_booking.dto.RegisterRequest;
import com.rooms_booking.dto.RegisterResponse;
import com.roomsbooking.backend.exception.AuthException;
import com.roomsbooking.backend.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service responsible for managing users.
 */
@Service
@AllArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    private boolean doPasswordsMatch(String password, String repeatedPassword) {
        return password.endsWith(repeatedPassword);
    }

    public RegisterResponse register(RegisterRequest registerRequest) {

        if (!doPasswordsMatch(registerRequest.getPassword(),
            registerRequest.getRepeatedPassword())) {
            throw AuthException.passwordsDoNotMatch();
        }

        userRepository.findByEmail(registerRequest.getEmail()).ifPresent(
            user -> {
                throw AuthException.emailAlreadyTaken();
            });

        return null;
    }
}
