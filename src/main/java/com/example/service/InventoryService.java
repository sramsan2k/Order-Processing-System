package com.example.service;

import com.example.dto.*;

import java.util.List;

public interface InventoryService {
    void addInventoryItem(InventoryItemDto item);
    InventoryItemDto getItem(String itemId);
    boolean reserveStock(List<OrderItemDto> orderItems);
}
