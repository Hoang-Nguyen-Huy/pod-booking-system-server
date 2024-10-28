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
    private int code = 200;
    private String message;
    private T data;
    private int currentPage;
    private int totalPage;
    private int recordPerPage;
    private int totalRecord;
}
