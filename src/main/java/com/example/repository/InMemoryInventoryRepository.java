package com.example.repository;

import com.example.model.InventoryItem;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryInventoryRepository implements InventoryRepository {

    private final Map<String, InventoryItem> inventoryMap = new ConcurrentHashMap<>();

    @Override
    public void save(InventoryItem item) {
        inventoryMap.put(item.getItemId(), item);
    }

    @Override
    public InventoryItem findById(String itemId) {
        return inventoryMap.get(itemId);
    }

    @Override
    public Map<String, InventoryItem> findAll() {
        return inventoryMap;
    }
}
