package com.swp.PodBookingSystem.dto.request.Room;

import com.swp.PodBookingSystem.dto.request.Slot.SlotDTO;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomAvailabilityDTO {
     Integer roomId;
     String name;
     List<SlotDTO> slots;
}
