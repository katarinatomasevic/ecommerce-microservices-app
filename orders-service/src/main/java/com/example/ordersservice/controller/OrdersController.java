package com.example.ordersservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrdersController {
    @GetMapping("/hello")
    public String hello() {
        return "Hello from orders service!";
    }
}
