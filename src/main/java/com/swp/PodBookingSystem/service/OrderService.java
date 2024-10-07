    package com.swp.PodBookingSystem.service;

    import com.swp.PodBookingSystem.dto.request.Order.OrderCreationRequest;
    import com.swp.PodBookingSystem.dto.respone.OrderResponse;
    import com.swp.PodBookingSystem.entity.Account;
    import com.swp.PodBookingSystem.entity.Order;
    import com.swp.PodBookingSystem.mapper.OrderMapper;
    import com.swp.PodBookingSystem.repository.AccountRepository;
    import com.swp.PodBookingSystem.repository.OrderRepository;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.HttpStatus;
    import org.springframework.stereotype.Service;

    import java.util.List;
    import java.util.Optional;
    import java.util.stream.Collectors;

    @Service
    public class OrderService {
        @Autowired
        private OrderRepository orderRepository;

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

        public OrderResponse createOrder(OrderCreationRequest request){
            Optional<Account> accountOptional = accountRepository.findById(request.getAccountId());

            Account account = accountOptional.get();

            // Step 3: Create the Order object
            Order order = new Order();
            order.setAccount(account); // Set the account

            Order savedOrder = orderRepository.save(order);

            // Step 5: Create and return the response
            OrderResponse response = new OrderResponse();
            response.setId(savedOrder.getId());
            response.setAccountId(savedOrder.getAccount().getId());
            response.setCreatedAt(savedOrder.getCreatedAt());
            response.setUpdatedAt(savedOrder.getUpdatedAt());

            return response;


        }
    }
