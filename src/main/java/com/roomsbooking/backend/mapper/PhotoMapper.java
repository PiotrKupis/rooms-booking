package com.roomsbooking.backend.mapper;

import com.roomsbooking.backend.model.Photo;
import com.roomsbooking.dto.AddPhotoRequest;
import com.roomsbooking.dto.PhotoPayload;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Interface responsible for mapping {@link Photo} object to data transfer objects.
 */
@Mapper(componentModel = "spring")
public interface PhotoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cloudinaryId", ignore = true)
    @Mapping(target = "url", ignore = true)
    Photo toPhoto(AddPhotoRequest photo);

    PhotoPayload toPhotoPayload(Photo photo);
}
