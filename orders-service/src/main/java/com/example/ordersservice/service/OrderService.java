package com.example.ordersservice.service;

import com.example.ordersservice.client.UserClient;
import com.example.ordersservice.dtos.OrderCreateDTO;
import com.example.ordersservice.dtos.OrderDTO;
import com.example.ordersservice.dtos.OrderWithUserDTO;
import com.example.ordersservice.dtos.UserDTO;
import com.example.ordersservice.model.Order;
import com.example.ordersservice.repository.OrderRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserClient userClient;

    public OrderService(OrderRepository orderRepository,  UserClient userClient) {

        this.orderRepository = orderRepository;
        this.userClient = userClient;
    }

    private static OrderDTO toDto(Order order) {
        return new OrderDTO(
                order.getId(),
                order.getUserId(),
                order.getProductName(),
                order.getQuantity(),
                order.getPrice()
        );
    }

    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(OrderService::toDto)
                .collect(Collectors.toList());
    }

    public OrderDTO getOrderById(Long id) {
        return orderRepository.findById(id)
                .map(OrderService::toDto)
                .orElseThrow(() -> new RuntimeException("Order not found with id " + id));
    }

    public OrderDTO createOrder(OrderCreateDTO dto) {
        Order order = Order.builder()
                .userId(dto.getUserId())
                .productName(dto.getProductName())
                .quantity(dto.getQuantity())
                .price(dto.getPrice())
                .build();

        return toDto(orderRepository.save(order));
    }

    public OrderDTO updateOrder(Long id, OrderCreateDTO dto) {
        Order existing = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id " + id));

        existing.setUserId(dto.getUserId());
        existing.setProductName(dto.getProductName());
        existing.setQuantity(dto.getQuantity());
        existing.setPrice(dto.getPrice());

        return toDto(orderRepository.save(existing));
    }

    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }

    @CircuitBreaker(name = "userServiceCircuitBreaker", fallbackMethod = "getOrderWithUserFallback")
    @Retry(name = "userServiceRetry")
    public Optional<OrderWithUserDTO> getOrderWithUser(Long orderId) {
        return orderRepository.findById(orderId)
                .map(order -> {
                    ResponseEntity<UserDTO> response = userClient.getUserById(order.getUserId());

                    if (response.getStatusCode().is5xxServerError()) {
                        throw new ResponseStatusException(response.getStatusCode(), "User Service unavailable.");
                    }

                    UserDTO user;
                    if (response.getStatusCode() == HttpStatus.NOT_FOUND || response.getBody() == null) {
                        user = new UserDTO(0L, "UNKNOWN", "UNKNOWN", "unknown@example.com");
                    }
                    else {
                        user = response.getBody();
                    }

                    return OrderWithUserDTO.builder()
                            .orderId(order.getId())
                            .productName(order.getProductName())
                            .quantity(order.getQuantity())
                            .price(order.getPrice())
                            .user(user)
                            .build();
                });
    }

    public Optional<OrderWithUserDTO>  getOrderWithUserFallback(Long orderId, Throwable t) {
        return orderRepository.findById(orderId)
                .map(order -> OrderWithUserDTO.builder()
                        .orderId(order.getId())
                        .productName(order.getProductName())
                        .quantity(order.getQuantity())
                        .price(order.getPrice())
                        .user(new UserDTO(0L, "UNKNOWN", "UNKNOWN", "unknown@example.com"))
                        .build());
    }
}
