package com.swp.PodBookingSystem.dto.request.Amenity;

import com.swp.PodBookingSystem.dto.request.PaginationDTO;

public class AmenityPaginationDTO extends PaginationDTO {
    public AmenityPaginationDTO(int page, int take){
        super(page,take);
    }
}
