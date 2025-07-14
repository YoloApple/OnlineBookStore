package com.example.bookstore.service;

import com.example.bookstore.entity.Order;
import com.example.bookstore.entity.Payment;
import com.example.bookstore.repository.OrderRepository;
import com.example.bookstore.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PaymentService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    public String processMockPayment(Long orderId, String username) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getUser().getUsername().equals(username)) {
            throw new SecurityException("You are not the owner of this order.");
        }

        if (order.getStatus() != Order.OrderStatus.CONFIRMED) {
            throw new IllegalStateException("Order must be CONFIRMED before payment.");
        }

        if (order.getPaymentStatus() == Order.PaymentStatus.PAID) {
            throw new IllegalStateException("Order already paid.");
        }

        order.setPaymentStatus(Order.PaymentStatus.PAID);
        order.setPaidAt(LocalDateTime.now());
        orderRepository.save(order);

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setMethod("Mock");
        payment.setTransactionCode("TXN" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        payment.setCreatedAt(LocalDateTime.now());
        paymentRepository.save(payment);

        return "Payment successful (mocked)";
    }
}

