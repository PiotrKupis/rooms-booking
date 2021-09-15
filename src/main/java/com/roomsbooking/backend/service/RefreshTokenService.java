package com.roomsbooking.backend.service;

import com.roomsbooking.backend.model.RefreshToken;
import com.roomsbooking.backend.repository.RefreshTokenRepository;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service responsible for managing objects of type {@link RefreshToken}.
 */
@AllArgsConstructor
@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * Method responsible for generating refresh token.
     *
     * @return object of type {@link RefreshToken}
     */
    public RefreshToken generateRefreshToken() {
        RefreshToken token = RefreshToken.builder()
            .token(UUID.randomUUID().toString())
            .createDate(Instant.now())
            .build();
        return refreshTokenRepository.save(token);
    }
}
