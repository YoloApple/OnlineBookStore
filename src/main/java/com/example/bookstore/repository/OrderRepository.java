package com.example.bookstore.repository;


import com.example.bookstore.entity.Order;
import com.example.bookstore.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);
    @Query("SELECT DISTINCT o FROM Order o JOIN o.items i WHERE i.book.seller = :seller")
    List<Order> findOrdersBySeller(@Param("seller") User seller);
    @Query("SELECT DISTINCT o FROM Order o JOIN o.items i WHERE i.book.seller.username = :sellerUsername")
    List<Order> findOrdersBySellerUsername(String sellerUsername);
}

