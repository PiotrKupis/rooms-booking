package com.roomsbooking.backend.mapper;

import com.roomsbooking.backend.enums.RoomAmenity;
import com.roomsbooking.backend.exception.ResortException;
import com.roomsbooking.backend.model.Photo;
import com.roomsbooking.backend.model.Resort;
import com.roomsbooking.backend.model.Room;
import com.roomsbooking.backend.repository.ResortRepository;
import com.roomsbooking.dto.AddRoomRequest;
import com.roomsbooking.dto.AddRoomRequest.RoomAmenitiesEnum;
import com.roomsbooking.dto.DetailedRoomPayload;
import com.roomsbooking.dto.PhotoPayload;
import com.roomsbooking.dto.RoomPayload;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Class responsible for mapping {@link Room} objects to data transfer objects.
 */
@Mapper(componentModel = "spring")
public abstract class RoomMapper {

    @Autowired
    private ResortRepository resortRepository;
    @Autowired
    private PhotoMapper photoMapper;

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "reservations", ignore = true)
    @Mapping(target = "photos", ignore = true)
    @Mapping(target = "resort", expression = "java(findResortByName(addRoomRequest.getResortName()))")
    @Mapping(target = "price", expression = "java(new java.math.BigDecimal(addRoomRequest.getPrice()))")
    @Mapping(target = "roomAmenities", expression = "java(toRoomAmenities(addRoomRequest.getRoomAmenities()))")
    public abstract Room toRoom(AddRoomRequest addRoomRequest);

    public Resort findResortByName(String resortName) {
        return resortRepository.findByResortName(resortName)
            .orElseThrow(() -> ResortException.resortNotFound(resortName));
    }

    public Set<RoomAmenity> toRoomAmenities(List<RoomAmenitiesEnum> list) {
        return list.stream()
            .map(roomAmenityEnum -> RoomAmenity.valueOf(roomAmenityEnum.toString()))
            .collect(Collectors.toCollection(HashSet::new));
    }

    @Mapping(target = "resortName", source = "resort.resortName")
    @Mapping(target = "country", source = "resort.address.country")
    @Mapping(target = "city", source = "resort.address.city")
    @Mapping(target = "street", source = "resort.address.street")
    @Mapping(target = "streetNumber", source = "resort.address.streetNumber")
    @Mapping(target = "price", expression = "java(room.getPrice().toString())")
    @Mapping(target = "roomAmenities", expression = "java(toRoomAmenityEnums(room.getRoomAmenities()))")
    public abstract RoomPayload toRoomPayload(Room room);

    public List<String> toRoomAmenityEnums(Set<RoomAmenity> set) {
        return set.stream()
            .map(Enum::toString)
            .collect(Collectors.toList());
    }

    @Mapping(target = "resortName", source = "resort.resortName")
    @Mapping(target = "country", source = "resort.address.country")
    @Mapping(target = "city", source = "resort.address.city")
    @Mapping(target = "street", source = "resort.address.street")
    @Mapping(target = "streetNumber", source = "resort.address.streetNumber")
    @Mapping(target = "price", expression = "java(room.getPrice().toString())")
    @Mapping(target = "roomAmenities", expression = "java(toRoomAmenityEnums(room.getRoomAmenities()))")
    @Mapping(target = "photos", expression = "java(toPhotoPayloadList(room.getPhotos()))")
    public abstract DetailedRoomPayload toDetailedRoomPayload(Room room);

    public List<PhotoPayload> toPhotoPayloadList(Set<Photo> photos) {
        return photos.stream()
            .map(photoMapper::toPhotoPayload)
            .collect(Collectors.toList());
    }
}
