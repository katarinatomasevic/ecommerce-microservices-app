package com.example.ordersservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class OrderCreateDTO {
    private Long userId;
    private String productName;
    private int quantity;
    private double price;
}
