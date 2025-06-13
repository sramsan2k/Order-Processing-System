package com.example.service;

import com.example.dto.UserDto;
import com.example.model.User;
import com.example.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    private UserRepository userRepository;
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    void testRegisterUser_Success() {
        UserDto dto = UserDto.builder()
                .userId("u1")
                .name("Test")
                .email("test@example.com")
                .build();

        when(userRepository.exists("u1")).thenReturn(false);

        userService.registerUser(dto);

        verify(userRepository).save(argThat(user ->
                user.getUserId().equals("u1") &&
                        user.getName().equals("Test") &&
                        user.getEmail().equals("test@example.com")
        ));
    }

    @Test
    void testRegisterUser_AlreadyExists() {
        UserDto dto = UserDto.builder()
                .userId("u1")
                .name("Test")
                .email("test@example.com")
                .build();

        when(userRepository.exists("u1")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.registerUser(dto));

        assertEquals("User already exists with ID: u1", ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void testGetUserById_Found() {
        User user = new User.UserBuilder()
                .withUserId("u1")
                .withName("Alice")
                .withEmail("alice@example.com")
                .build();

        when(userRepository.findById("u1")).thenReturn(user);

        UserDto result = userService.getUserById("u1");

        assertNotNull(result);
        assertEquals("Alice", result.getName());
        assertEquals("alice@example.com", result.getEmail());
    }

    @Test
    void testGetUserById_NotFound() {
        when(userRepository.findById("u404")).thenReturn(null);

        UserDto result = userService.getUserById("u404");

        assertNull(result);
    }

    @Test
    void testUserExists() {
        when(userRepository.exists("u1")).thenReturn(true);

        assertTrue(userService.userExists("u1"));
    }

    @Test
    void testUserDoesNotExist() {
        when(userRepository.exists("u2")).thenReturn(false);

        assertFalse(userService.userExists("u2"));
    }
}
