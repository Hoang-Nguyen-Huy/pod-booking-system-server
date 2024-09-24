package com.swp.PodBookingSystem.controller;

import com.swp.PodBookingSystem.dto.request.Building.BuildingCreationRequest;
import com.swp.PodBookingSystem.dto.respone.ApiResponse;
import com.swp.PodBookingSystem.dto.respone.Building.BuildingResponse;
import com.swp.PodBookingSystem.service.BuildingService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/buildings")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BuildingController {
    BuildingService buildingService;

    @PostMapping
    ApiResponse<BuildingResponse> createBuilding(@RequestBody BuildingCreationRequest request) {
        return ApiResponse.<BuildingResponse>builder()
                .data(buildingService.createBuilding(request))
                .message("Create new building successfully")
                .build();
    }

    @GetMapping("/{buildingId}")
    ApiResponse<Optional<BuildingResponse>> getBuildingById(@PathVariable("buildingId") int buildingId) {
        return ApiResponse.<Optional<BuildingResponse>>builder()
                .data(buildingService.getBuildingById(buildingId))
                .message("Get building by Id successfully")
                .build();
    }
}
