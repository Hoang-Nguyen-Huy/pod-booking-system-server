package com.swp.PodBookingSystem.mapper;

import com.swp.PodBookingSystem.dto.request.Room.RoomCreationRequest;
import com.swp.PodBookingSystem.dto.respone.Room.RoomResponse;
import com.swp.PodBookingSystem.entity.Room;
import com.swp.PodBookingSystem.enums.RoomStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Optional;

@Mapper(componentModel = "spring")
public interface RoomMapper {
    RoomResponse toRoomResponse(Room room);

    @Named("mapOptionalStringToString")
    default String mapString(Optional<String> value) {
        return value.orElse(null);
    }

    @Named("mapOptionalStringToRoomStatus")
    default RoomStatus mapRoomStatus(Optional<String> value) {
        return value.map(RoomStatus::valueOf).orElse(null);
    }

    @Mapping(source = "image", target = "image", qualifiedByName = "mapOptionalStringToString")
    @Mapping(source = "status", target = "status", qualifiedByName = "mapOptionalStringToRoomStatus")
    Room toRoom(RoomCreationRequest request);
}
