package com.swp.PodBookingSystem.dto.respone;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaginationResponse<T> {
    @Builder.Default
    int code = 200;
    String message;
    T data;
    int currentPage;
    int totalPage;
    int recordPerPage;
    int totalRecord;
}
