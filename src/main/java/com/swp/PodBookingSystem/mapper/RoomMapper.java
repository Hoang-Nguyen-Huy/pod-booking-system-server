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
    default Optional<RoomResponse> toRoomResponse(Optional<Room> roomOptional) {
        return roomOptional.map(this::toRoomResponse);
    }
    RoomResponse toRoomResponse(Room room);

    @Mapping(source = "status", target = "status", qualifiedByName = "stringToRoomStatus")
    Room toRoom(RoomCreationRequest request);

    @Named("stringToRoomStatus")
    default RoomStatus stringToRoomStatus(String status) {
        return RoomStatus.valueOf(status);
    }
}
