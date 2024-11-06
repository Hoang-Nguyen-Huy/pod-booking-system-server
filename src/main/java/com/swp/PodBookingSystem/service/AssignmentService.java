package com.swp.PodBookingSystem.service;

import com.swp.PodBookingSystem.dto.request.Assignment.AssignmentCreationRequest;
import com.swp.PodBookingSystem.dto.request.Assignment.AssignmentRequest;
import com.swp.PodBookingSystem.dto.respone.AccountResponse;
import com.swp.PodBookingSystem.dto.respone.Assignment.AssignmentResponse;
import com.swp.PodBookingSystem.entity.Assignment;
import com.swp.PodBookingSystem.mapper.AccountMapper;
import com.swp.PodBookingSystem.mapper.AccountMapperImpl;
import com.swp.PodBookingSystem.mapper.AssignmentMapper;
import com.swp.PodBookingSystem.repository.AccountRepository;
import com.swp.PodBookingSystem.repository.AssignmentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.swp.PodBookingSystem.entity.Account;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AssignmentService {

    private final AssignmentMapper assignmentMapper;
    private final AssignmentRepository assignmentRepository;
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    public AssignmentResponse createAssignment(AssignmentCreationRequest request){
        Assignment newAssignment = assignmentMapper.toAssignment(request);
        newAssignment.setId(UUID.randomUUID().toString());
        return assignmentMapper.toAssignmentResponse(assignmentRepository.save(newAssignment));
    }

    public List<AssignmentResponse> getAllAssignments(){
        List<Assignment> assignments = assignmentRepository.findAll();
        return assignments.stream()
                .map(assignment -> {
                    AssignmentResponse response = assignmentMapper.toAssignmentResponse(assignment);
                    String nameStaff = accountRepository.findById(assignment.getStaffId())
                            .map(Account::getName)
                            .orElse("Unknown");
                    response.setNameStaff(nameStaff);
                    return response;
                })
                .collect(Collectors.toList());
    }

    public AssignmentResponse updateAssignment(String id, AssignmentRequest request){
        Assignment existingAssignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Assignment not found"));
        Assignment updateAssignment = assignmentMapper.toUpdateAssignment(request, existingAssignment);
        return assignmentMapper.toAssignmentResponse(assignmentRepository.save(updateAssignment));
    }

    public String deleteAssignment(String id){
        Assignment existingAssignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Assignment not found"));
        assignmentRepository.deleteById(id);
        return "Assignment deleted successfully";
    }

    public List<AssignmentResponse> getAssignmentsByStaffId(String staffId) {
        List<Assignment> assignments = assignmentRepository.findByStaffId(staffId);
        return assignments.stream()
                .map(assignment -> {
                    AssignmentResponse response = assignmentMapper.toAssignmentResponse(assignment);
                    String nameStaff = accountRepository.findById(assignment.getStaffId())
                            .map(Account::getName)
                            .orElse("Unknown");
                    response.setNameStaff(nameStaff); // Set the staff name
                    return response;
                })
                .collect(Collectors.toList());
    }




}
