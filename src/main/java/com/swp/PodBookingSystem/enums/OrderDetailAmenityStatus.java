package com.swp.PodBookingSystem.enums;
import lombok.Getter;

@Getter
public enum OrderDetailAmenityStatus {
    Booked("Đã đặt"),
    Paid("Đã thanh toán"),
    Delivered("Đã giao"),
    Canceled("Đã hủy");

    private final String description;

    OrderDetailAmenityStatus(String description) {
        this.description = description;
    }
}
