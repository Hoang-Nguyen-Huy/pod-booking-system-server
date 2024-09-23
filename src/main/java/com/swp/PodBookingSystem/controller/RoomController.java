package com.swp.PodBookingSystem.controller;

import com.swp.PodBookingSystem.dto.respone.ApiResponse;
import com.swp.PodBookingSystem.dto.request.Room.RoomCreationRequest;
import com.swp.PodBookingSystem.dto.request.Room.RoomPaginationDTO;
import com.swp.PodBookingSystem.dto.respone.PaginationResponse;
import com.swp.PodBookingSystem.dto.respone.Room.RoomResponse;
import com.swp.PodBookingSystem.entity.Room;
import com.swp.PodBookingSystem.service.RoomService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoomController {
    RoomService roomService;

    @PostMapping
    ApiResponse<RoomResponse> createRoom(@RequestBody RoomCreationRequest request) {
        return ApiResponse.<RoomResponse>builder().data(roomService.createRoom(request)).build();
    }

    @GetMapping
    PaginationResponse<List<Room>> getRooms(@RequestParam(defaultValue = "1", name = "page") int page,
                                            @RequestParam(defaultValue = "10", name = "take") int take) {
        RoomPaginationDTO dto = new RoomPaginationDTO(page, take);
        Page<Room> roomPage = roomService.getRooms(dto.page, dto.take);

        return PaginationResponse.<List<Room>>builder()
                .data(roomPage.getContent())
                .currentPage(roomPage.getNumber() + 1)
                .totalPage(roomPage.getTotalPages())
                .recordPerPage(roomPage.getNumberOfElements())
                .totalRecord((int) roomPage.getTotalElements())
                .build();
    }

    @GetMapping("/{roomId}")
    ApiResponse<Optional<RoomResponse>> getRoomById(@PathVariable("roomId") int roomId) {
        return ApiResponse.<Optional<RoomResponse>>builder().data(roomService.getRoomById(roomId)).build();
    }

    @PutMapping("/{roomId}")
    ApiResponse<RoomResponse> updateRoom(@PathVariable("roomId") int roomId,
                                         @RequestBody RoomCreationRequest request) {
        return ApiResponse.<RoomResponse>builder().data(roomService.updateRoom(roomId, request)).build();
    }

    @DeleteMapping("/{roomId}")
    ApiResponse<RoomResponse> deleteRoom(@PathVariable("roomId") int roomId) {
        return ApiResponse.<RoomResponse>builder().message(roomService.deleteRoom(roomId)).build();
    }
}
