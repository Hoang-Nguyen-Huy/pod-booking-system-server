package com.swp.PodBookingSystem.service;

import com.swp.PodBookingSystem.dto.request.Assignment.AssignmentCreationRequest;
import com.swp.PodBookingSystem.dto.respone.Assignment.AssignmentResponse;
import com.swp.PodBookingSystem.entity.Assignment;
import com.swp.PodBookingSystem.mapper.AssignmentMapper;
import com.swp.PodBookingSystem.repository.AssignmentRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AssignmentService {

    private final AssignmentMapper assignmentMapper;
    private final AssignmentRepository assignmentRepository;

    public AssignmentResponse createAssignment(AssignmentCreationRequest request){
        Assignment newAssignment = assignmentMapper.toAssignment(request);
        return assignmentMapper.toAssignmentResponse(assignmentRepository.save(newAssignment));
    }
}
