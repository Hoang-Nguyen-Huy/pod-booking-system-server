package com.swp.PodBookingSystem.service;

import com.swp.PodBookingSystem.dto.request.Building.BuildingCreationRequest;
import com.swp.PodBookingSystem.dto.respone.Building.BuildingResponse;
import com.swp.PodBookingSystem.entity.Building;
import com.swp.PodBookingSystem.mapper.BuildingMapper;
import com.swp.PodBookingSystem.repository.BuildingRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BuildingService {
    BuildingRepository buildingRepository;
    BuildingMapper buildingMapper;

    /*
    [POST]: /buildings
     */
    public BuildingResponse createBuilding(BuildingCreationRequest request) {
        Building newBuilding = buildingMapper.toBuilding(request);
        return buildingMapper.toBuildingResponse(buildingRepository.save(newBuilding));
    }
}
