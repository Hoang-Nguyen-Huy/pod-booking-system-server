package com.swp.PodBookingSystem.controller;

import com.swp.PodBookingSystem.dto.request.Building.BuildingCreationRequest;
import com.swp.PodBookingSystem.dto.request.Building.BuildingPaginationDTO;
import com.swp.PodBookingSystem.dto.respone.ApiResponse;
import com.swp.PodBookingSystem.dto.respone.Building.BuildingResponse;
import com.swp.PodBookingSystem.dto.respone.PaginationResponse;
import com.swp.PodBookingSystem.entity.Building;
import com.swp.PodBookingSystem.service.BuildingService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

    @GetMapping
    PaginationResponse<List<Building>> getBuildings(@RequestParam(defaultValue = "1", name = "page") int page,
                                                    @RequestParam(defaultValue = "10", name = "take") int take) {
        BuildingPaginationDTO dto = new BuildingPaginationDTO(page, take);
        Page<Building> buildingPage = buildingService.getBuildings(dto.page, dto.take);
        return PaginationResponse.<List<Building>>builder()
                .data(buildingPage.getContent())
                .currentPage(buildingPage.getNumber() + 1)
                .totalPage(buildingPage.getTotalPages())
                .recordPerPage(buildingPage.getNumberOfElements())
                .totalRecord((int) buildingPage.getTotalElements())
                .build();
    }

    @GetMapping("/{buildingId}")
    ApiResponse<Optional<BuildingResponse>> getBuildingById(@PathVariable("buildingId") int buildingId) {
        return ApiResponse.<Optional<BuildingResponse>>builder()
                .data(buildingService.getBuildingById(buildingId))
                .message("Get building by Id successfully")
                .build();
    }

    @PutMapping("/{buildingId}")
    ApiResponse<BuildingResponse> updateBuilding(@PathVariable("buildingId") int buildingId,
                                                 @RequestBody BuildingCreationRequest request) {
        return ApiResponse.<BuildingResponse>builder()
                .data(buildingService.updateBuilding(buildingId, request))
                .message("Update building successfully")
                .build();
    }
}
