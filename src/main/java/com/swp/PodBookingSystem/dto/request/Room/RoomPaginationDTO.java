package com.swp.PodBookingSystem.dto.request.Room;

import com.swp.PodBookingSystem.dto.request.PaginationDTO;


public class RoomPaginationDTO extends PaginationDTO {
    public RoomPaginationDTO(int page, int take) {
        super(page, take);
    }
}
