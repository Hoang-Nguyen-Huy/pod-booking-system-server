package com.swp.PodBookingSystem.service;

import com.swp.PodBookingSystem.dto.request.Amenity.AmenityCreationRequest;
import com.swp.PodBookingSystem.dto.respone.AmenityResponse;
import com.swp.PodBookingSystem.entity.Amenity;
import com.swp.PodBookingSystem.enums.AmenityType;
import com.swp.PodBookingSystem.mapper.AmenityMapper;
import com.swp.PodBookingSystem.repository.AmenityRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    /*
    [GET]: /amenity/type?amenityType
     */
    public List<AmenityResponse> getAmenitiesByType(AmenityType amenityType) {
        List<Amenity> amenities = amenityRepository.findAllByType(amenityType);
        return amenities.stream()
                .map(amenityMapper::toAmenityResponse)
                .collect(Collectors.toList());
    }

    public AmenityResponse createAmenity(AmenityCreationRequest request){
        Amenity newAmenity = amenityMapper.toAmenity(request);
        return amenityMapper.toAmenityResponse(amenityRepository.save(newAmenity));
    }

    public AmenityResponse updateAmenity(int amenityId, AmenityCreationRequest request){
        Amenity existingAmenity = amenityRepository.findById(amenityId)
                .orElseThrow(() -> new EntityNotFoundException("Amenity not found"));
        Amenity updateAmenity = amenityMapper.toUpdateAmenity(request, existingAmenity);
        return amenityMapper.toAmenityResponse(amenityRepository.save(updateAmenity));
    }

    public String deleteAmenity(int amenityId){
        Amenity existingAmenity = amenityRepository.findById(amenityId)
                .orElseThrow(() -> new EntityNotFoundException("Amenity not found"));
        existingAmenity.setIsDeleted(1);
        amenityRepository.save(existingAmenity);
        return "Delete amenity " + amenityId + " successfully";
    }

    public Page<Amenity> getAmenities(int page, int take){
        Pageable pageable = PageRequest.of(page - 1, take);
        return amenityRepository.findAll(pageable);
    }

}
