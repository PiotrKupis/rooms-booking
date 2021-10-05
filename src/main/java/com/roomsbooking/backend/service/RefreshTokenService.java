package com.roomsbooking.backend.service;

import com.roomsbooking.backend.exception.AuthException;
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

    /**
     * Method responsible for deleting a refresh token.
     *
     * @param refreshToken specific refresh token to be deleted
     */
    public void deleteRefreshToken(String refreshToken) {
        RefreshToken tokenToDelete = refreshTokenRepository.findByToken(refreshToken)
            .orElseThrow(AuthException::notFoundRefreshToken);
        refreshTokenRepository.delete(tokenToDelete);
    }

    /**
     * Method responsible for checking if refresh token exists.
     *
     * @param refreshToken refresh that is being checked
     */
    public void validateToken(String refreshToken) {
        refreshTokenRepository.findByToken(refreshToken)
            .orElseThrow(AuthException::notFoundRefreshToken);
    }
}
