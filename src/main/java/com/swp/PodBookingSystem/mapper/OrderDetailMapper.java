package com.swp.PodBookingSystem.mapper;

import com.swp.PodBookingSystem.dto.request.OrderDetail.OrderDetailCreationRequest;
import com.swp.PodBookingSystem.dto.respone.OrderDetailResponse;
import com.swp.PodBookingSystem.entity.Building;
import com.swp.PodBookingSystem.entity.OrderDetail;
import com.swp.PodBookingSystem.entity.Room;
import com.swp.PodBookingSystem.entity.ServicePackage;
import com.swp.PodBookingSystem.repository.BuildingRepository; // Assuming you have this repository
import com.swp.PodBookingSystem.repository.RoomRepository; // Assuming you have this repository
import com.swp.PodBookingSystem.repository.ServicePackageRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.persistence.EntityNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface OrderDetailMapper {


    @Mapping(target = "buildingId", source = "building.id") // Map building ID to response
    @Mapping(target = "roomId", source = "room.id") // Map room ID to response
    @Mapping(target = "servicePackageId", source = "servicePackage.id") // Map service package ID to response
    @Mapping(target = "orderId", source = "order.id") // Map order ID to response
    @Mapping(target = "orderHandledId", source = "orderHandler.id") // Map order handled ID to response
    OrderDetailResponse toOrderDetailResponse(OrderDetail orderDetail);

}
