package com.swp.PodBookingSystem.repository;

import com.swp.PodBookingSystem.entity.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, String> {
    List<Assignment> findByStaffId(String staffId);

}
