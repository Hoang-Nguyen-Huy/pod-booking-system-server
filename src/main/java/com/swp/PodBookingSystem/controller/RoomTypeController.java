package com.swp.PodBookingSystem.controller;

import com.swp.PodBookingSystem.dto.request.RoomType.RoomTypeCreationRequest;
import com.swp.PodBookingSystem.dto.respone.ApiResponse;
import com.swp.PodBookingSystem.dto.respone.RoomType.RoomTypeResponse;
import com.swp.PodBookingSystem.service.RoomTypeService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/room-types")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoomTypeController {
    RoomTypeService roomTypeService;

    @PostMapping
    ApiResponse<RoomTypeResponse> createRoomType(@RequestBody RoomTypeCreationRequest request) {
        return ApiResponse.<RoomTypeResponse>builder()
                .data(roomTypeService.createRoomType(request))
                .message("Create room type successfully")
                .build();
    }

    @GetMapping("/{roomTypeId}")
    ApiResponse<Optional<RoomTypeResponse>> getRoomTypeById(@PathVariable("roomTypeId") int roomTypeId) {
        return ApiResponse.<Optional<RoomTypeResponse>>builder()
                .data(roomTypeService.getRoomTypeById(roomTypeId))
                .message("Get room type by Id successfully")
                .build();
    }
}
