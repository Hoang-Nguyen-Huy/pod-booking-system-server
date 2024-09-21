package com.swp.PodBookingSystem.service;

import com.swp.PodBookingSystem.dto.request.Room.RoomCreationRequest;
import com.swp.PodBookingSystem.dto.respone.Room.RoomResponse;
import com.swp.PodBookingSystem.entity.Room;
import com.swp.PodBookingSystem.mapper.RoomMapper;
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

    // Just for test
    public List<Room> getRooms () {
        return roomRepository.findAll();
    }

    public Optional<RoomResponse> getRoomById(int roomId) {
        return roomMapper.toRoomResponse(roomRepository.findById(roomId));
    }
}
