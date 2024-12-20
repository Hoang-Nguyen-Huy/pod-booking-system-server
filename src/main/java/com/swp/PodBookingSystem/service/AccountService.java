package com.swp.PodBookingSystem.service;

import com.swp.PodBookingSystem.dto.request.Account.AccountCreationRequest;
import com.swp.PodBookingSystem.dto.request.Account.AccountUpdateAdminRequest;
import com.swp.PodBookingSystem.dto.respone.Account.AccountManagementResponse;
import com.swp.PodBookingSystem.dto.respone.Account.AccountOrderResponse;
import com.swp.PodBookingSystem.dto.respone.AccountResponse;
import com.swp.PodBookingSystem.dto.respone.Building.BuildingResponse;
import com.swp.PodBookingSystem.entity.Account;
import com.swp.PodBookingSystem.entity.Building;
import com.swp.PodBookingSystem.enums.AccountRole;
import com.swp.PodBookingSystem.exception.AppException;
import com.swp.PodBookingSystem.exception.ErrorCode;
import com.swp.PodBookingSystem.mapper.AccountMapper;
import com.swp.PodBookingSystem.mapper.BuildingMapper;
import com.swp.PodBookingSystem.repository.AccountRepository;
import com.swp.PodBookingSystem.repository.AssignmentRepository;
import com.swp.PodBookingSystem.repository.BuildingRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AccountService {
    AccountRepository accountRepository;
    AccountMapper accountMapper;
    BuildingMapper buildingMapper;
    JwtDecoder jwtDecoder;
    BuildingRepository buildingRepository;
    private final AssignmentRepository assignmentRepository;


    public AccountResponse createAccount(AccountCreationRequest request) {
        if (accountRepository.existsByEmail(request.getEmail())) {
            return null;
        }
        Account account = accountMapper.toAccount(request);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        return accountMapper.toAccountResponse(accountRepository.save(account));
    }

    /*
    [GET]: /accounts/page&take
     */
    public Page<AccountManagementResponse> getAccounts(String searchParams, int page, int take) {
        Pageable pageable = PageRequest.of(page - 1, take, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Account> accountPage = accountRepository.findFilteredAccount(searchParams, pageable);
        return accountPage.map(this::convertToAccountManagementResponse);
    }


    public Account getAccountById(String id) {
        return accountRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));
    }

    /*
    [PATCH]: /accounts/{accountId}
     */
    @PreAuthorize("hasRole('Admin')")
    public AccountResponse updateAccountByAdmin(String accountId, AccountUpdateAdminRequest request) {
        Optional<Account> existingAccountOpt = accountRepository.findById((accountId));

        Account existedAccount = existingAccountOpt.orElseThrow(() -> new RuntimeException("Account not found"));

        Account updatedAccount = accountMapper.toUpdatedAccountAdmin(request, existedAccount);

        return accountMapper.toAccountResponse(accountRepository.save(updatedAccount));
    }

    public void updatePhoneNumber(String accountId, String phoneNumber) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));
        account.setPhoneNumber(phoneNumber);
        accountRepository.save(account);
    }

    public void updateBalance(String accountId, double usedBalance) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));
        account.setBalance(account.getBalance() - usedBalance);
        accountRepository.save(account);
    }

    public List<AccountOrderResponse> getAllStaffAccounts() {
        List<Account> accounts = accountRepository.findByRole(AccountRole.Staff);
        return accounts.stream()
                .map(this::toAccountResponse)
                .collect(Collectors.toList());
    }

    public List<AccountOrderResponse> getAllStaffAccountsByManager(int buildingNumber) {
        List<Account> accounts = accountRepository.findStaffByManager(AccountRole.Staff, buildingNumber);
        return accounts.stream()
                .map(this::toAccountResponse)
                .collect(Collectors.toList());
    }


    public List<AccountOrderResponse> searchAccounts(String keyword, AccountRole role) {
        List<Account> accounts = accountRepository.searchAccounts(keyword, role);
        return accounts.stream()
                .map(this::toAccountResponse)
                .collect(Collectors.toList());
    }

    public AccountOrderResponse toAccountResponse(Account account) {
        return AccountOrderResponse.builder()
                .id(account.getId())
                .name(account.getName())
                .email(account.getEmail())
                .avatar(account.getAvatar())
                .role(account.getRole())
                .buildingNumber(account.getBuildingNumber())
                .build();
    }

    private AccountManagementResponse convertToAccountManagementResponse(Account account) {
        BuildingResponse buildingResponse = null;
        if (account.getBuildingNumber() != 0) {
            Building building = buildingRepository.findById(account.getBuildingNumber())
                    .orElseThrow(() -> null);
            buildingResponse = buildingMapper.toBuildingResponse(building);
        }

        return AccountManagementResponse.builder()
                .id(account.getId())
                .name(account.getName())
                .email(account.getEmail())
                .password(account.getPassword())
                .avatar(account.getAvatar())
                .point(account.getPoint())
                .role(account.getRole())
                .balance(account.getBalance())
                .building(buildingResponse)
                .createdAt(account.getCreatedAt())
                .status(account.getStatus())
                .build();
    }

    public String extractAccountIdFromToken(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }
        token = token.substring(7);
        Jwt jwt = jwtDecoder.decode(token);
        return jwt.getClaimAsString("accountId");
    }

    /*
    [GET]: /accounts/number-accounts-current-day
     */
    public int countCurrentCustomer() {
        return accountRepository.countCurrentCustomer();
    }

    /*
    [GET]: /accounts/number-accounts
     */
    public int countCustomer(LocalDateTime startTime, LocalDateTime endTime) {
        return accountRepository.countCustomerBetweenDatetime(startTime, endTime);
    }

    public List<AccountOrderResponse> getStaffWithoutAssignment(String weekDate, String slot, String role, Integer buildingNumber) {

        List<String> assignedStaffIds = assignmentRepository.findStaffIdsByWeekDateAndSlot(weekDate, slot);


        if ("Admin".equals(role)) {
            return accountRepository.findStaffNotInAssignedList(assignedStaffIds).stream()
                    .map(accountMapper::toAccountOrderResponse)
                    .collect(Collectors.toList());
        } else if ("Manager".equals(role) && buildingNumber != null) {
            return accountRepository.findStaffNotInAssignedListByBuilding(assignedStaffIds, buildingNumber).stream()
                    .map(accountMapper::toAccountOrderResponse)
                    .collect(Collectors.toList());
        } else {
            throw new IllegalArgumentException("Invalid role or missing building number for manager.");
        }
    }

}