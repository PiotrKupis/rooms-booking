package com.roomsbooking.backend.repository;

import com.roomsbooking.backend.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository responsible for managing object of type {@link Room}.
 */
@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

}
