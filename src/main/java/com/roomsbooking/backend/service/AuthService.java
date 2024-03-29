package com.roomsbooking.backend.service;

import com.roomsbooking.backend.exception.AuthException;
import com.roomsbooking.backend.exception.RoleException;
import com.roomsbooking.backend.exception.UserException;
import com.roomsbooking.backend.mapper.UserMapper;
import com.roomsbooking.backend.model.RefreshToken;
import com.roomsbooking.backend.model.Role;
import com.roomsbooking.backend.model.User;
import com.roomsbooking.backend.repository.RoleRepository;
import com.roomsbooking.backend.repository.UserRepository;
import com.roomsbooking.backend.security.JwtProvider;
import com.roomsbooking.dto.AuthenticationResponse;
import com.roomsbooking.dto.AuthenticationResponse.RoleEnum;
import com.roomsbooking.dto.LoginRequest;
import com.roomsbooking.dto.RefreshTokenPayload;
import com.roomsbooking.dto.RegisterRequest;
import com.roomsbooking.dto.UserPayload;
import java.time.Instant;
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

    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
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
     * @return object of type {@link UserPayload}
     */
    public UserPayload register(RegisterRequest registerRequest) {

        if (!doPasswordsMatch(registerRequest.getPassword(),
            registerRequest.getRepeatedPassword())) {
            throw AuthException.passwordsDoNotMatch();
        }

        userRepository.findByEmail(registerRequest.getEmail()).ifPresent(
            user -> {
                throw AuthException.emailAlreadyTaken();
            });

        Role defaultRole = roleRepository.findByName("USER")
            .orElseThrow(RoleException::defaultRoleIssue);
        User user = userMapper.toUser(registerRequest);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(defaultRole);

        log.info("Registered a new user with email: " + registerRequest.getEmail());
        return userMapper.toUserPayload(userRepository.save(user));
    }

    /**
     * Method responsible for handling login request.
     *
     * @param loginRequest object of type {@link LoginRequest}
     * @return object of type {@link AuthenticationResponse}
     */
    public AuthenticationResponse login(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),
                    loginRequest.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String token = jwtProvider.generateToken(loginRequest.getEmail());
            RefreshToken refreshToken = refreshTokenService.generateRefreshToken();
            Instant expirationDate = Instant.now()
                .plusMillis(jwtProvider.getJwtExpirationInMillis());
            User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(AuthException::badCredentials);

            AuthenticationResponse authenticationResponse = new AuthenticationResponse();
            authenticationResponse.setAuthToken(token);
            authenticationResponse.setRefreshToken(refreshToken.getToken());
            authenticationResponse.setExpireDate(expirationDate.toString());
            authenticationResponse.setEmail(loginRequest.getEmail());
            authenticationResponse.setRole(RoleEnum.valueOf(user.getRole().getName()));

            log.info("Logged in user with email: " + loginRequest.getEmail());
            return authenticationResponse;
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

    /**
     * Method responsible for logging out a user.
     *
     * @param refreshTokenPayload object of type {@link RefreshTokenPayload}
     * @return object of type {@link RefreshTokenPayload}
     */
    public RefreshTokenPayload logOut(RefreshTokenPayload refreshTokenPayload) {
        refreshTokenService.deleteRefreshToken(refreshTokenPayload.getRefreshToken());
        log.info("Logged out user with email: " + refreshTokenPayload.getEmail());
        return refreshTokenPayload;
    }

    /**
     * Method responsible for refreshing a token.
     *
     * @param refreshTokenPayload object of type {@link RefreshTokenPayload}
     * @return object of type {@link AuthenticationResponse}
     */
    public AuthenticationResponse refreshToken(RefreshTokenPayload refreshTokenPayload) {
        log.info(
            "Checking if refresh token " + refreshTokenPayload.getRefreshToken() + " is valid");
        refreshTokenService.validateToken(refreshTokenPayload.getRefreshToken());

        User user = userRepository.findByEmail(refreshTokenPayload.getEmail())
            .orElseThrow(() -> UserException.userNotFound(refreshTokenPayload.getEmail()));
        String token = jwtProvider.generateToken(refreshTokenPayload.getEmail());

        AuthenticationResponse authenticationResponse = new AuthenticationResponse();
        authenticationResponse.setAuthToken(token);
        authenticationResponse.setRefreshToken(refreshTokenPayload.getRefreshToken());
        authenticationResponse.setExpireDate(Instant.now()
            .plusMillis(jwtProvider.getJwtExpirationInMillis())
            .toString());
        authenticationResponse.setEmail(refreshTokenPayload.getEmail());
        authenticationResponse.setRole(RoleEnum.valueOf(user.getRole().getName()));
        log.info("Refreshed token");
        return authenticationResponse;
    }
}
