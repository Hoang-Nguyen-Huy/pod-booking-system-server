package com.swp.PodBookingSystem.controller;

import com.swp.PodBookingSystem.dto.respone.AmenityResponse;
import com.swp.PodBookingSystem.dto.respone.ApiResponse;
import com.swp.PodBookingSystem.service.AmenityService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/amenity")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AmenityController {

    @Autowired
    private AmenityService amenityService;

    @GetMapping
    public ApiResponse<List<AmenityResponse>> getAllAmentity(){
        List<AmenityResponse> amenities = amenityService.getAllAmenities();
        return ApiResponse.<List<AmenityResponse>>builder()
                .data(amenities)
                .build();
    }

}
