package com.swp.PodBookingSystem.dto.respone;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TransactionStatusDTO implements Serializable {
    String status;
    String message;
    String data;
