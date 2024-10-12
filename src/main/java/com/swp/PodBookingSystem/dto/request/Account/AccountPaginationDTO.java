package com.swp.PodBookingSystem.dto.request.Account;

import com.swp.PodBookingSystem.dto.request.PaginationDTO;

public class AccountPaginationDTO extends PaginationDTO {
    public AccountPaginationDTO(int page, int take) {
        super(page, take);
    }
}
