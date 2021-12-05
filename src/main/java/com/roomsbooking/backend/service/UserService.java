package com.roomsbooking.backend.service;

import com.roomsbooking.backend.exception.RoleException;
import com.roomsbooking.backend.exception.UserException;
import com.roomsbooking.backend.mapper.UserMapper;
import com.roomsbooking.backend.model.Role;
import com.roomsbooking.backend.model.User;
import com.roomsbooking.backend.repository.RoleRepository;
import com.roomsbooking.backend.repository.UserRepository;
import com.roomsbooking.dto.UserPayload;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service responsible for managing objects of type {@link User}
 */
@Slf4j
@AllArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;

    /**
     * Method responsible for getting all users.
     *
     * @return list of {@link UserPayload} objects
     */
    public List<UserPayload> getAllUsers() {
        log.info("Getting all users");
        return userRepository.findAll()
            .stream().map(userMapper::toUserPayload)
            .collect(Collectors.toList());
    }

    /**
     * Method responsible for changing role of the specific user.
     *
     * @param email    email indicating target user
     * @param roleName name of new user's role
     * @return object of type {@link UserPayload}
     */
    @Transactional
    public UserPayload changeRole(String email, String roleName) {
        log.info("Changing role of user with email: " + email);
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> UserException.userNotFound(email));
        Role role = roleRepository.findByName(roleName)
            .orElseThrow(() -> RoleException.roleNotFound(roleName));
        user.setRole(role);
        return userMapper.toUserPayload(user);
    }
}
