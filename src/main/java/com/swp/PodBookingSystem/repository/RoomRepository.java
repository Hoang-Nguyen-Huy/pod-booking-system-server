package com.swp.PodBookingSystem.repository;

import com.swp.PodBookingSystem.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Integer> {
}
