package com.swp.PodBookingSystem.service;

import com.swp.PodBookingSystem.dto.request.Assignment.AssignmentCreationRequest;
import com.swp.PodBookingSystem.dto.request.Assignment.AssignmentRequest;
import com.swp.PodBookingSystem.dto.respone.Assignment.AssignmentResponse;
import com.swp.PodBookingSystem.entity.Assignment;
import com.swp.PodBookingSystem.mapper.AccountMapper;
import com.swp.PodBookingSystem.mapper.AssignmentMapper;
import com.swp.PodBookingSystem.repository.AccountRepository;
import com.swp.PodBookingSystem.repository.AssignmentRepository;
import com.swp.PodBookingSystem.repository.OrderDetailRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.swp.PodBookingSystem.entity.Account;

import java.time.DayOfWeek;
import java.time.LocalTime;
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
    private final OrderDetailRepository orderDetailRepository;
    private static final Logger  logger = LoggerFactory.getLogger(AssignmentService.class);


    public AssignmentResponse createAssignment(AssignmentCreationRequest request) {
        Assignment newAssignment = assignmentMapper.toAssignment(request);
        newAssignment.setId(UUID.randomUUID().toString());

        String weekDate = newAssignment.getWeekDate();
        String slot = newAssignment.getSlot();
        Account staff = accountRepository.getById(newAssignment.getStaffId());

        DayOfWeek dayOfWeek = getDayOfWeekFromWeekDate(weekDate);
        int weekDay = dayOfWeek.getValue() -1 ;

        LocalTime[] slotTimes = getSlotTimes(slot);
        String slotStartTime = slotTimes[0].toString();
        String slotEndTime = slotTimes[1].toString();

        orderDetailRepository.assignOrdersToStaff(newAssignment.getStaffId(), weekDay, slotStartTime, slotEndTime, staff.getBuildingNumber());

        return assignmentMapper.toAssignmentResponse(assignmentRepository.save(newAssignment));
    }

    public DayOfWeek getDayOfWeekFromWeekDate(String weekDate) {
        switch (weekDate) {
            case "T1": return DayOfWeek.SUNDAY;
            case "T2": return DayOfWeek.MONDAY;
            case "T3": return DayOfWeek.TUESDAY;
            case "T4": return DayOfWeek.WEDNESDAY;
            case "T5": return DayOfWeek.THURSDAY;
            case "T6": return DayOfWeek.FRIDAY;
            case "T7": return DayOfWeek.SATURDAY;
            case "CN": return DayOfWeek.SUNDAY;
            default: throw new IllegalArgumentException("Unknown weekDate: " + weekDate);
        }
    }

    public LocalTime[] getSlotTimes(String slot) {
        String[] times = slot.split(" - ");
        LocalTime startTime = LocalTime.parse(times[0]);
        LocalTime endTime = LocalTime.parse(times[1]);
        return new LocalTime[]{startTime, endTime};
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
