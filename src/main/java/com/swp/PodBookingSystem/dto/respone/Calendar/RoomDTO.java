package com.swp.PodBookingSystem.dto.respone.Calendar;

import com.swp.PodBookingSystem.dto.request.Slot.SlotCreationRequest;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomDTO {
    int roomId;
    String roomName;
    List<SlotDTO> slots;
}
