package com.example.repository;

import org.springframework.stereotype.Repository;

import com.example.model.User;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository

public class InMemoryUserRepository implements UserRepository {

    private static InMemoryUserRepository instance;

    private final Map<String, User> userMap = new ConcurrentHashMap<>();



    @Override
    public void save(User user) {
        userMap.put(user.getUserId(), user);
    }

    @Override
    public User findById(String userId) {
        return userMap.get(userId);
    }

    @Override
    public boolean exists(String userId) {
        return userMap.containsKey(userId);
    }
}
