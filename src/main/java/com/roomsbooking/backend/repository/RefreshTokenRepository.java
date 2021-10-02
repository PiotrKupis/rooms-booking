package com.roomsbooking.backend.repository;

import com.roomsbooking.backend.model.RefreshToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository responsible for managing objects of type {@link RefreshToken}.
 */
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    /**
     * Method responsible for finding object of type {@link RefreshToken}.
     *
     * @param token token of {@link RefreshToken} object
     * @return optional of type {@link RefreshToken}
     */
    Optional<RefreshToken> findByToken(String token);
}
