package com.swp.PodBookingSystem.controller;

import com.swp.PodBookingSystem.dto.respone.ApiResponse;
import com.swp.PodBookingSystem.dto.respone.ServicePackage.ServicePackageResponse;
import com.swp.PodBookingSystem.entity.Account;
import com.swp.PodBookingSystem.entity.ServicePackage;
import com.swp.PodBookingSystem.service.ServicePackageService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/service-package")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ServicePackageController {
    @Autowired
    ServicePackageService servicePackageService;

    @GetMapping
    ApiResponse<List<ServicePackage>> getServicePackages(){
//        List<ServicePackage> servicePackageList = servicePackageService.findAll();
//        servicePackageList.forEach(servicePackage -> servicePackage.setOrderDetails(null));
        return ApiResponse.<List<ServicePackage>>builder()
                .data(servicePackageService.findAll())
                .build();
    }

}
