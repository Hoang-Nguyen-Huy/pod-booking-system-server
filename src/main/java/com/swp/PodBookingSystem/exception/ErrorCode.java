package com.swp.PodBookingSystem.exception;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(500, "Uncategorized error"),
    INVALID_KEY(500, "Uncategorized error"),
    NAME_INVALID(402, "Username must be at least 5 characters"),
    INVALID_PASSWORD(402, "Password must be at least 8 characters"),
    USER_NOT_EXIST(402, "User not exist"),
    ;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    int code;
    String message;

//    public int getCode() {
//        return code;
//    }
//    public String getMessage() {
//        return message;
//    }

}
