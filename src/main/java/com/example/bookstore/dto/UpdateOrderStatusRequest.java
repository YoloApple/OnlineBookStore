package com.example.bookstore.dto;

import com.example.bookstore.entity.Order;

public class UpdateOrderStatusRequest {
    private Long orderId;
    private Order.OrderStatus newStatus;

    // Getters and Setters

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Order.OrderStatus getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(Order.OrderStatus newStatus) {
        this.newStatus = newStatus;
    }
}
