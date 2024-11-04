package com.swp.PodBookingSystem.mapper;

import com.swp.PodBookingSystem.dto.request.Assignment.AssignmentCreationRequest;
import com.swp.PodBookingSystem.dto.respone.Assignment.AssignmentResponse;
import com.swp.PodBookingSystem.entity.Assignment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")

public interface AssignmentMapper {
    Assignment toAssignment(AssignmentCreationRequest request);

    AssignmentResponse toAssignmentResponse(Assignment assignment);
}
