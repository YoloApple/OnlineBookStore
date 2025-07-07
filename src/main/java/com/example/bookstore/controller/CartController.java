package com.example.bookstore.controller;

import com.example.bookstore.dto.CartItemRequest;
import com.example.bookstore.dto.CartItemResponse;
import com.example.bookstore.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    // Thêm sách vào giỏ hàng
    @PostMapping("/add")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> addToCart(@RequestBody CartItemRequest request,
                                            Authentication authentication) {
        cartService.addToCart(authentication.getName(), request);
        return ResponseEntity.ok("Thêm vào giỏ hàng thành công");
    }

    // Lấy toàn bộ giỏ hàng của người dùng
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<CartItemResponse>> getCart(Authentication authentication) {
        List<CartItemResponse> cart = cartService.getCart(authentication.getName());
        return ResponseEntity.ok(cart);
    }

    // Cập nhật số lượng sách trong giỏ
    @PutMapping("/update")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CartItemResponse> updateCartItem(@RequestBody CartItemRequest request,
                                                           Authentication authentication) {
        CartItemResponse updatedItem = cartService.updateCartItem(authentication.getName(), request);
        return ResponseEntity.ok(updatedItem);
    }

    // Xóa 1 item khỏi giỏ
    @DeleteMapping("/remove/{bookId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> removeCartItem(@PathVariable Long bookId,
                                                 Authentication authentication) {
        cartService.removeItemFromCart(authentication.getName(), bookId);
        return ResponseEntity.ok("Đã xóa sản phẩm khỏi giỏ hàng");
    }

    // Xóa toàn bộ giỏ hàng
    @DeleteMapping("/clear")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> clearCart(Authentication authentication) {
        cartService.clearCart(authentication.getName());
        return ResponseEntity.ok("Đã xóa toàn bộ giỏ hàng");
    }
}

