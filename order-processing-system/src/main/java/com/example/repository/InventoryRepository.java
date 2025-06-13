package com.example.repository;

import com.example.model.InventoryItem;

import java.util.Map;

public interface InventoryRepository {
    void save(InventoryItem item);
    InventoryItem findById(String itemId);
    Map<String, InventoryItem> findAll();
}
