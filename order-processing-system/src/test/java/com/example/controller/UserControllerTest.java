package com.example.controller;

import com.example.dto.UserDto;
import com.example.response.ApiResponse;
import com.example.service.OrderFacadeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderFacadeService orderFacadeService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDto sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = UserDto.builder()
                .userId("user1")
                .name("Test User")
                .email("test@example.com")
                .build();
    }

    @Test
    void testRegisterUserSuccess() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully"))
                .andExpect(jsonPath("$.data.name").value("Test User"));
    }

    @Test
    void testRegisterUserValidationFailure() throws Exception {
        doThrow(new IllegalArgumentException("Email already exists")).when(orderFacadeService).registerUser(any());

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Email already exists"));
    }

    @Test
    void testGetUserByIdSuccess() throws Exception {
        when(orderFacadeService.getUserById("user1")).thenReturn(sampleUser);

        mockMvc.perform(get("/api/users/user1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("user1"))
                .andExpect(jsonPath("$.data.email").value("test@example.com"));
    }

    @Test
    void testGetUserByIdNotFound() throws Exception {
        when(orderFacadeService.getUserById("unknown")).thenReturn(null);

        mockMvc.perform(get("/api/users/unknown"))
                .andExpect(status().isNotFound());
    }
}
