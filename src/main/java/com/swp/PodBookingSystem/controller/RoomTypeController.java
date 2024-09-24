package com.swp.PodBookingSystem.controller;

import com.swp.PodBookingSystem.dto.request.RoomType.RoomTypeCreationRequest;
import com.swp.PodBookingSystem.dto.request.RoomType.RoomTypePaginationDTO;
import com.swp.PodBookingSystem.dto.respone.ApiResponse;
import com.swp.PodBookingSystem.dto.respone.PaginationResponse;
import com.swp.PodBookingSystem.dto.respone.RoomType.RoomTypeResponse;
import com.swp.PodBookingSystem.entity.RoomType;
import com.swp.PodBookingSystem.service.RoomTypeService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

    @GetMapping
    PaginationResponse<List<RoomType>> getRoomTypes(@RequestParam(defaultValue = "1", name = "page") int page,
                                                    @RequestParam(defaultValue = "10", name = "take") int take) {
        RoomTypePaginationDTO dto = new RoomTypePaginationDTO(page, take);
        Page<RoomType> roomTypePage = roomTypeService.getRoomTypes(dto.page, dto.take);

        return PaginationResponse.<List<RoomType>>builder()
                .data(roomTypePage.getContent())
                .currentPage(roomTypePage.getNumber() + 1)
                .totalPage(roomTypePage.getTotalPages())
                .recordPerPage(roomTypePage.getNumberOfElements())
                .totalRecord((int) roomTypePage.getTotalElements())
                .build();
    }

    @GetMapping("/{roomTypeId}")
    ApiResponse<Optional<RoomTypeResponse>> getRoomTypeById(@PathVariable("roomTypeId") int roomTypeId) {
        return ApiResponse.<Optional<RoomTypeResponse>>builder()
                .data(roomTypeService.getRoomTypeById(roomTypeId))
                .message("Get room type by Id successfully")
                .build();
    }

    @PutMapping("/{roomTypeId}")
    ApiResponse<RoomTypeResponse> updatedRoomType(@PathVariable("roomTypeId") int roomTypeId,
                                                  @RequestBody RoomTypeCreationRequest request) {
        return ApiResponse.<RoomTypeResponse>builder()
                .data(roomTypeService.updateRoomType(roomTypeId, request))
                .build();
    }
}
