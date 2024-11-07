package com.swp.PodBookingSystem.repository;

import com.swp.PodBookingSystem.entity.RoomImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RoomImageRepository extends JpaRepository<RoomImage, Integer> {

    @Query("SELECT ri FROM RoomImage ri WHERE ri.room.id = :roomId")
    List<RoomImage> findAllImagesByRoomId(int roomId);
}
