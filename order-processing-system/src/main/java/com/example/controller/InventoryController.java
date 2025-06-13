package com.example.controller;

import com.example.dto.InventoryItemDto;
import com.example.response.ApiResponse;
import com.example.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    @Autowired
    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<InventoryItemDto>> addItem(@RequestBody InventoryItemDto item) {
        try {
            inventoryService.addInventoryItem(item);
            return ResponseEntity.ok(ApiResponse.success("Item added successfully", item));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.failure("Failed to add item: " + e.getMessage()));
        }
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ApiResponse<InventoryItemDto>> getItem(@PathVariable String itemId) {
        InventoryItemDto item = inventoryService.getItem(itemId);
        if (item == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ApiResponse.success("Item retrieved successfully", item));
    }
}
