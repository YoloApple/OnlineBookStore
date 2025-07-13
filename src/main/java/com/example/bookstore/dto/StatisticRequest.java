package com.example.bookstore.dto;
import com.example.bookstore.entity.Order;
import java.time.LocalDate;

public class StatisticRequest{
    private LocalDate startDate;
    private LocalDate endDate;
    private Order.OrderStatus status; // có thể null nếu không lọc theo trạng thái

    // Getters & Setters
    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Order.OrderStatus getStatus() {
        return status;
    }

    public void setStatus(Order.OrderStatus status) {
        this.status = status;
    }
}

