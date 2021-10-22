package com.roomsbooking.backend.service;

import com.roomsbooking.backend.exception.ResortException;
import com.roomsbooking.backend.mapper.ResortMapper;
import com.roomsbooking.backend.model.Resort;
import com.roomsbooking.backend.repository.ResortRepository;
import com.roomsbooking.dto.ResortPayload;
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
}
