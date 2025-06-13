package com.example.controller;

import com.example.dto.OrderRequestDto;
import com.example.model.Order;
import com.example.response.ApiResponse;
import com.example.service.OrderFacadeServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderFacadeServiceImpl facade;

    @Autowired
    public OrderController(OrderFacadeServiceImpl facade) {
        this.facade = facade;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<String>> placeOrder(@RequestBody OrderRequestDto dto) {
        try {
            facade.placeOrder(dto.getOrder());
            return ResponseEntity.ok(ApiResponse.success("Order placed successfully", dto.getOrder().getOrderId()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.failure("Order placement failed: " + e.getMessage()));
        }
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<ApiResponse<String>> cancelOrder(@PathVariable String orderId) {
        try {
            facade.cancelOrder(orderId);
            return ResponseEntity.ok(ApiResponse.success("Order cancelled successfully", orderId));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.failure("Cancellation failed: " + e.getMessage()));
        }
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<Order>> getOrder(@PathVariable String orderId) {
        Order order = facade.getOrder(orderId);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ApiResponse.success("Order fetched", order));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<Order>>> getUserOrders(@PathVariable String userId) {
        List<Order> orders = facade.getUserOrders(userId);
        return ResponseEntity.ok(ApiResponse.success("User orders retrieved", orders));
    }
}
