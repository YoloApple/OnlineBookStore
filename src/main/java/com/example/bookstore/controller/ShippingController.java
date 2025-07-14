package com.example.bookstore.controller;

import com.example.bookstore.service.ShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/shipping")
public class ShippingController {

    @Autowired
    private ShippingService shippingService;

    @PutMapping("/{orderId}/confirm-shipping")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<?> confirmShipping(@PathVariable Long orderId) {
        try {
            String result = shippingService.confirmShipping(orderId);
            return ResponseEntity.ok(result);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/{orderId}/mark-delivered")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<?> markDelivered(@PathVariable Long orderId) {
        try {
            String result = shippingService.markDelivered(orderId);
            return ResponseEntity.ok(result);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}

