package com.swp.PodBookingSystem.service;

import com.swp.PodBookingSystem.dto.respone.AmenityResponse;
import com.swp.PodBookingSystem.entity.Amenity;
import com.swp.PodBookingSystem.mapper.AmenityMapper;
import com.swp.PodBookingSystem.repository.AmenityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AmenityService {
    @Autowired
    private AmenityRepository amenityRepository;

    @Autowired
    private AmenityMapper amenityMapper;

    public List<AmenityResponse> getAllAmenities(){
        List<Amenity> amenities = amenityRepository.findAll();
        return amenities.stream()
                .map(amenityMapper::toAmenityResponse)
                .collect(Collectors.toList());
    }
}
