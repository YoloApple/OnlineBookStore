package com.example.bookstore.service;

import com.example.bookstore.dto.*;
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
    // service/OrderService.java
    public List<OrderSellerDto> getOrdersForSeller(String sellerUsername) {
        List<Order> orders = orderRepository.findOrdersBySellerUsername(sellerUsername);
        return orders.stream().map(order -> {
            OrderSellerDto dto = new OrderSellerDto();
            dto.setOrderId(order.getId());
            dto.setOrderDate(order.getOrderDate());
            dto.setTotalPrice(order.getTotalPrice());
            dto.setBuyerUsername(order.getUser().getUsername());

            List<OrderItemSellerDto> itemDtos = order.getItems().stream()
                    .filter(item -> item.getBook().getSeller().getUsername().equals(sellerUsername))
                    .map(item -> {
                        OrderItemSellerDto itemDto = new OrderItemSellerDto();
                        itemDto.setBookTitle(item.getBook().getTitle());
                        itemDto.setQuantity(item.getQuantity());
                        itemDto.setPriceAtPurchase(item.getPriceAtPurchase());
                        return itemDto;
                    })
                    .toList();

            dto.setItems(itemDtos);
            return dto;
        }).toList();
    }
    public void updateOrderStatus(String sellerUsername, UpdateOrderStatusRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        // Kiểm tra xem đơn hàng có chứa sách của seller hay không
        boolean hasBookBySeller = order.getItems().stream()
                .anyMatch(item -> item.getBook().getSeller().getUsername().equals(sellerUsername));

        if (!hasBookBySeller) {
            throw new RuntimeException("Bạn không có quyền cập nhật đơn hàng này");
        }

        order.setStatus(request.getNewStatus());
        orderRepository.save(order);
    }

    public StatisticResponse getStatistics(String username, boolean isAdmin, StatisticRequest request) {
        List<Order> orders;

        if (isAdmin) {
            orders = orderRepository.findAll();
        } else {
            orders = orderRepository.findOrdersBySellerUsername(username);
        }

        // Lọc theo thời gian
        if (request.getStartDate() != null) {
            orders = orders.stream()
                    .filter(o -> !o.getOrderDate().toLocalDate().isBefore(request.getStartDate()))
                    .collect(Collectors.toList());
        }

        if (request.getEndDate() != null) {
            orders = orders.stream()
                    .filter(o -> !o.getOrderDate().toLocalDate().isAfter(request.getEndDate()))
                    .collect(Collectors.toList());
        }

        // Lọc theo trạng thái nếu có
        if (request.getStatus() != null) {
            orders = orders.stream()
                    .filter(o -> o.getStatus() == request.getStatus())
                    .collect(Collectors.toList());
        }

        long totalOrders = orders.size();
        double totalRevenue = orders.stream()
                .mapToDouble(Order::getTotalPrice)
                .sum();
        long totalBooksSold = orders.stream()
                .flatMap(o -> o.getItems().stream())
                .mapToLong(item -> item.getQuantity())
                .sum();

        return new StatisticResponse(totalOrders, totalRevenue, totalBooksSold);
    }
}
