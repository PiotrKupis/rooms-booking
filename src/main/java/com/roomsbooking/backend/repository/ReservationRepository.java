package com.roomsbooking.backend.repository;

import com.roomsbooking.backend.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository responsible for managing objects of type {@link Reservation}.
 */
@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

}
