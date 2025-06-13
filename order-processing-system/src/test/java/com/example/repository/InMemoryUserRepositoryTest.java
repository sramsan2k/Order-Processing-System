package com.example.repository;

import com.example.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryUserRepositoryTest {

    private InMemoryUserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = new InMemoryUserRepository();
    }

    @Test
    void testSaveAndFindById() {
        User user = new User.UserBuilder()
                .withUserId("u1")
                .withName("Test User")
                .withEmail("test@example.com")
                .build();

        userRepository.save(user);

        User fetched = userRepository.findById("u1");
        assertNotNull(fetched);
        assertEquals("Test User", fetched.getName());
        assertEquals("test@example.com", fetched.getEmail());
    }

    @Test
    void testExists() {
        User user = new User.UserBuilder()
                .withUserId("u2")
                .withName("Jane")
                .withEmail("Jane@email.com")
                .build();


        assertFalse(userRepository.exists("u2"));

        userRepository.save(user);

        assertTrue(userRepository.exists("u2"));
    }

    @Test
    void testFindByIdNotFound() {
        assertNull(userRepository.findById("nonexistent"));
    }
}
