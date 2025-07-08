package com.example.bookstore.service;

import com.example.bookstore.dto.OrderItemResponse;
import com.example.bookstore.dto.OrderResponse;
import com.example.bookstore.entity.Cart;
import com.example.bookstore.entity.Order;
import com.example.bookstore.entity.OrderItem;
import com.example.bookstore.entity.User;
import com.example.bookstore.repository.CartRepository;
import com.example.bookstore.repository.OrderRepository;
import com.example.bookstore.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {
   @Autowired private UserRepository userRepository;
   @Autowired private OrderRepository orderRepository;
   @Autowired private CartRepository cartRepository;

   @Transactional
    public void placeOrder(String username){
       User user = userRepository.findByUsername(username).orElseThrow();
       Cart cart = user.getCart();

       if(cart.getItems()==null || cart.getItems().isEmpty()){
           throw  new RuntimeException("Giỏ hàng trống");
       }

       Order order = new Order();
       order.setUser(user);
       order.setOrderDate(LocalDateTime.now());


       List<OrderItem> orderItems = cart.getItems().stream().map(cartItem -> {
           OrderItem item = new OrderItem();
           item.setOrder(order);
           item.setBook(cartItem.getBook());
           item.setQuantity(cartItem.getQuantity());
           item.setPriceAtPurchase(cartItem.getBook().getPrice());
           return item;
       }).collect(Collectors.toList());

       order.setItems(orderItems);
       order.setTotalPrice(orderItems.stream().mapToDouble(i -> i.getPriceAtPurchase() * i.getQuantity()).sum());
       orderRepository.save(order);


       cart.getItems().clear();
       cartRepository.save(cart);
   }
    public List<OrderResponse> getUserOrders(String username) {
        User user = userRepository.findByUsername(username).orElseThrow();
        List<Order> orders = orderRepository.findByUser(user);

        return orders.stream().map(order -> {
            OrderResponse res = new OrderResponse();
            res.setId(order.getId());
            res.setOrderDate(order.getOrderDate());
            res.setTotalPrice(order.getTotalPrice());

            List<OrderItemResponse> itemResponses = order.getItems().stream().map(item -> {
                OrderItemResponse i = new OrderItemResponse();
                i.setBookTitle(item.getBook().getTitle());
                i.setQuantity(item.getQuantity());
                i.setPriceAtPurchase(item.getPriceAtPurchase());
                return i;
            }).collect(Collectors.toList());

            res.setItems(itemResponses);
            return res;
        }).collect(Collectors.toList());
    }
}
