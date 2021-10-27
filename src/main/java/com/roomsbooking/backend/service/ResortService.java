package com.roomsbooking.backend.service;

import com.roomsbooking.backend.exception.ResortException;
import com.roomsbooking.backend.exception.UserException;
import com.roomsbooking.backend.mapper.ResortMapper;
import com.roomsbooking.backend.model.Resort;
import com.roomsbooking.backend.model.User;
import com.roomsbooking.backend.repository.ResortRepository;
import com.roomsbooking.backend.repository.UserRepository;
import com.roomsbooking.dto.ResortPayload;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service responsible for managing object of type {@link Resort}
 */
@Slf4j
@AllArgsConstructor
@Service
public class ResortService {

    private final AuthService authService;
    private final ResortRepository resortRepository;
    private final UserRepository userRepository;
    private final ResortMapper resortMapper;

    /**
     * Method responsible for creating a new resort.
     *
     * @param resortPayload object of type {@link ResortPayload}
     * @return object of type {@link ResortPayload}
     */
    public ResortPayload createResort(ResortPayload resortPayload) {
        resortRepository.findByResortName(resortPayload.getResortName()).ifPresent(
            resort -> {
                throw ResortException.resortNameAlreadyTaken();
            }
        );
        Resort resort = resortMapper.toResort(resortPayload, authService.getCurrentUser());
        Resort savedResort = resortRepository.save(resort);
        log.info("Saved a new resort " + savedResort);
        return resortMapper.toResortPayload(savedResort);
    }

    /**
     * Method responsible for getting resorts by owner's email.
     *
     * @param email email of resorts owner
     * @return list of objects of type {@link ResortPayload}
     */
    public List<ResortPayload> getResortsByEmail(String email) {
        log.info("Getting resorts by owner's email: " + email);
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> UserException.userNotFound(email));

        return user.getResorts().stream()
            .map(resortMapper::toResortPayload)
            .collect(Collectors.toList());
    }
}
