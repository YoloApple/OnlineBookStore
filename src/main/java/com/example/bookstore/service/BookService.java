package com.example.bookstore.service;

import com.example.bookstore.dto.BookRequest;
import com.example.bookstore.dto.BookResponseDTO;
import com.example.bookstore.dto.SellerDTO;
import com.example.bookstore.entity.Book;
import com.example.bookstore.entity.Category;
import com.example.bookstore.entity.User;
import com.example.bookstore.repository.BookRepository;
import com.example.bookstore.repository.CategoryRepository;
import com.example.bookstore.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookService {

    @Autowired private BookRepository bookRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private CategoryRepository categoryRepository;
    public void addBook(BookRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        boolean isSeller = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_SELLER"));

        if (!isSeller) {
            throw new RuntimeException("Bạn không có quyền thêm sách.");
        }

        User seller = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại."));

        if (request.getCategoryId() == null) {
            throw new RuntimeException("Phải chọn danh mục cho sách.");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Danh mục không hợp lệ."));

        Book book = new Book();
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setPrice(request.getPrice());
        book.setDescription(request.getDescription());
        book.setSeller(seller);
        book.setCategory(category); // 🆕 gán danh mục

        bookRepository.save(book);
    }
    public List<BookResponseDTO> getBookBySeller(String sellerUsername){
        List<Book> books = bookRepository.findBySellerUsername(sellerUsername);

        return books.stream().map(book -> {
            SellerDTO sellerDTO = new SellerDTO(
                    book.getSeller().getUsername(),
                    book.getSeller().getEmail()
            );

            String categoryName = (book.getCategory() != null) ? book.getCategory().getName() : "Chưa phân loại";

            return new BookResponseDTO(
                    book.getId(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getPrice(),
                    book.getDescription(),
                    sellerDTO,
                    categoryName
            );
        }).collect(Collectors.toList());
    }

    public Book updateBook(Long bookId, BookRequest request, String sellerUsername) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Sách không tồn tại"));

        if (!book.getSeller().getUsername().equals(sellerUsername)) {
            throw new RuntimeException("Bạn không có quyền sửa sách này");
        }

        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setPrice(request.getPrice());
        book.setDescription(request.getDescription());

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Danh mục không tồn tại"));
            book.setCategory(category);
        }

        return bookRepository.save(book);
    }


    public void deleteBook(Long bookId, String sellerUsername) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Sách không tồn tại"));

        if (!book.getSeller().getUsername().equals(sellerUsername)) {
            throw new RuntimeException("Bạn không có quyền xóa sách này");
        }

        bookRepository.delete(book);
    }
    public List<BookResponseDTO> getAllBooks() {
        return bookRepository.findAll().stream().map(book -> {
            SellerDTO sellerDTO = new SellerDTO(
                    book.getSeller().getUsername(),
                    book.getSeller().getEmail()
            );
            String categoryName = (book.getCategory() != null) ? book.getCategory().getName() : "Chưa phân loại";
            return new BookResponseDTO(
                    book.getId(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getPrice(),
                    book.getDescription(),
                    sellerDTO,
                    categoryName
            );
        }).collect(Collectors.toList());
    }
    @Transactional
    public void assignUncategorizedToOldBooks() {
        Category defaultCategory = categoryRepository.findByName("Chưa phân loại")
                .orElseGet(() -> {
                    Category newCategory = new Category();
                    newCategory.setName("Chưa phân loại");
                    return categoryRepository.save(newCategory);
                });

        List<Book> booksWithoutCategory = bookRepository.findByCategoryIsNull();

        for (Book book : booksWithoutCategory) {
            book.setCategory(defaultCategory);
        }

        bookRepository.saveAll(booksWithoutCategory);
    }
}
