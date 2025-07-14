package com.example.bookstore.service;

import com.example.bookstore.dto.ReviewRequest;
import com.example.bookstore.dto.ReviewResponse;
import com.example.bookstore.entity.Book;
import com.example.bookstore.entity.Order;
import com.example.bookstore.entity.Review;
import com.example.bookstore.entity.User;
import com.example.bookstore.repository.BookRepository;
import com.example.bookstore.repository.OrderRepository;
import com.example.bookstore.repository.ReviewRepository;
import com.example.bookstore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReviewService {

    @Autowired private ReviewRepository reviewRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired private BookRepository bookRepository;
    @Autowired private OrderRepository orderRepository;

    public void addOrUpdateReview(String username, ReviewRequest request) {
        User user = userRepository.findByUsername(username).orElseThrow();
        Book book = bookRepository.findById(request.getBookId()).orElseThrow();

        // ✅ Kiểm tra user đã mua sách chưa
        boolean hasPurchased = orderRepository.findByUser(user).stream()
                .filter(o -> o.getStatus() == Order.OrderStatus.DELIVERED)
                .flatMap(o -> o.getItems().stream())
                .anyMatch(item -> item.getBook().getId().equals(book.getId()));


        if (!hasPurchased) {
            throw new RuntimeException("Bạn chưa mua sách này nên không thể đánh giá.");
        }

        // ✅ Nếu đã từng review → cập nhật
        Review review = reviewRepository.findByBookAndUser(book, user)
                .orElse(new Review());

        review.setUser(user);
        review.setBook(book);
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setCreatedAt(LocalDateTime.now());

        reviewRepository.save(review);
    }

    public List<ReviewResponse> getReviewsForBook(Long bookId) {
        Book book = bookRepository.findById(bookId).orElseThrow();

        return reviewRepository.findByBook(book).stream().map(r -> {
            ReviewResponse res = new ReviewResponse();
            res.setUsername(r.getUser().getUsername());
            res.setRating(r.getRating());
            res.setComment(r.getComment());
            res.setCreatedAt(r.getCreatedAt());
            return res;
        }).toList();
    }
}

