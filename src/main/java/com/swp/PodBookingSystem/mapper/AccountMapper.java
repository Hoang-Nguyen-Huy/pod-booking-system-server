//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.swp.PodBookingSystem.mapper;

import com.swp.PodBookingSystem.dto.request.Account.AccountCreationRequest;
import com.swp.PodBookingSystem.dto.request.Account.AccountResponseClient;
import com.swp.PodBookingSystem.dto.request.Account.AccountUpdateAdminRequest;
import com.swp.PodBookingSystem.dto.respone.Account.AccountOrderResponse;
import com.swp.PodBookingSystem.dto.respone.AccountResponse;
import com.swp.PodBookingSystem.entity.Account;
import com.swp.PodBookingSystem.enums.AccountRole;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
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

    @Mapping(source = "role", target = "role", qualifiedByName = "stringToAccountRole")
    @Mapping(source = "phoneNumber", target = "phoneNumber")
    AccountResponseClient toAccountResponseClient(Account account);

    @Mapping(target = "id", ignore = true)
    Account toUpdatedAccountAdmin(AccountUpdateAdminRequest request, @MappingTarget Account account);

    AccountOrderResponse toAccountOrderResponse(Account account);
}
