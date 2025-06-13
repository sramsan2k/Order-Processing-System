package com.example.service;

import com.example.dto.UserDto;
import com.example.model.User;
import com.example.repository.InMemoryUserRepository;
import com.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void registerUser(UserDto userDto) {
        if (userRepository.exists(userDto.getUserId())) {
            throw new IllegalArgumentException("User already exists with ID: " + userDto.getUserId());
        }

        User user = new User.UserBuilder()
                .withUserId(userDto.getUserId())
                .withName(userDto.getName())
                .withEmail(userDto.getEmail())
                .build();

        userRepository.save(user);
        System.out.println("end");

    }

    @Override
    public UserDto getUserById(String userId) {
        User user = userRepository.findById(userId);
        if (user == null) return null;

        return UserDto.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    @Override
    public boolean userExists(String userId) {
        return userRepository.exists(userId);
    }
}
