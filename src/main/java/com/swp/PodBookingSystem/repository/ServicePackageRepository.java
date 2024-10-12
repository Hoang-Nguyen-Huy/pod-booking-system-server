package com.swp.PodBookingSystem.repository;

import com.swp.PodBookingSystem.dto.respone.ServicePackage.ServicePackageResponse;
import com.swp.PodBookingSystem.entity.Amenity;
import com.swp.PodBookingSystem.entity.ServicePackage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServicePackageRepository extends JpaRepository<ServicePackage,Integer> {
}
