package com.swp.PodBookingSystem.repository;

import com.swp.PodBookingSystem.entity.Amenity;
import com.swp.PodBookingSystem.enums.AmenityType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AmenityRepository extends JpaRepository<Amenity, Integer> {
    List<Amenity> findAllByType(AmenityType type);

    Page<Amenity> findAllByBuildingId(Integer buildingId, Pageable pageable);

    @Query("SELECT a FROM Amenity a WHERE a.isDeleted = 0")
    List<Amenity> findAllActiveAmenities();
}
