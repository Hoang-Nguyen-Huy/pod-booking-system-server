package com.swp.PodBookingSystem.mapper;

import com.swp.PodBookingSystem.dto.request.Amenity.AmenityCreationRequest;
import com.swp.PodBookingSystem.dto.request.Assignment.AssignmentCreationRequest;
import com.swp.PodBookingSystem.dto.request.Assignment.AssignmentRequest;
import com.swp.PodBookingSystem.dto.respone.Assignment.AssignmentResponse;
import com.swp.PodBookingSystem.entity.Amenity;
import com.swp.PodBookingSystem.entity.Assignment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")

public interface AssignmentMapper {
    Assignment toAssignment(AssignmentCreationRequest request);

    AssignmentResponse toAssignmentResponse(Assignment assignment);

    @Mapping(target = "id", ignore = true)
    Assignment toUpdateAssignment(AssignmentRequest request, @MappingTarget Assignment assignment);
}
