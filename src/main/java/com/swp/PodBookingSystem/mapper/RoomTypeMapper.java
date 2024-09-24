package com.swp.PodBookingSystem.mapper;

import com.swp.PodBookingSystem.dto.request.RoomType.RoomTypeCreationRequest;
import com.swp.PodBookingSystem.dto.respone.RoomType.RoomTypeResponse;
import com.swp.PodBookingSystem.entity.RoomType;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoomTypeMapper {

    RoomTypeResponse toRoomTypeResponse(RoomType roomType);

    RoomType toRoomType(RoomTypeCreationRequest request);
}
