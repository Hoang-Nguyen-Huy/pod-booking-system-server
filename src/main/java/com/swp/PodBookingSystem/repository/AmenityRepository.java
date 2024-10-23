package com.swp.PodBookingSystem.repository;

import com.swp.PodBookingSystem.entity.Amenity;
import com.swp.PodBookingSystem.enums.AmenityType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AmenityRepository extends JpaRepository<Amenity, Integer> {
    List<Amenity> findAllByType(AmenityType type);

    Page<Amenity> findAllByBuildingId(Integer buildingId, Pageable pageable);
}
