package com.roomsbooking.backend.repository;

import com.roomsbooking.backend.model.Role;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository responsible for managing objects of type {@link Role}.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Method responsible for finding a specific role by its name.
     *
     * @param roleName name of a specific role
     * @return optional of type {@link Role}
     */
    Optional<Role> findByRoleName(String roleName);
}
