package com.example.bookstore.repository;

import com.example.bookstore.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface CategoryRepository extends JpaRepository<Category,Long> {
    boolean existsByName(String name);
    Optional<Category> findByName(String name);
}
