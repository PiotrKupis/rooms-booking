package com.roomsbooking.backend.repository;

import com.roomsbooking.backend.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository responsible for managing object of type {@link Image}.
 */
@Repository
public interface PhotoRepository extends JpaRepository<Image, Long> {

}
