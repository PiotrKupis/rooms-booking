package com.roomsbooking.backend.service;

import com.roomsbooking.backend.exception.ReservationException;
import com.roomsbooking.backend.exception.ResortException;
import com.roomsbooking.backend.exception.RoomException;
import com.roomsbooking.backend.mapper.ReservationMapper;
import com.roomsbooking.backend.model.Reservation;
import com.roomsbooking.backend.model.Resort;
import com.roomsbooking.backend.model.Room;
import com.roomsbooking.backend.model.User;
import com.roomsbooking.backend.repository.ReservationRepository;
import com.roomsbooking.backend.repository.ResortRepository;
import com.roomsbooking.dto.ReservationPayload;
import java.text.ParseException;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service responsible for managing object of type {@link Reservation}
 */
@Slf4j
@AllArgsConstructor
@Service
public class ReservationService {

    private final AuthService authService;
    private final ReservationRepository reservationRepository;
    private final ResortRepository resortRepository;
    private final ReservationMapper reservationMapper;

    /**
     * Method responsible for adding a new room reservation.
     *
     * @param reservationPayload object of type {@link ReservationPayload}
     * @return object of type {@link ReservationPayload}
     */
    public ReservationPayload addReservation(ReservationPayload reservationPayload) {
        log.info("Booking room nr " + reservationPayload.getRoomNumber() + " in resort "
            + reservationPayload.getResortName());

        Reservation reservation;
        try {
            Room room = getRoom(reservationPayload);
            User user = authService.getCurrentUser();
            reservation = reservationMapper.toReservation(reservationPayload, room, user);
        } catch (ParseException e) {
            throw ReservationException.incorrectDateFormat();
        }
        reservation = reservationRepository.save(reservation);
        return reservationMapper.toReservationPayload(reservation);
    }

    private Room getRoom(ReservationPayload reservationPayload) throws ParseException {
        String resortName = reservationPayload.getResortName();
        Integer roomNumber = reservationPayload.getRoomNumber();

        Date today = new Date();
        Date startDate = ReservationMapper.dateFormat.parse(reservationPayload.getStartDate());
        Date endDate = ReservationMapper.dateFormat.parse(reservationPayload.getEndDate());

        if (today.after(startDate) || startDate.after(endDate) || startDate.equals(endDate)) {
            throw ReservationException.incorrectDateRange();
        }

        Resort resort = resortRepository.findByResortName(resortName)
            .orElseThrow(() -> ResortException.resortNotFound(resortName));

        Room room = resort.getRooms().stream()
            .filter(r -> r.getRoomNumber().equals(roomNumber))
            .findFirst()
            .orElseThrow(() -> RoomException.roomWithNumberNotFound(roomNumber));

        room.getReservations().stream()
            .filter(reservation -> areDateRangesUnavailable(startDate, endDate, reservation))
            .findFirst()
            .ifPresent(reservation -> {
                throw ReservationException.unavailableTimePeriod();
            });
        return room;
    }

    private boolean areDateRangesUnavailable(Date startDate, Date endDate,
        Reservation reservation) {
        return areDateRangesOverlap(startDate, endDate, reservation) || areDateRangesTheSame(
            startDate, endDate, reservation);
    }

    private boolean areDateRangesOverlap(Date startDate, Date endDate,
        Reservation reservation) {
        return startDate.before(reservation.getEndDate())
            && reservation.getStartDate().before(endDate);
    }

    private boolean areDateRangesTheSame(Date startDate, Date endDate,
        Reservation reservation) {
        return startDate.equals(reservation.getStartDate())
            && endDate.equals(reservation.getEndDate());
    }
}
