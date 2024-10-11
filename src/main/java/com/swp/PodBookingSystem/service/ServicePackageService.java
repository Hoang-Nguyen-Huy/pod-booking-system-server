package com.swp.PodBookingSystem.service;

import com.swp.PodBookingSystem.dto.respone.ServicePackage.ServicePackageResponse;
import com.swp.PodBookingSystem.entity.ServicePackage;
import com.swp.PodBookingSystem.repository.ServicePackageRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ServicePackageService {

    @Autowired
    ServicePackageRepository servicePackageRepository;

    public List<ServicePackage> findAll() {
        return servicePackageRepository.findAll();
    }

    public ServicePackageResponse toServicePackageResponse(ServicePackage servicePackage) {
        return ServicePackageResponse.builder()
                .id(servicePackage.getId())
                .name(servicePackage.getName())
                .discountPercentage(servicePackage.getDiscountPercentage())
                .build();
    }
}
