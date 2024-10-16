package com.swp.PodBookingSystem.repository;

import com.swp.PodBookingSystem.entity.Building;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BuildingRepository extends JpaRepository<Building, Integer> {
    @Query("SELECT b FROM Building b WHERE b.address LIKE %:keyword% OR b.description LIKE %:keyword%")
    List<Building> searchByKeyword(String keyword);
}
