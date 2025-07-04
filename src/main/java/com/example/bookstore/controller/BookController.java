package com.example.bookstore.controller;

import com.example.bookstore.dto.BookRequest;
import com.example.bookstore.entity.Book;
import com.example.bookstore.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books")

public class BookController {

    @Autowired private BookService bookService;
    @GetMapping("")
    public ResponseEntity<?> getAllBooks() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }
    @PostMapping("/add")
    @PreAuthorize("hasRole('SELLER')")
    public String addBook(@RequestBody BookRequest request) {
        bookService.addBook(request);
        return "Thêm sách thành công!";
    }

    @GetMapping("/my-books")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<?> getBooksBySeller(Authentication auth){
        return ResponseEntity.ok(bookService.getBookBySeller(auth.getName()));
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<?> updateBook(@PathVariable Long id,
                                        @RequestBody Book book,
                                        Authentication auth){
        return ResponseEntity.ok(bookService.updateBook(id,book,auth.getName()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<?> deleteBook(@PathVariable Long id,Authentication auth){
        bookService.deleteBook(id,auth.getName());
        return ResponseEntity.ok("Xóa thành công");
    }
}
