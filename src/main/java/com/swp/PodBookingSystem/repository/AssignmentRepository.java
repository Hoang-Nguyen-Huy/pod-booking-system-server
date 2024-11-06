package com.swp.PodBookingSystem.repository;

import com.swp.PodBookingSystem.entity.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, String> {
    List<Assignment> findByStaffId(String staffId);


    @Query("SELECT a.staffId FROM Assignment a WHERE a.weekDate = :weekDate AND a.slot = :slot")
    List<String> findStaffIdsByWeekDateAndSlot(@Param("weekDate") String weekDate, @Param("slot") String slot);

}
