package com.example.repository;

import com.example.model.Order;

import java.util.List;

public interface OrderRepository {
    void save(Order order);
    Order findById(String orderId);
    void delete(String orderId);
    List<Order> findAll();
    List<Order> getOrdersByStatus(String status);
    List<Order> getOrdersByUser(String userId); // ✅ NEW
}
