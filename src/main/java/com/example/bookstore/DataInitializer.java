package com.example.bookstore;


import com.example.bookstore.entity.Book;
import com.example.bookstore.entity.Category;
import com.example.bookstore.service.BookService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private BookService bookService;

    @Override
    public void run(String... args) throws Exception {
        bookService.assignUncategorizedToOldBooks();
        System.out.println("✅ Gán danh mục 'Chưa phân loại' cho sách cũ (nếu cần)");
    }
}

