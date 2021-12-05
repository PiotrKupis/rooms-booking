package com.roomsbooking.backend.service;

import com.roomsbooking.backend.exception.UserException;
import com.roomsbooking.backend.model.Role;
import com.roomsbooking.backend.model.User;
import com.roomsbooking.backend.repository.UserRepository;
import java.util.Collection;
import java.util.Collections;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Class responsible for configuring user-specific data, that will be used by Spring Security.
 */
@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Method responsible for preparing security information about user.
     *
     * @param email user's email, that will be used to find a specific user
     * @return object of type {@link UserDetails}
     */
    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> UserException.userNotFound(email));

        return new org.springframework.security.core.userdetails.User(
            user.getEmail(), user.getPassword(), user.getIsActive(), true, true,
            true, getAuthorities(user.getRole()));
    }

    private Collection<GrantedAuthority> getAuthorities(Role role) {
        return Collections.singletonList(new SimpleGrantedAuthority(role.getName()));
    }
}
