package com.example.bookstore.entity;

import jakarta.persistence.*;

import java.text.BreakIterator;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name="orders")
public class Order {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;

    @ManyToOne
    private User user;
    private LocalDateTime orderDate;
    private double totalPrice;

    @OneToMany(mappedBy = "order",cascade = CascadeType.ALL)
    private List<OrderItem> items;

    public enum OrderStatus {
        PENDING,       // Mặc định khi mới đặt hàng
        CONFIRMED,     // Người bán đã xác nhận
        SHIPPING,      // Đang giao hàng
        DELIVERED,     // Giao hàng thành công
        CANCELLED      // Đơn bị hủy
    }

    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.PENDING;

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }
}
