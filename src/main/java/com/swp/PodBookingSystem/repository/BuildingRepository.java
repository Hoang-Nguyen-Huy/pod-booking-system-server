package com.swp.PodBookingSystem.repository;

import com.swp.PodBookingSystem.entity.Building;
import com.swp.PodBookingSystem.entity.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BuildingRepository extends JpaRepository<Building, Integer> {
    @Query("SELECT b FROM Building b WHERE b.address LIKE %:keyword% OR b.description LIKE %:keyword%")
    List<Building> searchByKeyword(String keyword);

    @Query("SELECT b FROM Building b " +
            "WHERE (b.address LIKE %:address%)" +
            "ORDER BY b.createdAt DESC")
    Page<Building> findFilteredBuildings(@Param("address") String address,
                                         Pageable pageable);
}
