package com.swp.PodBookingSystem.service;

import com.swp.PodBookingSystem.dto.request.Room.RoomCreationRequest;
import com.swp.PodBookingSystem.dto.respone.Room.RoomResponse;
import com.swp.PodBookingSystem.entity.Room;
import com.swp.PodBookingSystem.mapper.RoomMapper;
import com.swp.PodBookingSystem.repository.RoomRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoomService {
    RoomRepository roomRepository;
    RoomMapper roomMapper;

    /*
    [POST]: /rooms
     */
    public RoomResponse createRoom(RoomCreationRequest request) {
        Room newRoom = roomMapper.toRoom(request);
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
    [PUT]: /rooms/roomId
     */
    public RoomResponse updateRoom(int roomId, RoomCreationRequest request) {
        Optional<Room> existingRoom = roomRepository.findById((roomId));
        Room updatedRoom = roomMapper.toUpdatedRoom(request, existingRoom.orElse(null));
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
