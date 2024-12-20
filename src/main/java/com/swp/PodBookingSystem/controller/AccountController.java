package com.swp.PodBookingSystem.controller;

import com.swp.PodBookingSystem.dto.request.Account.*;
import com.swp.PodBookingSystem.dto.request.CalendarRequest;
import com.swp.PodBookingSystem.dto.respone.Account.AccountManagementResponse;
import com.swp.PodBookingSystem.dto.respone.Account.AccountOrderResponse;
import com.swp.PodBookingSystem.dto.respone.ApiResponse;
import com.swp.PodBookingSystem.dto.respone.AccountResponse;
import com.swp.PodBookingSystem.dto.respone.Order.OrderManagementResponse;
import com.swp.PodBookingSystem.dto.respone.OrderDetail.OrderDetailFullInfoResponse;
import com.swp.PodBookingSystem.dto.respone.PaginationResponse;
import com.swp.PodBookingSystem.entity.Account;
import com.swp.PodBookingSystem.enums.AccountRole;
import com.swp.PodBookingSystem.exception.AppException;
import com.swp.PodBookingSystem.exception.ErrorCode;
import com.swp.PodBookingSystem.mapper.AccountMapper;
import com.swp.PodBookingSystem.service.*;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
    OrderService orderService;
    OrderDetailService orderDetailService;


    @PostMapping
    ApiResponse<AccountResponse> createAccount(@RequestBody @Valid AccountCreationRequest request) {
        AccountResponse response = accountService.createAccount(request);
        return ApiResponse.<AccountResponse>builder()
                .data(response)
                .message(response != null ? "Thêm tài khoản mới thành công" : "Email đã tồn tại")
                .build();
    }

    @GetMapping
    PaginationResponse<List<AccountManagementResponse>> getAccounts(@RequestParam(required = false) String searchParams,
                                                                    @RequestParam(defaultValue = "1", name = "page") int page,
                                                                    @RequestParam(defaultValue = "10", name = "take") int take) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        authentication.getAuthorities().forEach(grantedAuthority -> log.info(grantedAuthority.getAuthority()));

        AccountPaginationDTO dto = new AccountPaginationDTO(page, take);
        Page<AccountManagementResponse> accountPage = accountService.getAccounts(searchParams, dto.page, dto.take);

        return PaginationResponse.<List<AccountManagementResponse>>builder()
                .data(accountPage.getContent())
                .currentPage(accountPage.getNumber() + 1)
                .totalPage(accountPage.getTotalPages())
                .recordPerPage(accountPage.getNumberOfElements())
                .totalRecord((int) accountPage.getTotalElements())
                .build();
    }

    @PatchMapping("/phoneNumber")
    ApiResponse<Void> updateAccountPhoneNumber(@RequestBody AccountUpdatePhoneRequest request) {
        accountService.updatePhoneNumber(request.getId(), request.getPhoneNumber());
        return ApiResponse.<Void>builder()
                .message("Cập nhật số điện thoại thành công")
                .build();
    }

    @PatchMapping("/balance")
    public ApiResponse<Void> updateAccountPhoneNumber(@RequestBody UpdateBalanceDto updateBalanceDto) {
        accountService.updateBalance(updateBalanceDto.getAccountId(), updateBalanceDto.getUsedBalance());
        return ApiResponse.<Void>builder()
                .message("Cập nhật tiền trong ví thành công")
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
    ApiResponse sendEmail(@RequestBody SendMailRequest request) throws MessagingException, IOException {
        sendEmailService.sendCalenderInvite(
                CalendarRequest.builder()
                        .subject("Đăt lịch ở POD Booking")
                        .description("Hãy đặt lịch ở calendar để không bị bỏ lỡ")
                        .summary("Đăt lịch ở POD Booking")
                        .to(request.getEmail())
                        .eventDateTime(LocalDateTime.parse(request.getStartTime())).build());
        return ApiResponse.builder()
                .message("Gửi lời mời đặt lịch thành công")
                .code(200)
                .build();
    }

    @PostMapping("/send-email-order")
    ApiResponse sendEmailOrder(@RequestBody SendMailOrderRequest request) throws MessagingException, IOException {
        OrderManagementResponse order = orderService.getInfoOrder(request.getOrderId());
        sendEmailService.sendMailTemplate(request.getEmail(), order, "Hóa đơn tại FlexiPod");
        return ApiResponse.builder()
                .message("Gửi lời mời đặt lịch thành công")
                .code(200)
                .build();
    }

    @PostMapping("/send-email-order-amenity")
    ApiResponse sendEmailOrderAmenity(@RequestBody SendOrderAmenityRequest request) throws MessagingException, IOException {
        OrderDetailFullInfoResponse orderDetail = orderDetailService.getOrderDetailByOrderDetailId(request.getOrderDetailId());
        sendEmailService.sendMailAmenityOrder(request.getEmail(), orderDetail, "Hóa đơn tại FlexiPod");
        return ApiResponse.builder()
                .message("Gửi lời mời đặt lịch thành công")
                .code(200)
                .build();
    }


    @GetMapping("/staff")
    public ApiResponse<?> getAllStaffAccounts(@RequestHeader("Authorization") String token,
                                              @RequestParam(value = "weekDate", required = false) String weekDate,
                                              @RequestParam(value = "slot", required = false) String slot) {
        Account account = accountService.getAccountById(accountService.extractAccountIdFromToken(token));
        Integer buildingNumber = account.getBuildingNumber();

        if (weekDate != null && slot != null) {
            List<AccountOrderResponse> availableStaff;
            if (account.getRole().equals(AccountRole.Admin)) {
                availableStaff = accountService.getStaffWithoutAssignment(weekDate, slot, "Admin", null);
            } else if (account.getRole().equals(AccountRole.Manager)) {
                availableStaff = accountService.getStaffWithoutAssignment(weekDate, slot, "Manager", buildingNumber);
            } else {
                throw new AppException(ErrorCode.UNAUTHORIZED);
            }

            return ApiResponse.<List<AccountOrderResponse>>builder()
                    .data(availableStaff)
                    .message("Available staff retrieved successfully")
                    .build();
        } else {
            if (account.getRole().equals(AccountRole.Admin)) {
                return ApiResponse.<List<AccountOrderResponse>>builder()
                        .message("Lấy danh sách nhân viên thành công")
                        .data(accountService.getAllStaffAccounts())
                        .build();
            } else if (account.getRole().equals(AccountRole.Manager)) {
                return ApiResponse.<List<AccountOrderResponse>>builder()
                        .message("Lấy danh sách nhân viên thành công")
                        .data(accountService.getAllStaffAccountsByManager(buildingNumber))
                        .build();
            } else {
                List<AccountOrderResponse> list = new ArrayList<>();
                list.add(new AccountOrderResponse(account.getId(), account.getName(), account.getEmail(), account.getAvatar(), account.getRole(), account.getBuildingNumber()));
                return ApiResponse.<List<AccountOrderResponse>>builder()
                        .message("Không có quyền truy cập")
                        .data(list)
                        .build();
            }
        }
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
    ApiResponse<Integer> countCustomer(@RequestParam(required = false) String startTime,
                                       @RequestParam(required = false) String endTime) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy'T'HH:mm");
        LocalDateTime start = startTime != null ? LocalDateTime.parse(startTime, formatter) : null;
        LocalDateTime end = endTime != null ? LocalDateTime.parse(endTime, formatter) : null;

        return ApiResponse.<Integer>builder()
                .message("Số khách hàng từ " + startTime + " đến " + endTime)
                .data(accountService.countCustomer(start, end))
                .build();
    }
}
