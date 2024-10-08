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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
    [GET]: /room-types/page&take
     */
    public Page<RoomType> getRoomTypes(int page, int take) {
        Pageable pageable = PageRequest.of(page - 1, take);
        return roomTypeRepository.findAll(pageable);
    }

    /*
    [GET]: /room-types/roomTypeId
     */
    public Optional<RoomTypeResponse> getRoomTypeById(int roomTypeId) {
        return roomTypeMapper.toRoomTypeResponse(roomTypeRepository.findById(roomTypeId));
    }

    /*
    [GET]: /room-types/address&capacity&startTime&endTime&page&take
     */
    public Page<RoomType> getFilteredRoomTypes(String address, Integer capacity, LocalDateTime startTime, LocalDateTime endTime, int page, int take) {
        Pageable pageable = PageRequest.of(page - 1, take);
        return roomTypeRepository.findFilteredRoomTypes(address, capacity, startTime, endTime, pageable);
    }

    /*
    [PUT]: /room-types/roomTypeId
     */
    public RoomTypeResponse updateRoomType(int roomTypeId, RoomTypeCreationRequest request) {
        Optional<RoomType> existingRoomTypeOpt = roomTypeRepository.findById(roomTypeId);

        RoomType existingRoomType = existingRoomTypeOpt.orElseThrow(() -> new RuntimeException("RoomType not found"));
        System.out.println(request.toString());

        Integer newBuildingId = request.getBuildingId();
        Optional<Building> newBuilding = buildingRepository.findById(newBuildingId);
        if (existingRoomType.getBuilding() == null ||
                !existingRoomType.getBuilding().getId().equals(newBuildingId)) {

            existingRoomType.setBuilding(newBuilding.orElse(null));
        }

        RoomType updatedRoomType = roomTypeMapper.toUpdatedRoomType(request, existingRoomType);
        return roomTypeMapper.toRoomTypeResponse(roomTypeRepository.save(updatedRoomType));
    }

    /*
    [DELETE]: /room-types/roomTypeId
     */
    public String deleteRoomType(int roomTypeId) {
        roomTypeRepository.deleteById(roomTypeId);
        return "Delete room type " + roomTypeId + " successfully";
    }
}
