package com.roomsbooking.backend.service;

import com.roomsbooking.backend.exception.ReservationException;
import com.roomsbooking.backend.exception.RoomException;
import com.roomsbooking.backend.mapper.RoomMapper;
import com.roomsbooking.backend.model.Address;
import com.roomsbooking.backend.model.Room;
import com.roomsbooking.backend.repository.RoomRepository;
import com.roomsbooking.backend.utils.DateUtils;
import com.roomsbooking.dto.DetailedRoomPayload;
import com.roomsbooking.dto.SearchPayload;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * Service responsible for managing operations connected with room searching.
 */
@Slf4j
@AllArgsConstructor
@Service
public class SearchService {

    private final RoomRepository roomRepository;
    private final RoomMapper roomMapper;

    /**
     * Method responsible for getting list of specific rooms.
     *
     * @param searchPayload object of type {@link SearchPayload}
     * @param pageNumber    specific part of the searched results
     * @param roomsPerPage  number of rooms to return
     * @return list of {@link SearchPayload} objects
     */
    @Cacheable("room")
    public List<DetailedRoomPayload> searchRooms(SearchPayload searchPayload, Integer pageNumber,
        Integer roomsPerPage, Integer imageQuantity) {
        log.info("Searching for the specific rooms");
        List<DetailedRoomPayload> rooms = findRoomsMeetingRequirements(searchPayload);
        if (pageNumber != null && roomsPerPage != null) {
            rooms = rooms.stream()
                .skip((long) (pageNumber - 1) * roomsPerPage)
                .limit((long) roomsPerPage)
                .collect(Collectors.toList());
        }

        if (imageQuantity != null) {
            for (DetailedRoomPayload room : rooms) {
                room.setImages(
                    room.getImages().subList(0, getToPositionOrMaxSize(imageQuantity, room)));
            }
        }

        if (rooms.isEmpty()) {
            throw RoomException.matchingRoomsNotFound();
        }
        return rooms;
    }

    /**
     * Method responsible for getting number of rooms that meet requirements.
     *
     * @param searchPayload object of type {@link SearchPayload}
     * @return number of rooms that meet requirements
     */
    public String getNumberOfFoundRooms(SearchPayload searchPayload) {
        log.info("Getting number of rooms that meet requirements");
        int quantity = findRoomsMeetingRequirements(searchPayload).size();
        return String.format("\"%d\"", quantity);
    }

    private List<DetailedRoomPayload> findRoomsMeetingRequirements(SearchPayload searchPayload) {
        try {
            Date startDate = DateUtils.dateFormat.parse(searchPayload.getStartDate());
            Date endDate = DateUtils.dateFormat.parse(searchPayload.getEndDate());

            return roomRepository.findAll().stream()
                .filter(room -> areLocationsMatching(searchPayload, room))
                .filter(room -> isResidentNumberMatching(searchPayload, room))
                .filter(room -> isRoomAvailable(startDate, endDate, room))
                .map(roomMapper::toDetailedRoomPayload)
                .collect(Collectors.toList());
        } catch (ParseException e) {
            throw ReservationException.incorrectDateFormat();
        }
    }

    private int getToPositionOrMaxSize(Integer imageQuantity, DetailedRoomPayload room) {
        return imageQuantity > room.getImages().size() ? room.getImages().size() : imageQuantity;
    }

    private boolean isRoomAvailable(Date startDate, Date endDate, Room room) {
        return room.getReservations().stream()
            .noneMatch(reservation ->
                DateUtils.areDateRangesOverlapOrAreTheSame(startDate, endDate,
                    reservation.getStartDate(), reservation.getEndDate()));
    }

    private boolean isResidentNumberMatching(SearchPayload searchPayload, Room room) {
        return room.getMaxResidentsNumber().equals(searchPayload.getResidentsNumber());
    }

    private boolean areLocationsMatching(SearchPayload searchPayload, Room room) {
        return getRoomAddress(room).getCountry().equalsIgnoreCase(searchPayload.getLocation())
            || getRoomAddress(room).getCity().equalsIgnoreCase(searchPayload.getLocation());
    }

    private Address getRoomAddress(Room room) {
        return room.getResort().getAddress();
    }
}
