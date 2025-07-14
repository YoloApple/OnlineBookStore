package com.example.bookstore.controller;

import com.example.bookstore.dto.ReviewRequest;
import com.example.bookstore.dto.ReviewResponse;
import com.example.bookstore.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> addReview(@RequestBody ReviewRequest request, Authentication auth) {
        reviewService.addOrUpdateReview(auth.getName(), request);
        return ResponseEntity.ok("Đánh giá đã được ghi nhận.");
    }

    @GetMapping("/{bookId}")
    public ResponseEntity<List<ReviewResponse>> getReviews(@PathVariable Long bookId) {
        return ResponseEntity.ok(reviewService.getReviewsForBook(bookId));
    }

}

