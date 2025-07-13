package com.example.bookstore.dto;

public class StatisticResponse {
    private long totalOrders;
    private double totalRevenue;
    private long totalBooksSold;

    public StatisticResponse(long totalOrders, double totalRevenue, long totalBooksSold) {
        this.totalOrders = totalOrders;
        this.totalRevenue = totalRevenue;
        this.totalBooksSold = totalBooksSold;
    }

    public long getTotalOrders() {
        return totalOrders;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public long getTotalBooksSold() {
        return totalBooksSold;
    }
}
