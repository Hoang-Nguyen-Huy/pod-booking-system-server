package com.swp.PodBookingSystem.exception;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(500, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR, "system"),
    INVALID_KEY(500, "Uncategorized error", HttpStatus.BAD_REQUEST, "system"),
    USER_EXISTED(400, "User existed", HttpStatus.BAD_REQUEST, "email"),
    NAME_INVALID(422, "Username must be at least 5 characters", HttpStatus.UNPROCESSABLE_ENTITY, "name"),
    INVALID_PASSWORD(422, "Password must be at least 8 characters", HttpStatus.UNPROCESSABLE_ENTITY, "password"),
    INCORRECT_PASSWORD(422, "Incorrect password", HttpStatus.UNPROCESSABLE_ENTITY, "password"),
    USER_NOT_EXIST(404, "User not exist", HttpStatus.NOT_FOUND, "email"),
    UNAUTHENTICATED(401, "Unauthenticated", HttpStatus.UNAUTHORIZED, "system"),
    UNAUTHORIZED(403, "You do not have permission", HttpStatus.FORBIDDEN, "system"),
    REFRESH_TOKEN_NOT_EXIST(404, "Refresh token not exist", HttpStatus.NOT_FOUND, "refreshToken"),
    ;

    ErrorCode(int code, String message, HttpStatusCode statusCode, String field) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
        this.field = field;
    }

    int code;
    String message;
    private HttpStatusCode statusCode;
    String field;


}
