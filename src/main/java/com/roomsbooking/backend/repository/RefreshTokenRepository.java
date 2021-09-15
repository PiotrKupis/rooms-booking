package com.roomsbooking.backend.repository;

import com.roomsbooking.backend.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository responsible for managing objects of type {@link RefreshToken}.
 */
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

}
