package com.swp.PodBookingSystem.controller;

import com.swp.PodBookingSystem.dto.request.Assignment.AssignmentCreationRequest;
import com.swp.PodBookingSystem.dto.respone.ApiResponse;
import com.swp.PodBookingSystem.dto.respone.Assignment.AssignmentResponse;
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

    @PostMapping
    ApiResponse<AssignmentResponse> createAssignment(@RequestBody @Valid AssignmentCreationRequest request){
        return ApiResponse.<AssignmentResponse>builder()
                .data(assignmentService.createAssignment(request))
                .message("Thêm assignment thành công")
                .build();
    }

    @GetMapping("/all")
    public ApiResponse<List<AssignmentResponse>> getAllAssignment(){
        List<AssignmentResponse> assignments = assignmentService.getAllAssignments();
        return ApiResponse.<List<AssignmentResponse>>builder()
                .data(assignments)
                .build();
    }

}
