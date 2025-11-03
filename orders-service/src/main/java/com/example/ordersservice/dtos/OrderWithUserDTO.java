package com.example.ordersservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderWithUserDTO {
    private Long orderId;
    private String productName;
    private int quantity;
    private double price;
    private UserDTO user;
}
