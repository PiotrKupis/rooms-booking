package com.roomsbooking.backend.repository;

import com.roomsbooking.backend.model.Resort;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository responsible for managing object of type {@link Resort}
 */
@Repository
public interface ResortRepository extends JpaRepository<Resort, Long> {

    /**
     * Method responsible for finding a specific resort by its name.
     *
     * @param resortName name of a specific resort
     * @return object of type {@link Resort}
     */
    Optional<Resort> findByResortName(String resortName);
}
