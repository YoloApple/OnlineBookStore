package com.example.bookstore.service;

import com.example.bookstore.dto.CartItemRequest;
import com.example.bookstore.dto.CartItemResponse;
import com.example.bookstore.entity.Cart;
import com.example.bookstore.entity.CartItem;
import com.example.bookstore.repository.BookRepository;
import com.example.bookstore.repository.CartRepository;
import com.example.bookstore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartService {
    @Autowired private CartRepository cartRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private BookRepository bookRepository;

    public void addToCart(String username, CartItemRequest request){
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        var book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new RuntimeException("Sách không tồn tại"));
        Cart cart = user.getCart();
        if (cart == null) {
            cart = new Cart();
            cart.setUser(user);
            user.setCart(cart);
        }
        var existing = cart.getItems().stream()
                .filter(item -> item.getBook().getId().equals(book.getId()))
                .findFirst();

        if (existing.isPresent()) {
            CartItem item = existing.get();
            item.setQuantity(item.getQuantity() + request.getQuantity());
        } else {
            CartItem newItem = new CartItem();
            newItem.setBook(book);
            newItem.setQuantity(request.getQuantity());
            newItem.setCart(cart);
            cart.getItems().add(newItem);
        }

        cartRepository.save(cart); // cascade sẽ tự lưu CartItem
    }

    public List<CartItemResponse> getCart(String username) {
        var user = userRepository.findByUsername(username).orElseThrow();
        Cart cart = user.getCart();
        List<CartItem> items = cart.getItems();

        return items.stream().map(item -> {
            CartItemResponse dto = new CartItemResponse();
            dto.setId(item.getId()); //  Set ID
            dto.setBookId(item.getBook().getId()); // Set Book ID
            dto.setBookTitle(item.getBook().getTitle());
            dto.setQuantity(item.getQuantity());
            dto.setTotalPrice(item.getQuantity() * item.getBook().getPrice());
            return dto;
        }).collect(Collectors.toList());
    }

    public void removeItemFromCart(String username, Long bookId) {
        var user = userRepository.findByUsername(username).orElseThrow();
        Cart cart = user.getCart();

        cart.getItems().removeIf(item -> item.getBook().getId().equals(bookId));
        cartRepository.save(cart);
    }

    public void clearCart(String username) {
        var user = userRepository.findByUsername(username).orElseThrow();
        Cart cart = user.getCart();
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    public CartItemResponse updateCartItem(String username, CartItemRequest request) {
        var user = userRepository.findByUsername(username).orElseThrow();
        Cart cart = user.getCart();

        var item = cart.getItems().stream()
                .filter(i -> i.getBook().getId().equals(request.getBookId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sách trong giỏ hàng"));

        item.setQuantity(request.getQuantity());

        cartRepository.save(cart);

        CartItemResponse response = new CartItemResponse();
        response.setId(item.getId());
        response.setBookId(item.getBook().getId());
        response.setBookTitle(item.getBook().getTitle());
        response.setQuantity(item.getQuantity());
        response.setTotalPrice(item.getBook().getPrice() * item.getQuantity());

        return response;
    }
}

