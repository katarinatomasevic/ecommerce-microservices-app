package com.example.ordersservice.client;

import com.example.ordersservice.dtos.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "users-service")
public interface UserClient {
    @GetMapping("/api/users/{id}")
    ResponseEntity<UserDTO> getUserById(@PathVariable("id") Long id);
}
