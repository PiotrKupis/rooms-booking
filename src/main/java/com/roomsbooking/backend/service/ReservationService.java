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
import com.roomsbooking.backend.utils.DateUtils;
import com.roomsbooking.dto.ReservationPayload;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

/**
 * Service responsible for managing object of type {@link Reservation}
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class ReservationService {

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;
    private final AuthService authService;
    private final ReservationRepository reservationRepository;
    private final ResortRepository resortRepository;
    private final ReservationMapper reservationMapper;
    private final static BigDecimal currencyMultiplier = BigDecimal.valueOf(100);

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey;
    }

    /**
     * Method responsible for adding a new room reservation.
     *
     * @param reservationPayload object of type {@link ReservationPayload}
     * @return object of type {@link ReservationPayload}
     */
    @CacheEvict(value = "room", allEntries = true)
    public ReservationPayload addReservation(ReservationPayload reservationPayload) {
        log.info("Booking room nr " + reservationPayload.getRoomNumber() + " in resort "
            + reservationPayload.getResortName());

        Reservation reservation;
        try {
            Room room = getRoom(reservationPayload);
            User user = authService.getCurrentUser();
            reservation = reservationMapper.toReservation(reservationPayload, room, user);
            chargeCreditCard(reservationPayload.getStripeToken(), reservation);
        } catch (ParseException e) {
            throw ReservationException.incorrectDateFormat();
        } catch (StripeException e) {
            throw ReservationException.paymentFailed();
        }

        reservation = reservationRepository.save(reservation);
        return reservationMapper.toReservationPayload(reservation);
    }

    private void chargeCreditCard(String stripeToken, Reservation reservation)
        throws StripeException {
        long datesDifference =
            reservation.getEndDate().getTime() - reservation.getStartDate().getTime();
        long daysNumber = TimeUnit.DAYS.convert(datesDifference, TimeUnit.MILLISECONDS);

        BigDecimal chargeAmount = reservation.getRoom().getPrice()
            .multiply(BigDecimal.valueOf(daysNumber))
            .multiply(currencyMultiplier);

        Map<String, Object> chargeParams = new HashMap<>();
        chargeParams.put("amount", chargeAmount.intValue());
        chargeParams.put("currency", "PLN");
        chargeParams.put("source", stripeToken);
        Charge.create(chargeParams);
    }

    private Room getRoom(ReservationPayload reservationPayload) throws ParseException {
        String resortName = reservationPayload.getResortName();
        Integer roomNumber = reservationPayload.getRoomNumber();

        Date today = DateUtils.dateFormat.parse(DateUtils.dateFormat.format(new Date()));
        Date startDate = DateUtils.dateFormat.parse(reservationPayload.getStartDate());
        Date endDate = DateUtils.dateFormat.parse(reservationPayload.getEndDate());

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
            .filter(reservation -> DateUtils.areDateRangesOverlapOrAreTheSame(startDate, endDate,
                reservation.getStartDate(), reservation.getEndDate()))
            .findFirst()
            .ifPresent(reservation -> {
                throw ReservationException.unavailableTimePeriod();
            });

        return room;
    }
}
