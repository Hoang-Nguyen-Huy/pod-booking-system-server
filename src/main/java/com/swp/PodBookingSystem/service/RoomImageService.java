package com.swp.PodBookingSystem.service;

import com.swp.PodBookingSystem.dto.respone.RoomImage.RoomImageCustomResponse;
import com.swp.PodBookingSystem.entity.Room;
import com.swp.PodBookingSystem.entity.RoomImage;
import com.swp.PodBookingSystem.repository.RoomImageRepository;
import com.swp.PodBookingSystem.repository.RoomRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoomImageService {
    RoomImageRepository roomImageRepository;
    RoomRepository roomRepository;
    public List<RoomImage> getRoomImages(int roomId) {
        return roomImageRepository.findAllImagesByRoomId(roomId);
    }

    public void addRoomImages(int roomId, List<String> images) {

        Optional<Room> room = roomRepository.findById(roomId);
        images.forEach(image -> {
            roomImageRepository.save(
                    new RoomImage(null, image, room.orElseThrow(() -> new RuntimeException("Không tìm thấy phòng"))));
        });
    }

    public void deleteImage(int imageId) {
        roomImageRepository.deleteById(imageId);
    }

    public List<RoomImageCustomResponse> getRoomImagesByRoomType(int roomTypeId) {
        return roomImageRepository.findAllImagesByRoomTypeId(roomTypeId);
    }
}
