package com.swp.PodBookingSystem.controller;

import com.swp.PodBookingSystem.dto.request.Assignment.AssignmentCreationRequest;
import com.swp.PodBookingSystem.dto.request.Assignment.AssignmentRequest;
import com.swp.PodBookingSystem.dto.respone.ApiResponse;
import com.swp.PodBookingSystem.dto.respone.Assignment.AssignmentResponse;
import com.swp.PodBookingSystem.entity.Account;
import com.swp.PodBookingSystem.service.AccountService;
import com.swp.PodBookingSystem.service.AssignmentService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/assignment")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AssignmentController {

    private final AssignmentService assignmentService;
    private final AccountService accountService;

    @PostMapping
    ApiResponse<AssignmentResponse> createAssignment(@RequestBody @Valid AssignmentCreationRequest request){
        return ApiResponse.<AssignmentResponse>builder()
                .data(assignmentService.createAssignment(request))
                .message("Add Assignment successfully")
                .build();
    }

    @GetMapping("/all")
    public ApiResponse<List<AssignmentResponse>> getAllAssignment(@RequestHeader("Authorization") String token){
        Account account = accountService.getAccountById(accountService.extractAccountIdFromToken(token));
        List<AssignmentResponse> assignments = assignmentService.getAllAssignments(account);
            return ApiResponse.<List<AssignmentResponse>>builder()
                    .data(assignments)
                    .build();
    }


    @PutMapping("/{assignmentId}")
    ApiResponse<AssignmentResponse> updateAssignment(@PathVariable("assignmentId") String assignmentId,
                                                     @RequestBody AssignmentRequest request){
        return ApiResponse.<AssignmentResponse>builder()
                .data(assignmentService.updateAssignment(assignmentId,request))
                .message("Update Assignment successfully")
                .build();
    }

    @DeleteMapping("/{assignmentId}")
    ApiResponse<AssignmentResponse> deleteAssignment(@PathVariable("assignmentId") String assignmentId){
        return ApiResponse.<AssignmentResponse>builder()
                .message(assignmentService.deleteAssignment(assignmentId))
                .build();
    }

    @GetMapping("/{staffId}")
    public ApiResponse<List<AssignmentResponse>> getAssignmentByStaffId(@PathVariable("staffId") String staffId) {
        List<AssignmentResponse> assignments = assignmentService.getAssignmentsByStaffId(staffId);
        return ApiResponse.<List<AssignmentResponse>>builder()
                .data(assignments)
                .message("Assignments retrieved successfully")
                .build();
    }


 }

