package com.swp.PodBookingSystem.exception;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(500, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(500, "Uncategorized error", HttpStatus.BAD_REQUEST),
    USER_EXISTED(400, "User existed", HttpStatus.BAD_REQUEST),
    NAME_INVALID(422, "Username must be at least 5 characters", HttpStatus.UNPROCESSABLE_ENTITY),
    INVALID_PASSWORD(422, "Password must be at least 8 characters", HttpStatus.UNPROCESSABLE_ENTITY),
    USER_NOT_EXIST(404, "User not exist", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(401, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(403, "You do not have permission", HttpStatus.FORBIDDEN),
    REFRESH_TOKEN_NOT_EXIST(404, "Refresh token not exist", HttpStatus.NOT_FOUND),
    ;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    int code;
    String message;
    private HttpStatusCode statusCode;


}
