package com.swp.PodBookingSystem.dto.respone.Calendar;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DateResponse {
    LocalDate date;
    List<RoomDTO> rooms;
}
