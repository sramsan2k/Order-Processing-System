package com.example.service;

import com.example.dto.OrderDto;
import com.example.dto.UserDto;
import com.example.model.Order;

import java.util.List;

public interface OrderFacadeService {
    void placeOrder(OrderDto orderDto);
    void cancelOrder(String orderId);
    Order getOrder(String orderId);
    List<Order> getUserOrders(String userId);

    void registerUser(UserDto userDto);                // NEW
    UserDto getUserById(String userId);                // NEW
}
