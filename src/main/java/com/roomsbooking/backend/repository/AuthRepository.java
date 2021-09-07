package com.roomsbooking.backend.repository;

import com.roomsbooking.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository responsible for managing objects of type {@link User}.
 */
@Repository
public interface AuthRepository extends JpaRepository<User, Long> {

}
