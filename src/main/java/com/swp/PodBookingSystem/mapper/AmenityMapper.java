package com.swp.PodBookingSystem.mapper;

import com.swp.PodBookingSystem.dto.respone.AmenityResponse;
import com.swp.PodBookingSystem.entity.Amenity;
import com.swp.PodBookingSystem.enums.AmenityType;
import com.swp.PodBookingSystem.repository.AmenityRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface AmenityMapper {
    AmenityResponse toAmenityResponse(Amenity amenity);

    @Named("stringToAmenityType")
    default AmenityType stringToAmentityType(String type){
        return AmenityType.valueOf(type);
    }
}
