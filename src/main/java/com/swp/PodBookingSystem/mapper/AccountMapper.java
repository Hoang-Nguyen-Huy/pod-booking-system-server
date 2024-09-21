package com.swp.PodBookingSystem.mapper;

import com.swp.PodBookingSystem.dto.request.AccountCreationRequest;
import com.swp.PodBookingSystem.dto.respone.AccountResponse;
import com.swp.PodBookingSystem.entity.Account;
import com.swp.PodBookingSystem.enums.AccountRole;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    @Mapping(source = "role", target = "role", qualifiedByName = "stringToAccountRole")
    Account toAccount(AccountCreationRequest request);

    AccountResponse toAccountResponse(Account account);

    @Named("stringToAccountRole")
    default AccountRole stringToAccountRole(String role) {
        return AccountRole.valueOf(role); // Chuyển từ chuỗi thành enum
    }
}
