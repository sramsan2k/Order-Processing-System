package com.example.controller;

import com.example.dto.UserDto;
import com.example.response.ApiResponse;
import com.example.service.OrderFacadeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final OrderFacadeService orderFacade;

    @Autowired
    public UserController(OrderFacadeService orderFacade) {
        this.orderFacade = orderFacade;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserDto>> registerUser(@RequestBody UserDto userDto) {
        try {
            orderFacade.registerUser(userDto);
            return ResponseEntity.ok(ApiResponse.success("User registered successfully", userDto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.failure(e.getMessage()));
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserDto>> getUserById(@PathVariable String userId) {
        UserDto user = orderFacade.getUserById(userId);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ApiResponse.success(userId, user));
    }
}
