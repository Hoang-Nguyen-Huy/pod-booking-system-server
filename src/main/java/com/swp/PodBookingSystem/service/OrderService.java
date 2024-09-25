    package com.swp.PodBookingSystem.service;

    import com.swp.PodBookingSystem.dto.respone.OrderResponse;
    import com.swp.PodBookingSystem.entity.Order;
    import com.swp.PodBookingSystem.mapper.OrderMapper;
    import com.swp.PodBookingSystem.repository.OrderRepository;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Service;

    import java.util.List;
    import java.util.stream.Collectors;

    @Service
    public class OrderService {
        @Autowired
        private OrderRepository orderRepository;

        @Autowired
        private OrderMapper orderMapper;

        public List<OrderResponse> getAllOrders() {
            List<Order> orders = orderRepository.findAll();
            return orders.stream()
                    .map(orderMapper::toOrderResponse)
                    .collect(Collectors.toList());
        }

        public List<OrderResponse> getOrdersByAccountId(String accountId) {
            List<Order> orders = orderRepository.findByAccountId(accountId);

            System.out.println("Orders found for account " + accountId + ": " + orders.size());

            for (Order order : orders) {
                System.out.println("Order ID: " + order.getId());
                System.out.println("Created At: " + order.getCreatedAt());
                System.out.println("Updated At: " + order.getUpdatedAt());
                System.out.println("Account Id: " + order.getAccount().getId());
                System.out.println();
                System.out.println("-------------------------------");
            }

            return orders.stream()
                    .map(orderMapper::toOrderResponse)
                    .collect(Collectors.toList());
        }
    }
