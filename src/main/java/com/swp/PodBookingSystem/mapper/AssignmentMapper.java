package com.swp.PodBookingSystem.mapper;

import com.swp.PodBookingSystem.dto.request.Assignment.AssignmentCreationRequest;
import com.swp.PodBookingSystem.dto.request.Assignment.AssignmentRequest;
import com.swp.PodBookingSystem.dto.respone.Assignment.AssignmentResponse;
import com.swp.PodBookingSystem.entity.Account;
import com.swp.PodBookingSystem.entity.Assignment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AssignmentMapper {

    @Mapping(target = "staff", source = "staffId")
    Assignment toAssignment(AssignmentCreationRequest request);

    @Mapping(target = "staffId", source = "staff.id")
    AssignmentResponse toAssignmentResponse(Assignment assignment);

    @Mapping(target = "staff", source = "staffId")
    @Mapping(target = "id", ignore = true)
    Assignment toUpdateAssignment(AssignmentRequest request, @MappingTarget Assignment assignment);

    Account map(String staffId);
}
