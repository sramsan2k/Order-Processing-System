package com.example.repository;

import com.example.model.Order;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class InMemoryOrderRepository implements OrderRepository {

    private final Map<String, Order> orderMap = new ConcurrentHashMap<>();

    @Override
    public void save(Order order) {
        orderMap.put(order.getOrderId(), order);
    }

    @Override
    public Order findById(String orderId) {
        return orderMap.get(orderId);
    }

    @Override
    public void delete(String orderId) {
        orderMap.remove(orderId);
    }

    @Override
    public List<Order> findAll() {
        return new ArrayList<>(orderMap.values());
    }

    @Override
    public List<Order> getOrdersByStatus(String status) {
        return orderMap.values().stream()
                .filter(order -> order.getStatus().equalsIgnoreCase(status))
                .collect(Collectors.toList());
    }

    @Override
    public List<Order> getOrdersByUser(String userId) {
        return orderMap.values().stream()
                .filter(order -> userId.equals(order.getUserId()))
                .collect(Collectors.toList());
    }
}
