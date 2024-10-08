package com.swp.PodBookingSystem.exception;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(500, "Lỗi chưa xác định", HttpStatus.INTERNAL_SERVER_ERROR, "system"),
    INVALID_KEY(500, "Lỗi chưa xác định", HttpStatus.BAD_REQUEST, "system"),
    USER_EXISTED(400, "User đã tồn tại", HttpStatus.BAD_REQUEST, "email"),
    EMAIL_EXISTED(422, "Email đã tồn tại", HttpStatus.UNPROCESSABLE_ENTITY, "email"),
    NAME_INVALID(422, "Tên chứa ít nhất 5 kí tự", HttpStatus.UNPROCESSABLE_ENTITY, "name"),
    INVALID_PASSWORD(422, "Password chứa ít nhất 6 kí tự", HttpStatus.UNPROCESSABLE_ENTITY, "password"),
    INCORRECT_PASSWORD(422, "Password không đúng", HttpStatus.UNPROCESSABLE_ENTITY, "password"),
    USER_NOT_EXIST(404, "User không tồn tại", HttpStatus.NOT_FOUND, "email"),
    EMAIL_NOT_EXIST(422, "Email không tồn tại", HttpStatus.UNPROCESSABLE_ENTITY, "email"),
    UNAUTHENTICATED(401, "Chưa được xác thực", HttpStatus.UNAUTHORIZED, "system"),
    UNAUTHORIZED(403, "Bạn không có quyền hạn", HttpStatus.FORBIDDEN, "system"),
    REFRESH_TOKEN_NOT_EXIST(404, "Refresh token không tồn tại", HttpStatus.NOT_FOUND, "refreshToken"),
    INVALID_TOKEN(401, "Token không đúng", HttpStatus.UNAUTHORIZED, "accessToken"),
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
