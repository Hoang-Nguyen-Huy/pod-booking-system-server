package com.swp.PodBookingSystem.service;

import com.swp.PodBookingSystem.dto.request.Room.RoomCreationRequest;
import com.swp.PodBookingSystem.dto.respone.Room.RoomResponse;
import com.swp.PodBookingSystem.entity.Room;
import com.swp.PodBookingSystem.entity.RoomType;
import com.swp.PodBookingSystem.mapper.RoomMapper;
import com.swp.PodBookingSystem.repository.RoomRepository;
import com.swp.PodBookingSystem.repository.RoomTypeRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoomService {
    RoomRepository roomRepository;
    RoomMapper roomMapper;
    RoomTypeRepository roomTypeRepository;

    /*
    [POST]: /rooms
     */
    public RoomResponse createRoom(RoomCreationRequest request) {
        Optional<RoomType> roomType = roomTypeRepository.findById(request.getRoomTypeId());
        Room newRoom = roomMapper.toRoom(request);
        newRoom.setRoomType(roomType.orElse(null));
        return roomMapper.toRoomResponse(roomRepository.save(newRoom));
    }

    /*
    [GET]: /rooms/page&take
     */
    public Page<Room> getRooms(int page, int take) {
        Pageable pageable = PageRequest.of(page - 1, take);
        return roomRepository.findAll(pageable);
    }

    /*
    [GET]: /rooms/roomId
     */
    public Optional<RoomResponse> getRoomById(int roomId) {
        return roomMapper.toRoomResponse(roomRepository.findById(roomId));
    }

    /*
    [GET]: /rooms/address&capacity&startTime&endTime&page&take
     */
    public Page<Room> getFilteredRoomsOnLandingPage(String address, Integer capacity, LocalDateTime startTime, LocalDateTime endTime, int page, int take) {
        Pageable pageable = PageRequest.of(page - 1, take);
        return roomRepository.findFilteredRoomsOnLandingPage(address, capacity, startTime, endTime, pageable);
    }

    /*
    [PUT]: /rooms/roomId
     */
    public RoomResponse updateRoom(int roomId, RoomCreationRequest request) {
        Optional<Room> existingRoomOpt = roomRepository.findById((roomId));

        Room existingRoom = existingRoomOpt.orElseThrow(() -> new RuntimeException("Room not found"));

        Integer newRoomTypeId = request.getRoomTypeId();
        Optional<RoomType> newRoomType = roomTypeRepository.findById(newRoomTypeId);
        if (existingRoom.getRoomType() == null ||
                !existingRoom.getRoomType().getId().equals(newRoomTypeId)) {
            existingRoom.setRoomType(newRoomType.orElse(null));
        }

        Room updatedRoom = roomMapper.toUpdatedRoom(request, existingRoom);
        return roomMapper.toRoomResponse(roomRepository.save(updatedRoom));
    }

    /*
    [DELETE]: /rooms/roomId
     */
    public String deleteRoom(int roomId) {
        roomRepository.deleteById(roomId);
        return "Delete room " + roomId + " successfully";
    }
}
