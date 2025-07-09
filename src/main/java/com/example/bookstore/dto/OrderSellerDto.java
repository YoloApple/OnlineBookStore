package com.example.bookstore.dto;

import java.time.LocalDateTime;
import java.util.List;

public class OrderSellerDto {
    private Long orderId;
    private String buyerUsername;
    private LocalDateTime orderDate;
    private double totalPrice;
    private List<OrderItemSellerDto> items;

    // Getters & Setters

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getBuyerUsername() {
        return buyerUsername;
    }

    public void setBuyerUsername(String buyerUsername) {
        this.buyerUsername = buyerUsername;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public List<OrderItemSellerDto> getItems() {
        return items;
    }

    public void setItems(List<OrderItemSellerDto> items) {
        this.items = items;
    }
}
