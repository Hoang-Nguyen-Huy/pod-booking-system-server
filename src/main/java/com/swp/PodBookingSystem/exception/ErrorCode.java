package com.swp.PodBookingSystem.exception;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(500, "Uncategorized error"),
    INVALID_KEY(500, "Uncategorized error"),
    NAME_INVALID(422, "Username must be at least 5 characters"),
    INVALID_PASSWORD(422, "Password must be at least 8 characters"),
    USER_NOT_EXIST(422, "User not exist"),
    UNAUTHENTICATED(401, "Unauthenticated"),
    ;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    int code;
    String message;


}
