package com.roomsbooking.backend.service;

import com.rooms_booking.dto.RegisterRequest;
import com.rooms_booking.dto.RegisterResponse;
import com.roomsbooking.backend.exception.AuthException;
import com.roomsbooking.backend.mapper.UserMapper;
import com.roomsbooking.backend.model.Role;
import com.roomsbooking.backend.model.User;
import com.roomsbooking.backend.repository.RoleRepository;
import com.roomsbooking.backend.repository.UserRepository;
import java.util.Collections;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service responsible for managing users.
 */
@Slf4j
@Service
@AllArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    private boolean doPasswordsMatch(String password, String repeatedPassword) {
        return password.endsWith(repeatedPassword);
    }

    /**
     * Method responsible for registering a new user.
     *
     * @param registerRequest object of type {@link RegisterRequest}
     * @return object of type {@link RegisterResponse}
     */
    public RegisterResponse register(RegisterRequest registerRequest) {

        if (!doPasswordsMatch(registerRequest.getPassword(),
            registerRequest.getRepeatedPassword())) {
            throw AuthException.passwordsDoNotMatch();
        }

        userRepository.findByEmail(registerRequest.getEmail()).ifPresent(
            user -> {
                throw AuthException.emailAlreadyTaken();
            });

        Role defaultRole = roleRepository.findByRoleName("USER")
            .orElseThrow(AuthException::rolesIssue);
        User user = userMapper.toUser(registerRequest);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(Collections.singleton(defaultRole));
        userRepository.save(user);
        log.info("Registered a new user with email: " + registerRequest.getEmail());

        return userMapper.toRegisterResponse(user);
    }
}
