package com.swp.PodBookingSystem.mapper;

import com.swp.PodBookingSystem.dto.request.Amenity.AmenityCreationRequest;
import com.swp.PodBookingSystem.dto.respone.AmenityResponse;
import com.swp.PodBookingSystem.dto.respone.OrderDetailAmenity.AmenityResponseDTO;
import com.swp.PodBookingSystem.entity.Amenity;
import com.swp.PodBookingSystem.enums.AmenityType;
import com.swp.PodBookingSystem.repository.AmenityRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface AmenityMapper {
    AmenityResponse toAmenityResponse(Amenity amenity);

    // use in orderDetailAmenityResponse
    AmenityResponseDTO toAmenityResponseDTO(Amenity amenity);

    @Named("stringToAmenityType")
    default AmenityType stringToAmentityType(String type){
        return AmenityType.valueOf(type);
    }

    @Mapping(source = "type", target = "type", qualifiedByName = "stringToAmenityType")
    Amenity toAmenity(AmenityCreationRequest request);

    @Mapping(target = "id", ignore = true)
    Amenity toUpdateAmenity(AmenityCreationRequest request, @MappingTarget Amenity amenity);

}
