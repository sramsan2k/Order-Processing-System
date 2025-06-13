package com.example.service;

import com.example.dto.InventoryItemDto;
import com.example.dto.OrderItemDto;
import com.example.model.InventoryItem;
import com.example.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;

    @Autowired
    public InventoryServiceImpl(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    public void addInventoryItem(InventoryItemDto itemDto) {
        InventoryItem existingItem = inventoryRepository.findById(itemDto.getItemId());

        if (existingItem != null) {
            // Accumulate stock if item already exists
            int updatedQty = existingItem.getAvailableQuantity() + itemDto.getAvailableQty();
            existingItem.setAvailableQuantity(updatedQty);
            inventoryRepository.save(existingItem);
        } else {
            // Add new inventory item
            InventoryItem inventoryItem = new InventoryItem.Builder()
                    .withItemId(itemDto.getItemId())
                    .withItemName(itemDto.getItemName())
                    .withAvailableQuantity(itemDto.getAvailableQty())
                    .build();
            inventoryRepository.save(inventoryItem);
        }
    }

    @Override
    public InventoryItemDto getItem(String itemId) {
        InventoryItem item = inventoryRepository.findById(itemId);
        if (item == null) return null;

        return InventoryItemDto.builder()
                .itemId(item.getItemId())
                .itemName(item.getItemName())
                .availableQty(item.getAvailableQuantity())
                .build();
    }

    @Override
    public boolean reserveStock(List<OrderItemDto> orderItems) {
        // First, check if all items have sufficient stock
        for (OrderItemDto orderItem : orderItems) {
            InventoryItem item = inventoryRepository.findById(orderItem.getItemId());
            if (item == null || item.getAvailableQuantity() < orderItem.getQuantity()) {
                return false; // insufficient stock
            }
        }

        // Then, reserve stock by reducing availability
        for (OrderItemDto orderItem : orderItems) {
            InventoryItem item = inventoryRepository.findById(orderItem.getItemId());
            item.setAvailableQuantity(item.getAvailableQuantity() - orderItem.getQuantity());
            inventoryRepository.save(item);
        }

        return true;
    }
}
