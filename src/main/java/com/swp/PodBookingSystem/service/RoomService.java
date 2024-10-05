package com.swp.PodBookingSystem.service;

import com.swp.PodBookingSystem.dto.request.Room.RoomAvailabilityDTO;
import com.swp.PodBookingSystem.dto.request.Room.RoomCreationRequest;
import com.swp.PodBookingSystem.dto.request.Slot.SlotCreationRequest;
import com.swp.PodBookingSystem.dto.respone.Calendar.DateResponse;
import com.swp.PodBookingSystem.dto.respone.Calendar.RoomDTO;
import com.swp.PodBookingSystem.dto.respone.Calendar.SlotDTO;
import com.swp.PodBookingSystem.dto.respone.Room.RoomResponse;
import com.swp.PodBookingSystem.entity.OrderDetail;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        Optional<RoomType> roomType = Optional.empty();
        if (request.getRoomTypeId() != null) {
            roomType = roomTypeRepository.findById(request.getRoomTypeId());
        }
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

    public boolean isRoomAvailable(Integer roomId, LocalDateTime startTime, LocalDateTime endTime) {
        return roomRepository.isRoomAvailable(roomId, startTime, endTime);
    }

    public List<Room> getRoomByTypeAndSlot(Integer typeId,List<SlotCreationRequest> slots ) {
        List<Room> roomList = roomRepository.findRoomsByTypeId(typeId);
        List<Room> availableRooms = new ArrayList<>();
        for (Room room : roomList) {
            boolean isAvailableForAllSlots = true;

            for (SlotCreationRequest slot : slots) {

                if (!isRoomAvailable(room.getId(), slot.getStartTime(), slot.getEndTime())) {
                    isAvailableForAllSlots = false;
                    break;
                }
            }
            if (isAvailableForAllSlots) {
                availableRooms.add(room);
            }
        }

        return availableRooms;
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

    public List<DateResponse> getCalendar(List<Integer> roomIds, Integer servicePackageId, LocalDate selectedDate, List<String> slots)
    {
        List<DateResponse> response = new ArrayList<>();
        List<LocalDate> dates = new ArrayList<>();

        if(servicePackageId!= null && servicePackageId == 1) {
            LocalDateTime currentDate = selectedDate.atStartOfDay();
            int count = 1;
            do {
                dates.add(currentDate.toLocalDate());
                currentDate.plusDays(7);
            } while(count++<=3);
        } else if (servicePackageId!= null && servicePackageId == 2) {
            LocalDateTime currentDate = selectedDate.atStartOfDay();
            int count = 1;
            do {
                dates.add(currentDate.toLocalDate());
                currentDate.plusDays(1);
            } while(count++<=29);
        } else {
            LocalDateTime currentDate = selectedDate.atStartOfDay();
            dates.add(currentDate.toLocalDate());
        }
        for(LocalDate date: dates) {
            List<RoomDTO> rooms = roomIds.parallelStream().map(roomId -> {
                RoomDTO room = new RoomDTO();
                Optional<Room> findRoom = roomRepository.findById((roomId));
                Room roomFromDB = findRoom.orElseThrow(() -> new RuntimeException("Room not found"));
                room.setRoomId(roomFromDB.getId());
                room.setRoomName(roomFromDB.getName());
                LocalDateTime currentDate = date.atStartOfDay();

                List<SlotDTO> slotResponse = slots.parallelStream().map(slot->{
                    String[] parts = slot.split("-");
                    LocalDateTime startTime = currentDate.withHour(Integer.parseInt(parts[0].split(":")[0].trim()));
                    LocalDateTime endTime = currentDate.withHour(Integer.parseInt(parts[1].split(":")[0].trim()));
                    return new SlotDTO(startTime, endTime, roomRepository.isRoomAvailable(roomFromDB.getId(),startTime, endTime));
                }).collect(Collectors.toList());
                room.setSlots(slotResponse);
                return room;
            }).collect(Collectors.toList());
            response.add(new DateResponse(date,rooms));
        }
        return response;
    }


    public List<RoomAvailabilityDTO> getUnavailableRooms(LocalDateTime startTime, LocalDateTime endTime) {
        List<OrderDetail> orders = roomRepository.findRoomAvailabilityWithinDateRange(startTime, endTime);
        System.out.println(orders.toString());
        return orders.stream()
                .map(order -> RoomAvailabilityDTO.builder()
                        .roomId(order.getRoom().getId()) // Assuming Room has an 'id' field
                        .name(order.getRoom().getName()) // Assuming Room has a 'name' field
                        .startTime(order.getStartTime())
                        .endTime(order.getEndTime())
                        .build())
                .collect(Collectors.toList());
    }


}
