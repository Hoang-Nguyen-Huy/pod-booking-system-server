package com.swp.PodBookingSystem.controller;

import com.swp.PodBookingSystem.dto.request.Account.AccountCreationRequest;
import com.swp.PodBookingSystem.dto.request.Account.AccountResponseClient;
import com.swp.PodBookingSystem.dto.request.Account.GetMeRequest;
import com.swp.PodBookingSystem.dto.request.CalendarRequest;
import com.swp.PodBookingSystem.dto.respone.ApiResponse;
import com.swp.PodBookingSystem.dto.respone.AccountResponse;
import com.swp.PodBookingSystem.entity.Account;
import com.swp.PodBookingSystem.mapper.AccountMapper;
import com.swp.PodBookingSystem.service.AccountService;
import com.swp.PodBookingSystem.service.SendEmailService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
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

    @PostMapping
    ApiResponse<AccountResponse> createAccount(@RequestBody @Valid AccountCreationRequest request) {
        return ApiResponse.<AccountResponse>builder()
                .data(accountService.createAccount(request))
                .build();
    }

    @GetMapping
    ApiResponse<List<Account>> getAccounts() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        authentication.getAuthorities().forEach(grantedAuthority -> log.info(grantedAuthority.getAuthority()));
        return ApiResponse.<List<Account>>builder()
                .data(accountService.getAccounts())
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
    ApiResponse<AccountResponseClient> getMe(@AuthenticationPrincipal Jwt jwt) {
        var result = accountService.getAccountById("d6b9469e-978f-4f84-b2bb-5910c6240deb");
        return ApiResponse.<AccountResponseClient>builder()
                .data(accountMapper.toAccountResponseClient(result))
                .message("Lấy thông tin cá nhân thành công")
                .code(200)
                .build();
    }

//    @GetMapping("/send-email")
//    public String sendEmail() throws MessagingException, IOException {
//        sendEmailService.sendCalenderInvite(
//                CalendarRequest.builder()
//                        .subject("test")
//                        .description("test")
//                        .summary("test")
//                        .to("phuongnguyen2772004.work@gmail.com")
//                        .eventDateTime(LocalDateTime.now()).build());
//        return "Send email successfully";
//    }
}
