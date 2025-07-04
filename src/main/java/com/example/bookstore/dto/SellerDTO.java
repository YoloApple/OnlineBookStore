package com.example.bookstore.dto;

public class SellerDTO {
    private String username;
    private String email;

    // constructor, getters, setters

    public SellerDTO(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

