package com.roomsbooking.backend.mapper;

import com.roomsbooking.backend.model.Reservation;
import com.roomsbooking.backend.model.Room;
import com.roomsbooking.backend.model.User;
import com.roomsbooking.dto.ReservationPayload;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Class responsible for mapping {@link Reservation} object to data transfer objects.
 */
@Mapper(componentModel = "spring")
public abstract class ReservationMapper {

    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "startDate", expression = "java(dateFormat.parse(reservationPayload.getStartDate()))")
    @Mapping(target = "endDate", expression = "java(dateFormat.parse(reservationPayload.getEndDate()))")
    public abstract Reservation toReservation(ReservationPayload reservationPayload, Room room,
        User user)
        throws ParseException;

    @Mapping(target = "resortName", source = "reservation.room.resort.resortName")
    @Mapping(target = "roomNumber", source = "reservation.room.roomNumber")
    @Mapping(target = "startDate", expression = "java(dateFormat.format(reservation.getStartDate()))")
    @Mapping(target = "endDate", expression = "java(dateFormat.format(reservation.getEndDate()))")
    public abstract ReservationPayload toReservationPayload(Reservation reservation);
}
