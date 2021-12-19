package com.roomsbooking.backend.service;

import com.roomsbooking.backend.exception.ResortException;
import com.roomsbooking.backend.exception.UserException;
import com.roomsbooking.backend.mapper.ResortMapper;
import com.roomsbooking.backend.mapper.RoomMapper;
import com.roomsbooking.backend.model.Resort;
import com.roomsbooking.backend.model.User;
import com.roomsbooking.backend.repository.ResortRepository;
import com.roomsbooking.backend.repository.UserRepository;
import com.roomsbooking.dto.DetailedRoomPayload;
import com.roomsbooking.dto.ResortPayload;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
    private final RoomMapper roomMapper;

    /**
     * Method responsible for creating a new resort.
     *
     * @param resortPayload object of type {@link ResortPayload}
     * @return object of type {@link ResortPayload}
     */
    @CacheEvict(value = "resort", allEntries = true)
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
    @Cacheable("resort")
    public List<ResortPayload> getResortsByEmail(String email) {
        log.info("Getting resorts by owner's email: " + email);
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> UserException.userNotFound(email));

        return user.getResorts().stream()
            .map(resortMapper::toResortPayload)
            .collect(Collectors.toList());
    }

    /**
     * Method responsible for getting resort payload by its name.
     *
     * @param resortName name of searched resort
     * @return object of type {@link ResortPayload}
     */
    @Cacheable("resort")
    public ResortPayload getResortPayloadByName(String resortName) {
        log.info("Getting resort by name: " + resortName);
        return resortMapper.toResortPayload(getResortByName(resortName));
    }

    /**
     * Method responsible for getting resort of the current user.
     *
     * @param resortName name of searched resort
     * @return object of type {@link Resort}
     */
    public Resort getCurrentUserResort(String resortName) {
        log.info("Getting resort of the current user by name: " + resortName);
        return authService.getCurrentUser().getResorts().stream()
            .filter(r -> r.getResortName().equals(resortName))
            .findFirst()
            .orElseThrow(() -> ResortException.resortNotFound(resortName));
    }

    /**
     * Method responsible for getting resort by its name.
     *
     * @param resortName name of searched resort
     * @return object of type {@link Resort}
     */
    public Resort getResortByName(String resortName) {
        log.info("Getting resort by name: " + resortName);
        return getResort(resortName);
    }

    /**
     * Method responsible for getting rooms of the specific resort.
     *
     * @param resortName name of searched resort
     * @return list of objects of type {@link DetailedRoomPayload}
     */
    public List<DetailedRoomPayload> getResortRooms(String resortName) {
        log.info("Getting rooms of the specific resort");
        return getResort(resortName).getRooms().stream()
            .map(roomMapper::toDetailedRoomPayload)
            .collect(Collectors.toList());
    }

    private Resort getResort(String resortName) {
        return resortRepository.findByResortName(resortName)
            .orElseThrow(() -> ResortException.resortNotFound(resortName));
    }
}
