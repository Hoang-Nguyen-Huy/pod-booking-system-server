package com.swp.PodBookingSystem.controller;

import com.swp.PodBookingSystem.dto.request.Amenity.AmenityCreationRequest;
import com.swp.PodBookingSystem.dto.request.Amenity.AmenityPaginationDTO;
import com.swp.PodBookingSystem.dto.respone.AmenityResponse;
import com.swp.PodBookingSystem.dto.respone.ApiResponse;
import com.swp.PodBookingSystem.dto.respone.PaginationResponse;
import com.swp.PodBookingSystem.entity.Account;
import com.swp.PodBookingSystem.entity.Amenity;
import com.swp.PodBookingSystem.enums.AmenityType;
import com.swp.PodBookingSystem.service.AccountService;
import com.swp.PodBookingSystem.service.AmenityService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/amenity")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AmenityController {

    @Autowired
    private AmenityService amenityService;

    @Autowired
    private AccountService accountService;

    @GetMapping("/all")
    public ApiResponse<List<AmenityResponse>> getAllAmentity(){
        List<AmenityResponse> amenities = amenityService.getAllAmenities();
        return ApiResponse.<List<AmenityResponse>>builder()
                .data(amenities)
                .build();
    }


    @GetMapping("/allActive")
    public ApiResponse<List<AmenityResponse>> getAllActiveAmenities() {
        List<AmenityResponse> amenities = amenityService.getAllActiveAmenities();
        return ApiResponse.<List<AmenityResponse>>builder()
                .data(amenities)
                .build();
    }

    @PostMapping
    ApiResponse<AmenityResponse> createAmenity(@RequestBody AmenityCreationRequest request){
        return ApiResponse.<AmenityResponse>builder()
                .data(amenityService.createAmenity(request))
                .message("Create amenity successfully!")
                .build();
    }

    @PutMapping("/{amenityId}")
    ApiResponse<AmenityResponse> updateAmenity(@PathVariable("amenityId") int amenityId,
                                               @RequestBody AmenityCreationRequest request){
        return ApiResponse.<AmenityResponse>builder()
                .data(amenityService.updateAmenity(amenityId,request))
                .message("Update amenity successfully")
                .build();
    }

    @DeleteMapping("/{amenityId}")
    ApiResponse<AmenityResponse> deleteAmenity(@PathVariable("amenityId") int amenityId){
        return ApiResponse.<AmenityResponse>builder()
                .message(amenityService.deleteAmenity(amenityId))
                .build();
    }

    @GetMapping
    PaginationResponse<List<Amenity>> getAmenity(@RequestParam(defaultValue = "1", name = "page") int page,
                                                 @RequestParam(defaultValue = "10", name = "take") int take,
                                                 @RequestHeader("Authorization") String token){
        AmenityPaginationDTO dto = new AmenityPaginationDTO(page, take);
        Account account = accountService.getAccountById(accountService.extractAccountIdFromToken(token));
        Page<Amenity> amenityPage = amenityService.getAmenitiesByRole(dto.page, dto.take, account);
        return PaginationResponse.<List<Amenity>>builder()
                .data(amenityPage.getContent())
                .currentPage(amenityPage.getNumber() + 1)
                .totalPage(amenityPage.getTotalPages())
                .recordPerPage(amenityPage.getNumberOfElements())
                .totalRecord((int) amenityPage.getTotalElements())
                .build();
    }

}
