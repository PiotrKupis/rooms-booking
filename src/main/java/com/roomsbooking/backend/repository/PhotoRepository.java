package com.roomsbooking.backend.repository;

import com.roomsbooking.backend.model.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository responsible for managing object of type {@link Photo}.
 */
@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {

}
