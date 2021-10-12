package com.roomsbooking.backend.mapper;

import com.roomsbooking.backend.model.User;
import com.roomsbooking.dto.RegisterRequest;
import com.roomsbooking.dto.RegisterResponse;
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
