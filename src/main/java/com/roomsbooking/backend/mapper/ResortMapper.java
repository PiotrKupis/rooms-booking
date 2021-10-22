package com.roomsbooking.backend.mapper;

import com.roomsbooking.backend.enums.ResortAmenity;
import com.roomsbooking.backend.model.Resort;
import com.roomsbooking.backend.model.User;
import com.roomsbooking.dto.ResortPayload;
import com.roomsbooking.dto.ResortPayload.ResortAmenitiesEnum;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Class responsible for mapping {@link Resort} object to data transfer objects.
 */
@Mapper(componentModel = "spring")
public abstract class ResortMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "address.country", source = "resortPayload.country")
    @Mapping(target = "address.city", source = "resortPayload.city")
    @Mapping(target = "address.street", source = "resortPayload.street")
    @Mapping(target = "address.streetNumber", source = "resortPayload.streetNumber")
    @Mapping(target = "resortAmenities", expression = "java(toResortAmenityEnums(resortPayload.getResortAmenities()))")
    @Mapping(target = "hotelDayStart", expression = "java(java.time.LocalTime.parse(resortPayload.getHotelDayStart()))")
    @Mapping(target = "hotelDayEnd", expression = "java(java.time.LocalTime.parse(resortPayload.getHotelDayEnd()))")
    @Mapping(target = "parkingFee", expression = "java(new java.math.BigDecimal(resortPayload.getParkingFee()))")
    @Mapping(target = "rooms", ignore = true)
    public abstract Resort toResort(ResortPayload resortPayload, User owner);

    public Set<ResortAmenity> toResortAmenityEnums(List<ResortAmenitiesEnum> list) {
        return list.stream()
            .map(resortAmenityEnum -> ResortAmenity.valueOf(resortAmenityEnum.toString()))
            .collect(Collectors.toCollection(HashSet::new));
    }

    @Mapping(target = "country", source = "address.country")
    @Mapping(target = "city", source = "address.city")
    @Mapping(target = "street", source = "address.street")
    @Mapping(target = "streetNumber", source = "address.streetNumber")
    @Mapping(target = "resortAmenities", expression = "java(toResortAmenities(resort.getResortAmenities()))")
    @Mapping(target = "hotelDayStart", expression = "java(timeToString(resort.getHotelDayStart()))")
    @Mapping(target = "hotelDayEnd", expression = "java(timeToString(resort.getHotelDayEnd()))")
    @Mapping(target = "parkingFee", expression = "java(resort.getParkingFee().toString())")
    public abstract ResortPayload toResortPayload(Resort resort);

    public List<ResortAmenitiesEnum> toResortAmenities(Set<ResortAmenity> set) {
        return set.stream()
            .map(resortAmenity -> ResortAmenitiesEnum.valueOf(resortAmenity.toString()))
            .collect(Collectors.toList());
    }

    public String timeToString(LocalTime time) {
        return time.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
    }
}
