package com.example.bookstore.repository;

import com.example.bookstore.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByCartUserUsernameAndBookId(String username, Long bookId);
    List<CartItem> findByCartUserUsername(String username);
    void deleteByCartUserUsernameAndBookId(String username, Long bookId);
}

