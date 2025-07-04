package com.example.bookstore.service;

import com.example.bookstore.dto.BookRequest;
import com.example.bookstore.dto.BookResponseDTO;
import com.example.bookstore.dto.SellerDTO;
import com.example.bookstore.entity.Book;
import com.example.bookstore.entity.User;
import com.example.bookstore.repository.BookRepository;
import com.example.bookstore.repository.UserRepository;
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

    public void addBook(BookRequest request) {
        // Lấy Authentication từ context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        // Kiểm tra quyền ROLE_SELLER
        boolean isSeller = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_SELLER"));

        if (!isSeller) {
            throw new RuntimeException("Bạn không có quyền thêm sách.");
        }

        // Lấy người dùng từ DB
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("Người dùng không tồn tại.");
        }

        // Tạo và lưu sách mới
        User seller = userOpt.get();

        Book book = new Book();
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setPrice(request.getPrice());
        book.setDescription(request.getDescription());
        book.setSeller(seller);

        bookRepository.save(book);
        System.out.println("Username: " + username);
        System.out.println("Authorities: " + authentication.getAuthorities());

    }
    public List<Book> getBookBySeller(String sellerUsername){
        return bookRepository.findBySellerUsername(sellerUsername);
    }

    public Book updateBook(Long bookId,Book updatedBook,String sellerUsername){
        Book book = bookRepository.findById(bookId)
                .orElseThrow(()-> new RuntimeException("Sách không tồn tại"));
        if(!book.getSeller().getUsername().equals(sellerUsername)){
            throw new RuntimeException("Bạn không có quyền sửa sách này");
        }
        book.setTitle(updatedBook.getTitle());
        book.setAuthor(updatedBook.getAuthor());
        book.setPrice(updatedBook.getPrice());
        book.setDescription(updatedBook.getDescription());

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
            return new BookResponseDTO(
                    book.getId(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getPrice(),
                    book.getDescription(),
                    sellerDTO
            );
        }).collect(Collectors.toList());
    }
}
