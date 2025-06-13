package com.example.service;

import com.example.dto.OrderDto;
import com.example.model.Order;

import java.util.List;

public interface OrderService {
    void createOrder(Order order);
    Order getOrder(String orderId);
    List<Order> getOrdersByUser(String userId);
    void cancelOrder(String orderId);
    void updateOrderStatus(String orderId, String status);
    List<Order> getOrdersByStatus(String status);
}
