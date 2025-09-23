package com.motioncare.user.controller;

import com.motioncare.user.dto.AuthResponse;
import com.motioncare.user.dto.UserLoginRequest;
import com.motioncare.user.dto.UserRegistrationRequest;
import com.motioncare.user.dto.UserResponse;
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
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthController authController;

    private UserRegistrationRequest registrationRequest;
    private UserLoginRequest loginRequest;
    private AuthResponse authResponse;

    @BeforeEach
    void setUp() {
        UserResponse userResponse = UserResponse.builder()
                .id(1L)
                .username("testuser")
                .firstName("Test")
                .lastName("User")
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .roles(Set.of("ADMIN"))
                .build();

        authResponse = AuthResponse.builder()
                .token("jwtToken")
                .user(userResponse)
                .build();

        registrationRequest = UserRegistrationRequest.builder()
                .username("testuser")
                .password("password123")
                .firstName("Test")
                .lastName("User")
                .build();

        loginRequest = UserLoginRequest.builder()
                .username("testuser")
                .password("password123")
                .build();
    }

    @Test
    void register_ShouldReturnCreated_WhenValidRequest() {
        // Given
        when(userService.register(any(UserRegistrationRequest.class))).thenReturn(authResponse);

        // When
        ResponseEntity<AuthResponse> response = authController.register(registrationRequest);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("jwtToken", response.getBody().getToken());
        assertEquals("testuser", response.getBody().getUser().getUsername());
        assertEquals("Test", response.getBody().getUser().getFirstName());
        assertEquals("User", response.getBody().getUser().getLastName());
        assertTrue(response.getBody().getUser().getEnabled());
        assertEquals(1, response.getBody().getUser().getRoles().size());
        assertTrue(response.getBody().getUser().getRoles().contains("ADMIN"));
    }

    @Test
    void login_ShouldReturnOk_WhenValidCredentials() {
        // Given
        when(userService.login(any(UserLoginRequest.class))).thenReturn(authResponse);

        // When
        ResponseEntity<AuthResponse> response = authController.login(loginRequest);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("jwtToken", response.getBody().getToken());
        assertEquals("testuser", response.getBody().getUser().getUsername());
        assertEquals("Test", response.getBody().getUser().getFirstName());
        assertEquals("User", response.getBody().getUser().getLastName());
        assertTrue(response.getBody().getUser().getEnabled());
        assertEquals(1, response.getBody().getUser().getRoles().size());
        assertTrue(response.getBody().getUser().getRoles().contains("ADMIN"));
    }
}
