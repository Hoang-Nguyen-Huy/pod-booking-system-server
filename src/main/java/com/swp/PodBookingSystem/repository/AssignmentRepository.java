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


    @Query("SELECT a.staff.id FROM Assignment a WHERE a.weekDate = :weekDate AND a.slot = :slot")
    List<String> findStaffIdsByWeekDateAndSlot(@Param("weekDate") String weekDate, @Param("slot") String slot);

    @Query("SELECT a.staff.id " +
            "FROM Assignment a " +
            "JOIN Account ac ON ac.id = a.staff.id " +
            "WHERE a.slot = :slot " +
            "AND a.weekDate = :weekDate " +
            "AND ac.buildingNumber = :buildingId")
    String findStaffForMatchingOrder(@Param("slot") String slot,
                                     @Param("weekDate") String weekDate,
                                     @Param("buildingId") Integer buildingId);


    @Query("SELECT a FROM Assignment a " +
            "JOIN Account acc ON a.staff.id = acc.id " +
            "WHERE (:role = 'Admin') OR " +
            "(:role = 'Manager' AND acc.buildingNumber = :buildingId) OR " +
            "(:role = 'Staff' AND acc.buildingNumber = :buildingId AND a.staff.id = :staffId)")
    List<Assignment> findAllByRoleAndBuildingAndStaff(
            @Param("role") String role,
            @Param("buildingId") Integer buildingId,
            @Param("staffId") String staffId
    );

}
