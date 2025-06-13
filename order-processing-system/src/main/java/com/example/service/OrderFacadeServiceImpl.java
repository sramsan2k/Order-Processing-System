package com.example.service;

import com.example.dto.OrderDto;
import com.example.dto.UserDto;
import com.example.model.Order;
import com.example.model.OrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderFacadeServiceImpl implements OrderFacadeService {

    private final OrderService orderService;
    private final InventoryService inventoryService;
    private final UserService userService;

    @Autowired
    public OrderFacadeServiceImpl(OrderService orderService,
                                  InventoryService inventoryService,
                                  UserService userService) {
        this.orderService = orderService;
        this.inventoryService = inventoryService;
        this.userService = userService;
    }

    @Override
    public void placeOrder(OrderDto orderDto) {
        if (!userService.userExists(orderDto.getUserId())) {
            throw new IllegalArgumentException("User does not exist");
        }

        if (!inventoryService.reserveStock(orderDto.getItems())) {
            throw new IllegalStateException("Stock unavailable");
        }

        String uniqueOrderId = UUID.randomUUID().toString();

        Order order = new Order.Builder()
                .orderId(uniqueOrderId)
                .userId(orderDto.getUserId())
                .items(orderDto.getItems().stream()
                        .map(dto -> new OrderItem(dto.getItemId(), dto.getQuantity()))
                        .collect(Collectors.toList()))
                .status("PENDING")
                .build();

        orderService.createOrder(order);
    }

    @Override
    public void cancelOrder(String orderId) {
        orderService.cancelOrder(orderId);
    }

    @Override
    public Order getOrder(String orderId) {
        return orderService.getOrder(orderId);
    }

    @Override
    public List<Order> getUserOrders(String userId) {
        return orderService.getOrdersByUser(userId);
    }

    @Override
    public void registerUser(UserDto userDto) {
        userService.registerUser(userDto);
    }

    @Override
    public UserDto getUserById(String userId) {
        return userService.getUserById(userId);
    }
}
