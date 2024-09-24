package com.swp.PodBookingSystem.mapper;

import com.swp.PodBookingSystem.dto.request.Building.BuildingCreationRequest;
import com.swp.PodBookingSystem.dto.respone.Building.BuildingResponse;
import com.swp.PodBookingSystem.entity.Building;
import com.swp.PodBookingSystem.enums.BuildingStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Optional;

@Mapper(componentModel = "spring")
public interface BuildingMapper {
    @Mapping(source = "status", target = "status", qualifiedByName = "stringToBuildingStatus")
    @Mapping(source = "hotlineNumber", target = "hotlineNumber")
    Building toBuilding(BuildingCreationRequest request);

    BuildingResponse toBuildingResponse(Building building);

    default Optional<BuildingResponse> toBuildingResponse(Optional<Building> buildingOptional) {
        return buildingOptional.map(this::toBuildingResponse);
    }

    @Named("stringToBuildingStatus")
    default BuildingStatus stringToBuildingStatus(String status) {
        return BuildingStatus.valueOf(status);
    }
}
