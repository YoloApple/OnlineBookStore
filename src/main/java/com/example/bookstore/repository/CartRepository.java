package com.example.bookstore.repository;

import com.example.bookstore.entity.Cart;
import com.example.bookstore.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart,Long> {
    Optional<CartRepository> findByUser(User user);
}
