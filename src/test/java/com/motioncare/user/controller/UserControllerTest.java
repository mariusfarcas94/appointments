package com.motioncare.user.controller;

import com.motioncare.user.dto.UserResponse;
import com.motioncare.user.dto.UserUpdateRequest;
import com.motioncare.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private UserResponse userResponse;
    private UserUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        userResponse = UserResponse.builder()
                .id(1L)
                .username("testuser")
                .firstName("Test")
                .lastName("User")
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .roles(Set.of("ADMIN"))
                .build();

        updateRequest = UserUpdateRequest.builder()
                .password("newPassword123")
                .firstName("Updated")
                .lastName("User")
                .enabled(true)
                .build();
    }

    @Test
    void getAllUsers_ShouldReturnAllUsers() {
        // Given
        UserResponse user2 = UserResponse.builder()
                .id(2L)
                .username("user2")
                .firstName("User")
                .lastName("Two")
                .enabled(true)
                .roles(Set.of("USER"))
                .build();

        when(userService.getAllUsers()).thenReturn(List.of(userResponse, user2));

        // When
        ResponseEntity<List<UserResponse>> response = userController.getAllUsers();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals("testuser", response.getBody().get(0).getUsername());
        assertEquals("Test", response.getBody().get(0).getFirstName());
        assertEquals("User", response.getBody().get(0).getLastName());
        assertTrue(response.getBody().get(0).getEnabled());
        assertEquals(1, response.getBody().get(0).getRoles().size());
        assertTrue(response.getBody().get(0).getRoles().contains("ADMIN"));
        assertEquals("user2", response.getBody().get(1).getUsername());
        assertEquals("User", response.getBody().get(1).getFirstName());
        assertEquals("Two", response.getBody().get(1).getLastName());
        assertTrue(response.getBody().get(1).getEnabled());
        assertEquals(1, response.getBody().get(1).getRoles().size());
        assertTrue(response.getBody().get(1).getRoles().contains("USER"));
    }

    @Test
    void getUserById_ShouldReturnUser_WhenUserExists() {
        // Given
        when(userService.getUserById(1L)).thenReturn(userResponse);

        // When
        ResponseEntity<UserResponse> response = userController.getUserById(1L);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals("testuser", response.getBody().getUsername());
        assertEquals("Test", response.getBody().getFirstName());
        assertEquals("User", response.getBody().getLastName());
        assertTrue(response.getBody().getEnabled());
        assertEquals(1, response.getBody().getRoles().size());
        assertTrue(response.getBody().getRoles().contains("ADMIN"));
    }

    @Test
    void getUserByUsername_ShouldReturnUser_WhenUserExists() {
        // Given
        when(userService.getUserByUsername("testuser")).thenReturn(userResponse);

        // When
        ResponseEntity<UserResponse> response = userController.getUserByUsername("testuser");

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals("testuser", response.getBody().getUsername());
        assertEquals("Test", response.getBody().getFirstName());
        assertEquals("User", response.getBody().getLastName());
        assertTrue(response.getBody().getEnabled());
        assertEquals(1, response.getBody().getRoles().size());
        assertTrue(response.getBody().getRoles().contains("ADMIN"));
    }

    @Test
    void updateUser_ShouldReturnUpdatedUser_WhenValidRequest() {
        // Given
        UserResponse updatedResponse = UserResponse.builder()
                .id(1L)
                .username("testuser")
                .firstName("Updated")
                .lastName("User")
                .enabled(true)
                .roles(Set.of("ADMIN"))
                .build();

        when(userService.updateUser(anyLong(), any(UserUpdateRequest.class))).thenReturn(updatedResponse);

        // When
        ResponseEntity<UserResponse> response = userController.updateUser(1L, updateRequest);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals("testuser", response.getBody().getUsername());
        assertEquals("Updated", response.getBody().getFirstName());
        assertEquals("User", response.getBody().getLastName());
        assertTrue(response.getBody().getEnabled());
        assertEquals(1, response.getBody().getRoles().size());
        assertTrue(response.getBody().getRoles().contains("ADMIN"));
    }

    @Test
    void deleteUser_ShouldReturnNoContent_WhenUserExists() {
        // When
        ResponseEntity<Void> response = userController.deleteUser(1L);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void getAllUsers_ShouldReturnEmptyList_WhenNoUsers() {
        // Given
        when(userService.getAllUsers()).thenReturn(List.of());

        // When
        ResponseEntity<List<UserResponse>> response = userController.getAllUsers();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }
}
