package com.swp.PodBookingSystem.controller;

import com.swp.PodBookingSystem.dto.request.Room.RoomAvailabilityDTO;
import com.swp.PodBookingSystem.dto.request.Slot.SlotDTO;
import com.swp.PodBookingSystem.dto.respone.ApiResponse;
import com.swp.PodBookingSystem.dto.request.Room.RoomCreationRequest;
import com.swp.PodBookingSystem.dto.request.Room.RoomPaginationDTO;
import com.swp.PodBookingSystem.dto.respone.PaginationResponse;
import com.swp.PodBookingSystem.dto.respone.Room.BookedRoomDto;
import com.swp.PodBookingSystem.dto.respone.Room.RoomResponse;
import com.swp.PodBookingSystem.entity.Room;
import com.swp.PodBookingSystem.repository.RoomRepository;
import com.swp.PodBookingSystem.service.AccountService;
import com.swp.PodBookingSystem.service.RoomService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoomController {
    RoomService roomService;
    private final RoomRepository roomRepository;
    private final AccountService accountService;

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

    @GetMapping("/type/{roomId}")
    List<Room> getRoomsSameType(@PathVariable("roomId") int roomId) {
        Optional<Room> room = roomRepository.findById(roomId);
        if (room.isEmpty()) {
            return new ArrayList<>();
        }
        int roomTypeId = room.get().getRoomType().getId();
        return roomService.getRoomsByType(roomTypeId);
    }

    @GetMapping("/filtered-room")
    PaginationResponse<List<Room>> getFilteredRoom(@RequestParam(required = false) String address,
                                                   @RequestParam(required = false) Integer capacity,
                                                   @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
                                                   @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
                                                   @RequestParam(defaultValue = "1") int page,
                                                   @RequestParam(defaultValue = "10") int take) {
        RoomPaginationDTO dto = new RoomPaginationDTO(page, take);
        Page<Room> roomPage = roomService.getFilteredRoomsOnLandingPage(address, capacity, startTime, endTime, dto.page, dto.take);

        return PaginationResponse.<List<Room>>builder()
                .data(roomPage.getContent())
                .currentPage(roomPage.getNumber() + 1)
                .totalPage(roomPage.getTotalPages())
                .recordPerPage(roomPage.getNumberOfElements())
                .totalRecord((int) roomPage.getTotalElements())
                .build();
    }

    @GetMapping("/available-rooms")
    ApiResponse<List<Room>> getAvailableRoomsByRoomTypeId(@RequestParam  Integer typeId,
                                                          @RequestParam(required = false)  List<String> slots) {
        List<SlotDTO> slotList = new ArrayList<>();
        if(slots!=null) {
            for(String slot: slots) {
                String[] parts = slot.split("_");
                LocalDateTime startTime = LocalDateTime.parse(parts[0]);
                LocalDateTime endTime = LocalDateTime.parse(parts[1]);
                slotList.add(new SlotDTO(startTime,endTime));
            }
        }
        return ApiResponse.<List<Room>>builder()
                .data(roomService.getRoomByTypeAndSlot(typeId,slotList))
                .message("Get rooms by typeId and slots successfully")
                .build();
    }

    @GetMapping("/unavailable")
    ApiResponse<List<RoomAvailabilityDTO>> getUnavailableRooms(@RequestParam List<Integer> roomIds,
                                                                @RequestParam LocalDateTime startTime,
                                                                @RequestParam LocalDateTime endTime
                                                               ) {
        return ApiResponse.<List<RoomAvailabilityDTO>>builder()
                .data(roomService.getUnavailableRooms(roomIds,startTime,endTime))
                .message("Get unavailable rooms successfully")
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

    @GetMapping("/booked-rooms")
    ApiResponse<List<BookedRoomDto>> getBookedRooms(@RequestHeader("Authorization") String token) {
        String accountIdFromToken = accountService.extractAccountIdFromToken(token);
        return ApiResponse.<List<BookedRoomDto>>builder()
                .message("Các phòng đã đặt")
                .data(roomService.getBookedRooms(accountIdFromToken))
                .build();
    }

    @GetMapping("/booked-rooms/account")
    ApiResponse<List<BookedRoomDto>> getBookedRoomsByAccountId(@RequestParam("accountId") String accountId) {
        return ApiResponse.<List<BookedRoomDto>>builder()
                .message("Các phòng đã đặt")
                .data(roomService.getBookedRooms(accountId))
                .build();
    }

    @GetMapping("/number-served-rooms-currently")
    ApiResponse<Integer> countCurrentlyServedRooms() {
        return ApiResponse.<Integer>builder()
                .message("Số phòng đang được phục vụ")
                .data(roomService.countCurrentlyServedRooms())
                .build();
    }
    @GetMapping("/available-by-type-and-date")
    ApiResponse<List<Room>> getRoomsByTypeAndDate(@RequestParam  Integer typeId, @RequestParam String date) {
        LocalDate selectedDate = LocalDate.parse(date);
        return ApiResponse.<List<Room>>builder()
                .data(roomService.getRoomsByTypeAndDate(typeId, selectedDate))
                .message("Get rooms by typeId and date successfully")
                .build();
    }

    @GetMapping("/slots-by-rooms-and-date")
    ApiResponse<List<SlotDTO>> getSlotsByRoomsAndDate(@RequestParam(required = false) List<Integer> roomIds, @RequestParam(required = false) String date) {
        return ApiResponse.<List<SlotDTO>>builder()
                .data(roomService.getSlotsByRoomsAndDate(roomIds, date))
                .message("Get slots by rooms and date successfully")
                .build();
    }


}
