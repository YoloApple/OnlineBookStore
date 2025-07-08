package com.example.bookstore.controller;


import com.example.bookstore.dto.OrderResponse;
import com.example.bookstore.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired private OrderService orderService;

    // Đặt hàng
    @PostMapping("/place")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> placeOrder(Authentication auth) {
        orderService.placeOrder(auth.getName());
        return ResponseEntity.ok("Đặt hàng thành công!");
    }

    // Xem lịch sử đơn hàng
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<OrderResponse>> getOrders(Authentication auth) {
        return ResponseEntity.ok(orderService.getUserOrders(auth.getName()));
    }
}

