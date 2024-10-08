package com.swp.PodBookingSystem.service;
import com.swp.PodBookingSystem.dto.request.Order.OrderCreationRequest;
    import com.swp.PodBookingSystem.dto.request.OrderDetail.OrderDetailCreationRequest;
    import com.swp.PodBookingSystem.dto.respone.OrderResponse;
    import com.swp.PodBookingSystem.entity.Account;
    import com.swp.PodBookingSystem.entity.Order;
    import com.swp.PodBookingSystem.entity.OrderDetail;
    import com.swp.PodBookingSystem.mapper.OrderMapper;
    import com.swp.PodBookingSystem.repository.AccountRepository;
    import com.swp.PodBookingSystem.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.HttpStatus;
    import org.springframework.stereotype.Service;

    import java.time.LocalDateTime;
    import java.util.List;
    import java.util.Optional;
    import java.util.UUID;
    import java.util.stream.Collectors;
    @Service
    public class OrderService {
        @Autowired
        private OrderRepository orderRepository;

        private static final Logger log = LoggerFactory.getLogger(OrderService.class);

        @Autowired
        private OrderMapper orderMapper;
        @Autowired
        private AccountRepository accountRepository;

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
                System.out.println("Order ID    : " + order.getId());
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

        public Order createOrderByRequest(OrderDetailCreationRequest request, Account account) {
            try {
                Order order = new Order();
                order.setAccount(account); // Set the account
                order.setId(UUID.randomUUID().toString());
                order.setCreatedAt(LocalDateTime.now());
                order.setUpdatedAt(LocalDateTime.now());

                // Save the order to the repository
                order = orderRepository.save(order);

                return order;
            } catch (Exception e) {
                // Log the error message and handle the exception appropriately
                log.error("Error creating order: ", e);
                throw new RuntimeException("Failed to create order: " + e.getMessage());
            }
        }

        public Order createOrder(Account customer){
            Order order = new Order();
            order.setId(UUID.randomUUID().toString());
            order.setAccount(customer);
            order.setCreatedAt(LocalDateTime.now());
            order.setUpdatedAt(LocalDateTime.now());
            orderRepository.save(order);
            return order;
        }
    }
