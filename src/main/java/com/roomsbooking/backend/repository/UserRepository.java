package com.roomsbooking.backend.repository;

import com.roomsbooking.backend.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository responsible for managing objects of type {@link User}.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Method responsible for finding a specific user by email.
     *
     * @param email email of a specific user
     * @return optional of type {@link User}
     */
    Optional<User> findByEmail(String email);
}
