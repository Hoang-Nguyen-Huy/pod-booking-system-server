package com.swp.PodBookingSystem.mapper;

import com.swp.PodBookingSystem.dto.request.RoomType.RoomTypeCreationRequest;
import com.swp.PodBookingSystem.dto.respone.RoomType.RoomTypeResponse;
import com.swp.PodBookingSystem.entity.RoomType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.Optional;

@Mapper(componentModel = "spring")
public interface RoomTypeMapper {

    RoomTypeResponse toRoomTypeResponse(RoomType roomType);

    default Optional<RoomTypeResponse> toRoomTypeResponse(Optional<RoomType> roomTypeOptional) {
        return roomTypeOptional.map(this::toRoomTypeResponse);
    }

    RoomType toRoomType(RoomTypeCreationRequest request);

    @Mapping(target = "id", ignore = true)
    RoomType toUpdatedRoomType(RoomTypeCreationRequest request, @MappingTarget RoomType roomType);
}
