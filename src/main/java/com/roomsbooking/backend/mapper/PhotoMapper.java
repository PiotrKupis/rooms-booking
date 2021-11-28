package com.roomsbooking.backend.mapper;

import com.roomsbooking.backend.exception.RoomException;
import com.roomsbooking.backend.model.Image;
import com.roomsbooking.backend.model.Room;
import com.roomsbooking.dto.PhotoPayload;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.web.multipart.MultipartFile;

/**
 * Class responsible for mapping {@link Image} object to data transfer objects.
 */
@Mapper(componentModel = "spring")
public abstract class PhotoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", expression = "java(image.getName())")
    @Mapping(target = "type", expression = "java(image.getContentType())")
    @Mapping(target = "bytes", expression = "java(compressBytes(image.getBytes()))")
    @Mapping(target = "room", source = "room")
    public abstract Image toImage(MultipartFile image, Room room) throws IOException;

    @Mapping(target = "bytes", expression = "java(decompressBytes(image.getBytes()))")
    public abstract PhotoPayload toPhotoPayload(Image image);

    public byte[] compressBytes(byte[] data) {
        Deflater deflater = new Deflater();
        deflater.setInput(data);
        deflater.finish();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        try {
            byte[] buffer = new byte[1024];
            while (!deflater.finished()) {
                int count = deflater.deflate(buffer);
                outputStream.write(buffer, 0, count);
            }
            outputStream.close();
        } catch (IOException e) {
            throw RoomException.errorOfPhotoProcessing();
        }
        return outputStream.toByteArray();
    }

    public byte[] decompressBytes(byte[] data) {
        Inflater inflater = new Inflater();
        inflater.setInput(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        try {
            byte[] buffer = new byte[1024];
            while (!inflater.finished()) {
                int count = inflater.inflate(buffer);
                outputStream.write(buffer, 0, count);
            }
            outputStream.close();
        } catch (IOException | DataFormatException e) {
            throw RoomException.errorOfPhotoProcessing();
        }
        return outputStream.toByteArray();
    }
}
