// File: src/test/java/com/example/controller/InventoryControllerTest.java

package com.example.controller;

import com.example.dto.InventoryItemDto;
import com.example.service.InventoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InventoryController.class)
class InventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InventoryService inventoryService;

    @Autowired
    private ObjectMapper objectMapper;

    private InventoryItemDto sampleItem;

    @BeforeEach
    void setUp() {
        sampleItem = InventoryItemDto.builder()
                .itemId("item123")
                .itemName("Laptop")
                .availableQty(10)
                .build();
    }

    @Test
    void testAddItemSuccess() throws Exception {
        mockMvc.perform(post("/api/inventory")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleItem)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Item added successfully"))
                .andExpect(jsonPath("$.data.itemId").value("item123"));
    }

    @Test
    void testAddItemFailure() throws Exception {
        doThrow(new RuntimeException("Invalid data")).when(inventoryService).addInventoryItem(any());

        mockMvc.perform(post("/api/inventory")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleItem)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Failed to add item: Invalid data"));
    }

    @Test
    void testGetItemSuccess() throws Exception {
        when(inventoryService.getItem("item123")).thenReturn(sampleItem);

        mockMvc.perform(get("/api/inventory/item123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Item retrieved successfully"))
                .andExpect(jsonPath("$.data.itemName").value("Laptop")); // ✅ Corrected key
    }

    @Test
    void testGetItemNotFound() throws Exception {
        when(inventoryService.getItem("unknown")).thenReturn(null);

        mockMvc.perform(get("/api/inventory/unknown"))
                .andExpect(status().isNotFound());
    }
}
