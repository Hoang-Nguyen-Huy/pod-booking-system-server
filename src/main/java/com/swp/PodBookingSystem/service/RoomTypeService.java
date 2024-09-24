package com.swp.PodBookingSystem.service;

import com.swp.PodBookingSystem.dto.request.RoomType.RoomTypeCreationRequest;
import com.swp.PodBookingSystem.dto.respone.RoomType.RoomTypeResponse;
import com.swp.PodBookingSystem.entity.Building;
import com.swp.PodBookingSystem.entity.RoomType;
import com.swp.PodBookingSystem.mapper.RoomTypeMapper;
import com.swp.PodBookingSystem.repository.BuildingRepository;
import com.swp.PodBookingSystem.repository.RoomTypeRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoomTypeService {
    RoomTypeRepository roomTypeRepository;
    RoomTypeMapper roomTypeMapper;
    BuildingRepository buildingRepository;

    /*
    [POST]: /room-types
     */
    public RoomTypeResponse createRoomType(RoomTypeCreationRequest request) {
        Optional<Building> building = buildingRepository.findById(request.getBuildingId());
        RoomType newRoomType = roomTypeMapper.toRoomType(request);
        newRoomType.setBuilding(building.orElse(null));
        return roomTypeMapper.toRoomTypeResponse(roomTypeRepository.save(newRoomType));
    }

    /*
    [GET]: /room-types/roomTypeId
     */
    public Optional<RoomTypeResponse> getRoomTypeById(int roomTypeId) {
        return roomTypeMapper.toRoomTypeResponse(roomTypeRepository.findById(roomTypeId));
    }
}
