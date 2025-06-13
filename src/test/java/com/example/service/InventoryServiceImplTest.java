package com.example.service;

import com.example.dto.InventoryItemDto;
import com.example.dto.OrderItemDto;
import com.example.model.InventoryItem;
import com.example.repository.InventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InventoryServiceImplTest {

    private InventoryRepository inventoryRepository;
    private InventoryServiceImpl inventoryService;

    @BeforeEach
    void setUp() {
        inventoryRepository = mock(InventoryRepository.class);
        inventoryService = new InventoryServiceImpl(inventoryRepository);
    }

    @Test
    void testAddInventoryItem_NewItem() {
        InventoryItemDto itemDto = InventoryItemDto.builder()
                .itemId("item1")
                .itemName("Mouse")
                .availableQty(10)
                .build();

        when(inventoryRepository.findById("item1")).thenReturn(null);

        inventoryService.addInventoryItem(itemDto);

        verify(inventoryRepository).save(argThat(item ->
                item.getItemId().equals("item1") &&
                        item.getItemName().equals("Mouse") &&
                        item.getAvailableQuantity() == 10
        ));
    }

    @Test
    void testAddInventoryItem_ExistingItemAccumulates() {
        InventoryItem existing = new InventoryItem.Builder()
                .withItemId("item1")
                .withItemName("Mouse")
                .withAvailableQuantity(5)
                .build();

        InventoryItemDto itemDto = InventoryItemDto.builder()
                .itemId("item1")
                .itemName("Mouse")
                .availableQty(7)
                .build();

        when(inventoryRepository.findById("item1")).thenReturn(existing);

        inventoryService.addInventoryItem(itemDto);

        verify(inventoryRepository).save(argThat(item ->
                item.getAvailableQuantity() == 12
        ));
    }

    @Test
    void testGetItemFound() {
        InventoryItem item = new InventoryItem.Builder()
                .withItemId("item1")
                .withItemName("Laptop")
                .withAvailableQuantity(10)
                .build();

        when(inventoryRepository.findById("item1")).thenReturn(item);

        InventoryItemDto result = inventoryService.getItem("item1");

        assertNotNull(result);
        assertEquals("Laptop", result.getItemName());
        assertEquals(10, result.getAvailableQty());
    }

    @Test
    void testGetItemNotFound() {
        when(inventoryRepository.findById("invalid")).thenReturn(null);

        assertNull(inventoryService.getItem("invalid"));
    }

    @Test
    void testReserveStockSuccess() {
        InventoryItem item = new InventoryItem.Builder()
                .withItemId("item1")
                .withItemName("USB")
                .withAvailableQuantity(10)
                .build();

        OrderItemDto orderItem = OrderItemDto.builder()
                .itemId("item1")
                .quantity(3)
                .build();

        when(inventoryRepository.findById("item1")).thenReturn(item);

        boolean result = inventoryService.reserveStock(List.of(orderItem));

        assertTrue(result);
        verify(inventoryRepository).save(argThat(i -> i.getAvailableQuantity() == 7));
    }

    @Test
    void testReserveStockFails_InsufficientQty() {
        InventoryItem item = new InventoryItem.Builder()
                .withItemId("item1")
                .withItemName("Keyboard")
                .withAvailableQuantity(2)
                .build();

        OrderItemDto orderItem = OrderItemDto.builder()
                .itemId("item1")
                .quantity(5)
                .build();

        when(inventoryRepository.findById("item1")).thenReturn(item);

        boolean result = inventoryService.reserveStock(List.of(orderItem));

        assertFalse(result);
        verify(inventoryRepository, never()).save(any());
    }

    @Test
    void testReserveStockFails_ItemNotFound() {
        OrderItemDto orderItem = OrderItemDto.builder()
                .itemId("itemX")
                .quantity(1)
                .build();

        when(inventoryRepository.findById("itemX")).thenReturn(null);

        boolean result = inventoryService.reserveStock(List.of(orderItem));

        assertFalse(result);
        verify(inventoryRepository, never()).save(any());
    }
}
