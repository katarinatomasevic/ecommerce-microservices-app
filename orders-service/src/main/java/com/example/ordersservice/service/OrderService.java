package com.example.ordersservice.service;

import com.example.ordersservice.dtos.OrderCreateDTO;
import com.example.ordersservice.dtos.OrderDTO;
import com.example.ordersservice.model.Order;
import com.example.ordersservice.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
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
}
