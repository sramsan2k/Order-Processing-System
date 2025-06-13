package com.example.controller;

import com.example.dto.OrderDto;
import com.example.dto.OrderItemDto;
import com.example.dto.OrderRequestDto;
import com.example.model.Order;
import com.example.service.OrderFacadeServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderFacadeServiceImpl facade;

    @Autowired
    private ObjectMapper objectMapper;

    private OrderDto sampleOrderDto;

    @BeforeEach
    void setUp() {
        OrderItemDto itemDto = OrderItemDto.builder()
                .itemId("item1")
                .quantity(2)
                .build();

        sampleOrderDto = OrderDto.builder()
                .orderId("order1")
                .userId("user1")
                .status("PENDING")
                .items(List.of(itemDto))
                .build();
    }

    @Test
    void testPlaceOrderSuccess() throws Exception {

        OrderRequestDto dto = OrderRequestDto.builder().order(sampleOrderDto).build();

        doNothing().when(facade).placeOrder(any(OrderDto.class));

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Order placed successfully"))
                .andExpect(jsonPath("$.data").value("order1"));
    }

    @Test
    void testPlaceOrderFailure() throws Exception {
        Order orderEntity = mock(Order.class);
        when(orderEntity.getOrderId()).thenReturn("order1");

        OrderRequestDto dto = OrderRequestDto.builder().order(sampleOrderDto).build();

        doThrow(new RuntimeException("Inventory error")).when(facade).placeOrder(any(OrderDto.class));

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Order placement failed: Inventory error"));
    }

    @Test
    void testCancelOrderSuccess() throws Exception {
        doNothing().when(facade).cancelOrder("order1");

        mockMvc.perform(post("/api/orders/order1/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Order cancelled successfully"))
                .andExpect(jsonPath("$.data").value("order1"));
    }

    @Test
    void testCancelOrderFailure() throws Exception {
        doThrow(new RuntimeException("Order already shipped")).when(facade).cancelOrder("order1");

        mockMvc.perform(post("/api/orders/order1/cancel"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Cancellation failed: Order already shipped"));
    }

    @Test
    void testGetOrderSuccess() throws Exception {
        Order order = new Order.Builder()
                .orderId("order1")
                .userId("user1")
                .status("PENDING")
                .items(List.of())
                .build();

        when(facade.getOrder("order1")).thenReturn(order);

        mockMvc.perform(get("/api/orders/order1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Order fetched"))
                .andExpect(jsonPath("$.data.status").value("PENDING"));
    }

    @Test
    void testGetOrderNotFound() throws Exception {
        when(facade.getOrder("invalid")).thenReturn(null);

        mockMvc.perform(get("/api/orders/invalid"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetUserOrders() throws Exception {
        Order order = new Order.Builder()
                .orderId("order1")
                .userId("user1")
                .status("PENDING")
                .items(List.of())
                .build();

        when(facade.getUserOrders("user1")).thenReturn(List.of(order));

        mockMvc.perform(get("/api/orders/user/user1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User orders retrieved"))
                .andExpect(jsonPath("$.data[0].orderId").value("order1"));
    }

    @Test
    void testGetUserOrdersEmptyList() throws Exception {
        when(facade.getUserOrders("user1")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/orders/user/user1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User orders retrieved"))
                .andExpect(jsonPath("$.data").isArray());
    }
}
