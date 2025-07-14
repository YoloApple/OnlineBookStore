package com.example.bookstore.controller;


import com.example.bookstore.dto.*;
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

    // controller/OrderController.java
    @GetMapping("/seller")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<List<OrderSellerDto>> getOrdersForSeller(Authentication auth) {
        String sellerUsername = auth.getName();
        List<OrderSellerDto> orders = orderService.getOrdersForSeller(sellerUsername);
        return ResponseEntity.ok(orders);
    }
    @PutMapping("/update-status")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<String> updateOrderStatus(@RequestBody UpdateOrderStatusRequest request,
                                                    Authentication auth) {
        String sellerUsername = auth.getName();
        orderService.updateOrderStatus(sellerUsername, request);
        return ResponseEntity.ok("Cập nhật trạng thái đơn hàng thành công!");
    }

    @PostMapping("/statistics")
    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    public ResponseEntity<StatisticResponse> getStatistics(@RequestBody StatisticRequest request,
                                                           Authentication auth) {
        String username = auth.getName();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        StatisticResponse response = orderService.getStatistics(username, isAdmin, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{orderId}/choose-address/{addressId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> chooseAddress(@PathVariable Long orderId,
                                           @PathVariable Long addressId,
                                           Authentication auth) {
        try {
            orderService.assignShippingAddress(auth.getName(), orderId, addressId);
            return ResponseEntity.ok("Đã chọn địa chỉ giao hàng");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}

