package com.roomsbooking.backend.service;

import com.rooms_booking.dto.LoginRequest;
import com.rooms_booking.dto.LoginResponse;
import com.rooms_booking.dto.RegisterRequest;
import com.rooms_booking.dto.RegisterResponse;
import com.roomsbooking.backend.exception.AuthException;
import com.roomsbooking.backend.exception.UserException;
import com.roomsbooking.backend.mapper.UserMapper;
import com.roomsbooking.backend.model.RefreshToken;
import com.roomsbooking.backend.model.Role;
import com.roomsbooking.backend.model.User;
import com.roomsbooking.backend.repository.RoleRepository;
import com.roomsbooking.backend.repository.UserRepository;
import com.roomsbooking.backend.security.JwtProvider;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service responsible for managing objects of type {@link User}
 */
@Slf4j
@AllArgsConstructor
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenService refreshTokenService;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;

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

    /**
     * Method responsible for handling login request.
     *
     * @param loginRequest object of type {@link LoginRequest}
     * @return object of type {@link LoginResponse}
     */
    public LoginResponse login(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),
                    loginRequest.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String token = jwtProvider.generateToken(loginRequest.getEmail());
            RefreshToken refreshToken = refreshTokenService.generateRefreshToken();
            Instant expirationDate = Instant.now()
                .plusMillis(jwtProvider.getJwtExpirationInMillis());
            List<String> roles = userRepository.findByEmail(loginRequest.getEmail())
                .map(user -> user.getRoles().stream()
                    .map(Role::getRoleName)
                    .collect(Collectors.toList()))
                .orElseThrow(AuthException::badCredentials);

            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setAuthToken(token);
            loginResponse.setRefreshToken(refreshToken.getToken());
            loginResponse.setExpireDate(expirationDate.toString());
            loginResponse.setEmail(loginRequest.getEmail());
            loginResponse.setRole(roles);

            log.info("Logged in user with email: " + loginRequest.getEmail());
            return loginResponse;
        } catch (AuthenticationException e) {
            throw AuthException.badCredentials();
        }
    }

    /**
     * Method responsible for getting current user.
     *
     * @return object of type {@link User}
     */
    public User getCurrentUser() {
        var principal = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext()
            .getAuthentication()
            .getPrincipal();
        return userRepository.findByEmail(principal.getUsername())
            .orElseThrow(() -> UserException.userNotFound(principal.getUsername()));
    }
}
