package com.example.repository;

import com.example.model.InventoryItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryInventoryRepositoryTest {

    private InMemoryInventoryRepository inventoryRepository;

    private InventoryItem item1;
    private InventoryItem item2;

    @BeforeEach
    void setUp() {
        inventoryRepository = new InMemoryInventoryRepository();

        item1 = new InventoryItem.Builder()
                .withItemId("item1")
                .withItemName("Laptop")
                .withAvailableQuantity(10)
                .build();

        item2 = new InventoryItem.Builder()
                .withItemId("item2")
                .withItemName("Mouse")
                .withAvailableQuantity(25)
                .build();
    }

    @Test
    void testSaveAndFindById() {
        inventoryRepository.save(item1);
        InventoryItem found = inventoryRepository.findById("item1");

        assertNotNull(found);
        assertEquals("Laptop", found.getItemName());
        assertEquals(10, found.getAvailableQuantity());
    }

    @Test
    void testFindAll() {
        inventoryRepository.save(item1);
        inventoryRepository.save(item2);

        Map<String, InventoryItem> allItems = inventoryRepository.findAll();

        assertEquals(2, allItems.size());
        assertTrue(allItems.containsKey("item1"));
        assertTrue(allItems.containsKey("item2"));
    }

    @Test
    void testFindByIdNotFound() {
        assertNull(inventoryRepository.findById("unknown"));
    }
}
