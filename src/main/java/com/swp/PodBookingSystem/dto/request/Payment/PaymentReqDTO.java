package com.swp.PodBookingSystem.dto.request.Payment;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentReqDTO {
    @NotNull
    @Min(1000)
    Long amount;
    @NotEmpty
    String orderId;
}
