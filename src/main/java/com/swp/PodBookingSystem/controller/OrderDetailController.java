    package com.swp.PodBookingSystem.controller;

    import com.swp.PodBookingSystem.dto.request.OrderDetail.OrderDetailRequestDTO;
    import com.swp.PodBookingSystem.dto.respone.OrderDetail.OrderDetailResponse;
    import com.swp.PodBookingSystem.service.OrderDetailService;
    import lombok.AccessLevel;
    import lombok.RequiredArgsConstructor;
    import lombok.experimental.FieldDefaults;
    import lombok.extern.slf4j.Slf4j;
    import org.springframework.beans.factory.annotation.Autowired;
    import com.swp.PodBookingSystem.dto.respone.ApiResponse;

    import org.springframework.http.HttpStatus;
    import org.springframework.security.core.context.SecurityContextHolder;
    import org.springframework.web.bind.annotation.*;

    import java.util.*;

    @RestController
    @RequestMapping("/order-detail")
    @RequiredArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    @Slf4j
    public class OrderDetailController {

        @Autowired
        private OrderDetailService orderDetailService;

        @GetMapping
        public ApiResponse<List<OrderDetailResponse>> getAllOrders() {
            try{
                List<OrderDetailResponse> orders = orderDetailService.getAllOrders();
                logOrders(orders);
                return ApiResponse.<List<OrderDetailResponse>>builder()
                        .data(orders)
                        .build();
            }
            catch (Exception e){
                log.error("Error creating order detail: ", e);
                return ApiResponse.<List<OrderDetailResponse>>builder()
                        .message("Failed to create order detail: " + e.getMessage())
                        .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .build();
            }
        }

        @GetMapping("/{customerId}")
        public ApiResponse<List<OrderDetailResponse>> getOrdersByCustomerId(@PathVariable String customerId) {
            List<OrderDetailResponse> orders = orderDetailService.getOrdersByCustomerId(customerId);
            logOrders(orders);
            return ApiResponse.<List<OrderDetailResponse>>builder()
                    .data(orders)
                    .build();
        }


        @PutMapping("/{orderDetailId}")
        ApiResponse<OrderDetailResponse> updateOrderDetail(@PathVariable("orderDetailId") String orderDetailId,
                                                           @RequestBody OrderDetailRequestDTO request){
            return ApiResponse.<OrderDetailResponse>builder()
                    .data(orderDetailService.updateOrderDetail(orderDetailId, request))
                    .message("Update orderDetailId successfully")
                    .build();
        }



        private void logOrders(List<OrderDetailResponse> orders) {
            var authentication = SecurityContextHolder.getContext().getAuthentication();
            log.info("Username: {}", authentication.getName());
            log.info("Number of orders retrieved: {}", orders.size());
            orders.forEach(order -> log.info("Order ID: {}, Customer ID: {}", order.getId(), order.getCustomerId()));
        }

    }