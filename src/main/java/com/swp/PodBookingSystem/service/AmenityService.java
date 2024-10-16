package com.swp.PodBookingSystem.service;

import com.swp.PodBookingSystem.dto.request.Amenity.AmenityCreationRequest;
import com.swp.PodBookingSystem.dto.respone.AmenityResponse;
import com.swp.PodBookingSystem.entity.Amenity;
import com.swp.PodBookingSystem.mapper.AmenityMapper;
import com.swp.PodBookingSystem.repository.AmenityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
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

    public AmenityResponse createAmenity(AmenityCreationRequest request){
        Amenity newAmenity = amenityMapper.toAmenity(request);
        return amenityMapper.toAmenityResponse(amenityRepository.save(newAmenity));
    }

    public AmenityResponse updateAmenity(int amenityId, AmenityCreationRequest request){
        Optional<Amenity> existingAmenity = amenityRepository.findById(amenityId);
        Amenity updateAmenity = amenityMapper.toUpdateAmenity(request, existingAmenity.orElse(null));
        return amenityMapper.toAmenityResponse(amenityRepository.save(updateAmenity));
    }

}
