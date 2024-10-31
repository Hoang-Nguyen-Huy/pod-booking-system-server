package com.swp.PodBookingSystem.service;

import com.swp.PodBookingSystem.dto.request.Room.RoomAvailabilityDTO;
import com.swp.PodBookingSystem.dto.request.Room.RoomCreationRequest;
import com.swp.PodBookingSystem.dto.request.Slot.SlotDTO;
import com.swp.PodBookingSystem.dto.respone.Room.BookedRoomDto;
import com.swp.PodBookingSystem.dto.respone.Room.RoomResponse;
import com.swp.PodBookingSystem.entity.Room;
import com.swp.PodBookingSystem.entity.RoomType;
import com.swp.PodBookingSystem.exception.AppException;
import com.swp.PodBookingSystem.exception.ErrorCode;
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
            if(roomType.isEmpty()) {
                throw new RuntimeException("Room type not found");
            }else{
                RoomType roomTypeUpdate = roomType.get();
                roomTypeUpdate.setQuantity(roomTypeUpdate.getQuantity() + 1);
                roomTypeRepository.save(roomTypeUpdate);
            }
        }
        Room newRoom = roomMapper.toRoom(request);
        newRoom.setRoomType(roomType.orElse(null));
        return roomMapper.toRoomResponse(roomRepository.save(newRoom));
    }

    /*
    [GET]: /rooms/page&take
     */
    public Page<Room> getRooms(String searchParams, int page, int take) {
        Pageable pageable = PageRequest.of(page - 1, take);
        return roomRepository.findFilteredManagementRoom(searchParams, pageable);
    }

    public List<Room> getRoomsByType(int typeId) {
        return roomRepository.findRoomsByTypeId(typeId);
    }

    /*
    [GET]: /rooms/roomId
     */
    public Optional<RoomResponse> getRoomById(int roomId) {
        return roomMapper.toRoomResponse(roomRepository.findById(roomId));
    }

    public Room getRoomByIdV2(int roomId) {
        return roomRepository.findById(roomId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));
    }

    public boolean isRoomAvailable(Integer roomId, LocalDateTime startTime, LocalDateTime endTime) {
        return roomRepository.isRoomAvailable(roomId, startTime, endTime);
    }

    public List<Room> getRoomByTypeAndSlot(Integer typeId,List<SlotDTO> slots ) {
        List<Room> roomList = roomRepository.findRoomsByTypeId(typeId);
        List<Room> availableRooms = new ArrayList<>();
        for (Room room : roomList) {
            boolean isAvailableForAllSlots = true;

            for (SlotDTO slot : slots) {

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



    public List<RoomAvailabilityDTO> getUnavailableRooms(List<Integer> roomIds,LocalDateTime startTime, LocalDateTime endTime) {
        if(roomIds == null || roomIds.isEmpty()) {
            return new ArrayList<>();
        }
        if(startTime == null || endTime == null) {
            throw new RuntimeException("Invalid date");
        }

        List<RoomAvailabilityDTO> roomAvailabilityDTOList = new ArrayList<>();
        for(Integer roomId: roomIds) {
            List<SlotDTO> slotList = roomRepository.getSlotsByRoomAndDate(roomId, startTime, endTime);
            Room room = roomRepository.findById(roomId).orElseThrow(() -> new RuntimeException("Room not found"));
            roomAvailabilityDTOList.add(RoomAvailabilityDTO.builder()
                    .roomId(roomId)
                    .name(room.getName())
                    .slots(slotList)
                    .build());
        }
        return roomAvailabilityDTOList;
    }

    public List<BookedRoomDto> getBookedRooms(String customerId) {
        LocalDateTime currentTime = LocalDateTime.now();
        return roomRepository.findBookedRooms(currentTime, customerId);
    }

    /*
    [GET]: /rooms/number-served-rooms-currently
     */
    public int countCurrentlyServedRooms() {
        return roomRepository.countCurrentlyServedRooms(LocalDateTime.now());
    }

    public List<Room> getRoomsByTypeAndDate(Integer typeId, LocalDate date) {
        List<Room> result = new ArrayList<>();
        List<SlotDTO> listSlotConstants = new ArrayList<>();
        LocalDateTime current = date.atTime(7, 0, 0);
        while (current.isBefore(date.atTime(21, 0, 0))) {
            if(current.isBefore(LocalDateTime.now())) {
                current = current.plusHours(2);
                continue;
            }
            listSlotConstants.add(new SlotDTO(current, current.plusHours(2)));
            current = current.plusHours(2);
        }
        LocalDateTime startTime = date.atStartOfDay();
        LocalDateTime endTime = date.atTime(23, 59, 59);
        List<Room> rooms =roomRepository.findAllByTypeId(typeId);
        for(Room room: rooms) {
            List<SlotDTO> slotList = roomRepository.getSlotsByRoomAndDate(room.getId(), startTime, endTime);
            List<SlotDTO> tmp = new ArrayList<>(listSlotConstants);
            tmp.removeAll(slotList);
            if(!tmp.isEmpty()) {
                result.add(room);
            }
        }

        return result;
    }

    public List<SlotDTO> getSlotsByRoomsAndDate(List<Integer> roomIds, String date) {
        LocalDate selectedDate = date == null? LocalDate.now() : LocalDate.parse(date);
        if(selectedDate.isBefore(LocalDate.now())) {
            throw new RuntimeException("Invalid date");
        }
        if(selectedDate.isEqual(LocalDate.now()) && LocalDateTime.now().getHour() >= 21) {
            throw new RuntimeException("Invalid date");
        }

        LocalDateTime startTime = LocalDateTime.now().toLocalDate().equals(selectedDate) ? LocalDateTime.now() : selectedDate.atStartOfDay();
        LocalDateTime endTime = selectedDate.atTime(23, 59, 59);
        List<SlotDTO> listSlotConstants = new ArrayList<>();
        LocalDateTime current = selectedDate.atTime(7, 0, 0);
        while (current.isBefore(selectedDate.atTime(21, 0, 0))) {
            if(current.isBefore(LocalDateTime.now())) {
                current = current.plusHours(2);
                continue;
            }
            listSlotConstants.add(new SlotDTO(current, current.plusHours(2)));
            current = current.plusHours(2);
        }
        if(roomIds == null || roomIds.isEmpty()) {
            return listSlotConstants;
        }
        for (Integer roomId : roomIds) {
            List<SlotDTO> listSlotDateTime = roomRepository.getSlotsByRoomAndDate(roomId, startTime, endTime);
            listSlotConstants.removeAll(listSlotDateTime);
        }
        return listSlotConstants;
    }
}
