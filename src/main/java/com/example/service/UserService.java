package com.example.service;

import com.example.dto.UserDto;

public interface UserService {
    void registerUser(UserDto userDto);
    UserDto getUserById(String userId);
    boolean userExists(String userId);
}
