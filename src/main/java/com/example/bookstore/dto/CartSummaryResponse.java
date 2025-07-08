package com.example.bookstore.dto;

import java.util.List;

public class CartSummaryResponse {
    private List<CartItemResponse> items;
    private double grandTotal;

    //Getters and Setters

    public List<CartItemResponse> getItems() {
        return items;
    }

    public void setItems(List<CartItemResponse> items) {
        this.items = items;
    }

    public double getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(double grandTotal) {
        this.grandTotal = grandTotal;
    }
}
