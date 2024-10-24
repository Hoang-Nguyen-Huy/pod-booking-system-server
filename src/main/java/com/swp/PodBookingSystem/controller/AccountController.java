package com.swp.PodBookingSystem.controller;

import com.swp.PodBookingSystem.dto.request.Account.*;
import com.swp.PodBookingSystem.dto.request.CalendarRequest;
import com.swp.PodBookingSystem.dto.respone.Account.AccountOrderResponse;
import com.swp.PodBookingSystem.dto.respone.ApiResponse;
import com.swp.PodBookingSystem.dto.respone.AccountResponse;
import com.swp.PodBookingSystem.dto.respone.PaginationResponse;
import com.swp.PodBookingSystem.entity.Account;
import com.swp.PodBookingSystem.enums.AccountRole;
import com.swp.PodBookingSystem.exception.AppException;
import com.swp.PodBookingSystem.exception.ErrorCode;
import com.swp.PodBookingSystem.mapper.AccountMapper;
import com.swp.PodBookingSystem.service.AccountService;
import com.swp.PodBookingSystem.service.SendEmailService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AccountController {
    AccountService accountService;
    AccountMapper accountMapper;
    SendEmailService sendEmailService;
    JwtDecoder jwtDecoder;

    @PostMapping
    ApiResponse<AccountResponse> createAccount(@RequestBody @Valid AccountCreationRequest request) {
        return ApiResponse.<AccountResponse>builder()
                .data(accountService.createAccount(request))
                .build();
    }

    @GetMapping
    PaginationResponse<List<Account>> getAccounts(@RequestParam(defaultValue = "1", name = "page") int page,
                                                  @RequestParam(defaultValue = "10", name = "take") int take) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        authentication.getAuthorities().forEach(grantedAuthority -> log.info(grantedAuthority.getAuthority()));

        AccountPaginationDTO dto = new AccountPaginationDTO(page, take);
        Page<Account> accountPage = accountService.getAccounts(dto.page, dto.take);

        return PaginationResponse.<List<Account>>builder()
                .data(accountPage.getContent())
                .currentPage(accountPage.getNumber() + 1)
                .totalPage(accountPage.getTotalPages())
                .recordPerPage(accountPage.getNumberOfElements())
                .totalRecord((int) accountPage.getTotalElements())
                .build();
    }

    @PatchMapping("/{id}")
    ApiResponse<AccountResponse> updateAccountByAdmin(@PathVariable("id") String id,
                                                      @RequestBody AccountUpdateAdminRequest request) {
        return ApiResponse.<AccountResponse>builder()
                .data(accountService.updateAccountByAdmin(id, request))
                .message("Cập nhật tài khoản thành công")
                .build();
    }

    @GetMapping("/{id}")
    ApiResponse<AccountResponseClient> getAccountById(@PathVariable("id") String id) {
        return ApiResponse.<AccountResponseClient>builder()
                .data(accountMapper.toAccountResponseClient(accountService.getAccountById(id)))
                .message("Thành cong ")
                .build();
    }

    @GetMapping("/me")
    ApiResponse<AccountResponseClient> getMe(@RequestHeader("Authorization") String token, HttpServletRequest request) {
        // Kiểm tra token bắt đầu bằng "Bearer "
        if (token == null || !token.startsWith("Bearer ")) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }

        token = token.substring(7);

        Jwt jwt = jwtDecoder.decode(token);
        String accountId = jwt.getClaimAsString("accountId");

        var result = accountService.getAccountById(accountId);
        return ApiResponse.<AccountResponseClient>builder()
                .data(accountMapper.toAccountResponseClient(result))
                .message("Lấy thông tin cá nhân thành công")
                .code(200)
                .build();
    }

    @PostMapping("/send-email")
    public String sendEmail(@RequestBody SendMailRequest request) throws MessagingException, IOException {
        sendEmailService.sendCalenderInvite(
                CalendarRequest.builder()
                        .subject("Đăt lịch ở POD Booking")
                        .description("Hãy đặt lịch ở calendar để không bị bỏ lỡ")
                        .summary("Đăt lịch ở POD Booking")
                        .to(request.getEmail())
                        .eventDateTime(LocalDateTime.parse("2024-10-27T17:00:00")).build());
        return "Send email successfully";
    }

    @GetMapping("/staff")
    public ResponseEntity<List<AccountOrderResponse>> getAllStaffAccounts() {
        return ResponseEntity.status(HttpStatus.OK).body(accountService.getAllStaffAccounts());
    }

    @GetMapping("/{keyword}/{role}")
    public ResponseEntity<List<AccountOrderResponse>> searchAccounts(
            @PathVariable String keyword,
            @PathVariable AccountRole role) {
        return ResponseEntity.status(HttpStatus.OK).body(accountService.searchAccounts(keyword, role));
    }

    @GetMapping("/number-accounts-current-day")
    ApiResponse<Integer> countCurrentCustomer() {
        return ApiResponse.<Integer>builder()
                .message("Số khách hàng trong ngày")
                .data(accountService.countCurrentCustomer())
                .build();
    }

    @GetMapping("/number-accounts")
    ApiResponse<Integer> countCustomer(@RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy hh:mm a") LocalDateTime startTime,
                                       @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy hh:mm a") LocalDateTime endTime) {
        return ApiResponse.<Integer>builder()
                .message("Số khách hàng từ " + startTime + " đến " + endTime)
                .data(accountService.countCustomer(startTime, endTime))
                .build();
    }
}
