package com.swp.PodBookingSystem.controller;

import com.swp.PodBookingSystem.dto.respone.ApiResponse;
import com.swp.PodBookingSystem.entity.Room;
import com.swp.PodBookingSystem.entity.RoomImage;
import com.swp.PodBookingSystem.repository.RoomRepository;
import com.swp.PodBookingSystem.service.RoomImageService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/room-images")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoomImageController {
    RoomImageService roomImageService;
    RoomRepository roomRepository;
    @GetMapping("/{roomId}")
    ApiResponse<List<RoomImage>> getRoomImages(@PathVariable  int roomId) {
        Optional<Room> room = roomRepository.findById(roomId);
        if(room.isEmpty()) {
            throw new RuntimeException("Không tìm thấy phòng");
        }
        return ApiResponse.<List<RoomImage>>builder().data(roomImageService.getRoomImages(roomId))
                .message("Tất cả hình ảnh của phòng " + room.get().getName()+" được tìm thấy")
                .build();
    }
    @PostMapping("/{roomId}")
    ApiResponse<List<String>> addRoomImages(@PathVariable int roomId, @RequestBody List<String> images) {
        Optional<Room> room = roomRepository.findById(roomId);
        if(room.isEmpty()) {
            throw new RuntimeException("Không tìm thấy phòng");
        }
        roomImageService.addRoomImages(roomId, images);
        return ApiResponse.<List<String>>builder().data(images)
                .message("Thêm hình ảnh cho phòng " + room.get().getName() + " thành công")
                .build();
    }

    @DeleteMapping("/{imageId}")
    ApiResponse<String> deleteImage(@PathVariable int imageId) {
        roomImageService.deleteImage(imageId);
        return ApiResponse.<String>builder().data("Xóa hình ảnh thành công")
                .message("Xóa hình ảnh thành công")
                .build();
    }


}
