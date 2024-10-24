package com.swp.PodBookingSystem.service;

import com.swp.PodBookingSystem.dto.request.Account.AccountCreationRequest;
import com.swp.PodBookingSystem.dto.request.Account.AccountUpdateAdminRequest;
import com.swp.PodBookingSystem.dto.respone.Account.AccountOrderResponse;
import com.swp.PodBookingSystem.dto.respone.AccountResponse;
import com.swp.PodBookingSystem.entity.Account;
import com.swp.PodBookingSystem.enums.AccountRole;
import com.swp.PodBookingSystem.exception.AppException;
import com.swp.PodBookingSystem.exception.ErrorCode;
import com.swp.PodBookingSystem.mapper.AccountMapper;
import com.swp.PodBookingSystem.repository.AccountRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;

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
    JwtDecoder jwtDecoder;

    public AccountResponse createAccount(AccountCreationRequest request) {
        Account account = accountMapper.toAccount(request);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        return accountMapper.toAccountResponse(accountRepository.save(account));
    }

    /*
    [GET]: /accounts/page&take
     */
    @PreAuthorize("hasRole('Admin')")
    public Page<Account> getAccounts(int page, int take) {
        Pageable pageable = PageRequest.of(page - 1, take);
        return accountRepository.findAll(pageable);
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

    public List<AccountOrderResponse> getAllStaffAccounts() {
            List<Account> accounts = accountRepository.findByRole(AccountRole.Staff);
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
                .rankingName(account.getRankingName())
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
}