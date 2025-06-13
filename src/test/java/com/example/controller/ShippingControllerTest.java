package com.example.controller;

import com.example.response.ApiResponse;
import com.example.service.ShippingService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ShippingController.class)
class ShippingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ShippingService shippingService;

    @Test
    void testShipOrderSuccess() throws Exception {
        doNothing().when(shippingService).shipOrder("order123");

        mockMvc.perform(put("/api/shipping/ship/order123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Order shipped successfully"))
                .andExpect(jsonPath("$.data").value("order123"));
    }

    @Test
    void testShipOrderFailure() throws Exception {
        doThrow(new RuntimeException("Item not found")).when(shippingService).shipOrder("invalid123");

        mockMvc.perform(put("/api/shipping/ship/invalid123"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Shipping failed: Item not found"));
    }
}
