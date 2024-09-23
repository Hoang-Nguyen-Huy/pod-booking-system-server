package com.swp.PodBookingSystem.dto.request;

public class PaginationDTO {
    public int page;
    public int take;

    public PaginationDTO() {
    }

    public PaginationDTO(int page, int take) {
        this.page = (page > 0) ? page : 1;
        this.take = (take > 0) ? take: 10;
    }
}
