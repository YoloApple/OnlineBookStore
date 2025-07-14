package com.example.bookstore.service;

import com.example.bookstore.entity.Order;
import com.example.bookstore.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ShippingService {

    @Autowired
    private OrderRepository orderRepository;

    public String confirmShipping(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getPaymentStatus() != Order.PaymentStatus.PAID) {
            throw new IllegalStateException("Order must be PAID before shipping.");
        }

        if (order.getStatus() != Order.OrderStatus.CONFIRMED) {
            throw new IllegalStateException("Order must be CONFIRMED to start shipping.");
        }

        if (order.getShippingAddress() == null) {
            throw new IllegalStateException("Shipping address is required before shipping.");
        }

        order.setStatus(Order.OrderStatus.SHIPPING);
        order.setShippingCode("SHIP" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        orderRepository.save(order);

        return "Order marked as SHIPPING with code " + order.getShippingCode();
    }

    public String markDelivered(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getStatus() != Order.OrderStatus.SHIPPING) {
            throw new IllegalStateException("Order must be SHIPPING to mark delivered.");
        }

        order.setStatus(Order.OrderStatus.DELIVERED);
        order.setDeliveredAt(LocalDateTime.now());
        orderRepository.save(order);

        return "Order marked as DELIVERED.";
    }
}

