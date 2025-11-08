package com.swp.PodBookingSystem.exception;

import com.swp.PodBookingSystem.dto.respone.ApiResponse;  // Giả sử path đúng (lưu ý: "respone" có thể typo? Nên là "response")
import com.swp.PodBookingSystem.exception.AppException;  // Import custom AppException nếu chưa có
import com.swp.PodBookingSystem.exception.ErrorCode;  // Import enum ErrorCode nếu chưa có
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;  // Đổi thành RestControllerAdvice

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice  // Fix: Tự động @ResponseBody cho JSON response
public class GlobalExceptionHandler {

    // Fix: Đổi annotation thành RuntimeException.class để match param, hoặc dùng Exception làm param (tôi chọn cách 1)
    // Return 500 cho lỗi chung (không phải 400)
    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<ApiResponse> handlingRuntimeException(RuntimeException exception) {
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode());
        apiResponse.setMessage(exception.getMessage() != null ? exception.getMessage() : ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage());  // Log message cụ thể nếu có
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);  // Fix: 500 thay vì 400
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    public ResponseEntity<ApiResponse> handlingAccessDeniedException(AccessDeniedException exception) {
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;
        return ResponseEntity.status(errorCode.getStatusCode()).body(
                ApiResponse.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(value = AppException.class)
    public ResponseEntity<ApiResponse> handlingAppException(AppException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());
        Map<String, String> data = new HashMap<>();
        data.put(errorCode.getField(), errorCode.getMessage());  // Giả sử ErrorCode có getField()
        apiResponse.setData(data);
        return ResponseEntity.status(errorCode.getStatusCode()).body(apiResponse);
    }

    // Fix: Xử lý null cho getFieldError(), fallback nếu không có field error
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handlingValidation(MethodArgumentNotValidException exception) {
        ErrorCode errorCode = ErrorCode.INVALID_KEY;  // Default
        if (exception.getFieldError() != null) {  // Fix: Check null
            String enumKey = exception.getFieldError().getDefaultMessage();
            try {
                errorCode = ErrorCode.valueOf(enumKey);
            } catch (IllegalArgumentException e) {
                // Giữ default hoặc log warning
            }
        }
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());
        return ResponseEntity.unprocessableEntity().body(apiResponse);  // 422 OK
    }

    // Thêm: Catch-all cho các exception không match (an toàn hơn)
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ApiResponse> handleGenericException(Exception exception) {
        // Log full stack trace ở đây nếu cần (sử dụng SLF4J Logger)
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode());
        apiResponse.setMessage("Internal Server Error: " + exception.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
    }
}