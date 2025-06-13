package com.example.controller;

import com.example.response.ApiResponse;
import com.example.service.ShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shipping")
public class ShippingController {

    private final ShippingService shippingService;

    @Autowired
    public ShippingController(ShippingService shippingService) {
        this.shippingService = shippingService;
    }

    @PutMapping("/ship/{orderId}")
    public ResponseEntity<ApiResponse<String>> shipOrder(@PathVariable String orderId) {
        try {
            shippingService.shipOrder(orderId);
            return ResponseEntity.ok(ApiResponse.success("Order shipped successfully", orderId));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.failure("Shipping failed: " + e.getMessage()));
        }
    }

}
