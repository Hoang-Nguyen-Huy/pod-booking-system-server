package com.swp.PodBookingSystem.controller;

import com.swp.PodBookingSystem.dto.request.ApiResponse;
import com.swp.PodBookingSystem.dto.request.Room.RoomCreationRequest;
import com.swp.PodBookingSystem.dto.respone.Room.RoomResponse;
import com.swp.PodBookingSystem.entity.Room;
import com.swp.PodBookingSystem.service.RoomService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
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
    ApiResponse<List<Room>> getRooms() {
        return ApiResponse.<List<Room>>builder().data(roomService.getRooms()).build();
    }

    @GetMapping("/{roomId}")
    ApiResponse<Optional<RoomResponse>> getRoomById(@PathVariable("roomId") int roomId) {
        return ApiResponse.<Optional<RoomResponse>>builder().data(roomService.getRoomById(roomId)).build();
    }
}
