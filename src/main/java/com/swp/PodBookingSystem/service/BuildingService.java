package com.swp.PodBookingSystem.service;

import com.swp.PodBookingSystem.dto.request.Building.BuildingCreationRequest;
import com.swp.PodBookingSystem.dto.respone.Building.BuildingResponse;
import com.swp.PodBookingSystem.entity.Building;
import com.swp.PodBookingSystem.mapper.BuildingMapper;
import com.swp.PodBookingSystem.repository.BuildingRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    /*
    [GET]: /buildings/page&take
     */
    public Page<Building> getBuildings(int page, int take) {
        Pageable pageable = PageRequest.of(page - 1, take);
        return buildingRepository.findAll(pageable);
    }

    /*
    [GET]: /buildings/buildingId
     */
    public Optional<BuildingResponse> getBuildingById(int buildingId) {
        return buildingMapper.toBuildingResponse(buildingRepository.findById(buildingId));
    }

    /*
    [PUT]: /buildings/buildingId
     */
    public BuildingResponse updateBuilding(int buildingId, BuildingCreationRequest request) {
        Optional<Building> existingBuilding = buildingRepository.findById(buildingId);
        Building updatedBuilding = buildingMapper.toUpdatedBuilding(request, existingBuilding.orElse(null));
        return buildingMapper.toBuildingResponse(buildingRepository.save(updatedBuilding));
    }

    /*
    [DELETE]: /buildings/buildingId
     */
    public String deleteBuilding(int buildingId) {
        buildingRepository.deleteById(buildingId);
        return "Delete building " + buildingId + " successfully";
    }

    public List<Building> searchBuildings(String keyword) {
        return buildingRepository.searchByKeyword(keyword);
    }

    public List<Building> getAllBuildings() {
        return buildingRepository.findAll();
    }
}
