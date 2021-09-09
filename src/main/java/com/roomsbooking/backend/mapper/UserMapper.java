package com.roomsbooking.backend.mapper;

import com.rooms_booking.dto.RegisterRequest;
import com.rooms_booking.dto.RegisterResponse;
import com.roomsbooking.backend.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "phoneNumber.countryCode", source = "countryCode")
    @Mapping(target = "phoneNumber.phoneNumber", source = "phoneNumber")
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "isActive", constant = "true")
    User toUser(RegisterRequest registerRequest);

    RegisterResponse toRegisterResponse(User user);
}
