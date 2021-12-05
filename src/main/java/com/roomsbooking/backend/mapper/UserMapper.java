package com.roomsbooking.backend.mapper;

import com.roomsbooking.backend.model.User;
import com.roomsbooking.dto.RegisterRequest;
import com.roomsbooking.dto.UserPayload;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Interface responsible for mapping {@link User} object to data transfer objects.
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "phoneNumber.countryCode", source = "countryCode")
    @Mapping(target = "phoneNumber.phoneNumber", source = "phoneNumber")
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "isActive", constant = "true")
    @Mapping(target = "resorts", ignore = true)
    @Mapping(target = "reservations", ignore = true)
    User toUser(RegisterRequest registerRequest);

    @Mapping(target = "role", source = "role.name")
    UserPayload toUserPayload(User user);
}
