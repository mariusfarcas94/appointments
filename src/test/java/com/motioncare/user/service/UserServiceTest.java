package com.motioncare.user.service;

import com.motioncare.user.dto.*;
import com.motioncare.user.model.Role;
import com.motioncare.user.model.User;
import com.motioncare.user.repository.UserRepository;
import com.motioncare.user.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private Role adminRole;
    private UserRegistrationRequest registrationRequest;
    private UserLoginRequest loginRequest;
    private UserUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        adminRole = Role.builder()
                .id(1L)
                .name("ADMIN")
                .description("Administrator role")
                .build();

        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .password("encodedPassword")
                .firstName("Test")
                .lastName("User")
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .roles(Set.of(adminRole))
                .build();

        registrationRequest = UserRegistrationRequest.builder()
                .username("newuser")
                .password("password123")
                .firstName("New")
                .lastName("User")
                .build();

        loginRequest = UserLoginRequest.builder()
                .username("testuser")
                .password("password123")
                .build();

        updateRequest = UserUpdateRequest.builder()
                .password("newPassword123")
                .firstName("Updated")
                .lastName("User")
                .enabled(true)
                .build();
    }

    @Test
    void register_ShouldRegisterNewUser_WhenValidRequest() {
        // Given
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L); // Simulate the generated key
            return 1;
        });
        when(userRepository.findRolesByUserId(1L)).thenReturn(java.util.List.of(adminRole));
        when(jwtUtil.generateToken(any(User.class))).thenReturn("jwtToken");

        // When
        AuthResponse response = userService.register(registrationRequest);

        // Then
        assertNotNull(response);
        assertEquals("jwtToken", response.getToken());
        assertNotNull(response.getUser());
        assertEquals("newuser", response.getUser().getUsername());

        verify(userRepository).existsByUsername("newuser");
        verify(userRepository).save(any(User.class));
        verify(userRepository).assignRole(1L, 1L);
        verify(jwtUtil).generateToken(any(User.class));
    }

    @Test
    void register_ShouldThrowException_WhenUsernameExists() {
        // Given
        when(userRepository.existsByUsername("newuser")).thenReturn(true);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> userService.register(registrationRequest));
        
        assertEquals("Username is already taken!", exception.getMessage());
        verify(userRepository).existsByUsername("newuser");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_ShouldReturnToken_WhenValidCredentials() {
        // Given
        Authentication authentication = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);
        
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtUtil.generateToken(userDetails)).thenReturn("jwtToken");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(userRepository.findRolesByUserId(1L)).thenReturn(java.util.List.of(adminRole));

        // When
        AuthResponse response = userService.login(loginRequest);

        // Then
        assertNotNull(response);
        assertEquals("jwtToken", response.getToken());
        assertNotNull(response.getUser());
        assertEquals("testuser", response.getUser().getUsername());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtil).generateToken(userDetails);
        verify(userRepository).findByUsername("testuser");
    }

    @Test
    void getAllUsers_ShouldReturnAllUsers() {
        // Given
        User user2 = User.builder()
                .id(2L)
                .username("user2")
                .firstName("User")
                .lastName("Two")
                .enabled(true)
                .build();

        when(userRepository.findAll()).thenReturn(java.util.List.of(testUser, user2));
        when(userRepository.findRolesByUserId(1L)).thenReturn(java.util.List.of(adminRole));
        when(userRepository.findRolesByUserId(2L)).thenReturn(java.util.List.of());

        // When
        java.util.List<UserResponse> users = userService.getAllUsers();

        // Then
        assertNotNull(users);
        assertEquals(2, users.size());
        assertEquals("testuser", users.get(0).getUsername());
        assertEquals("user2", users.get(1).getUsername());

        verify(userRepository).findAll();
    }

    @Test
    void getUserById_ShouldReturnUser_WhenUserExists() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.findRolesByUserId(1L)).thenReturn(java.util.List.of(adminRole));

        // When
        UserResponse response = userService.getUserById(1L);

        // Then
        assertNotNull(response);
        assertEquals("testuser", response.getUsername());
        assertEquals("Test", response.getFirstName());
        assertEquals("User", response.getLastName());

        verify(userRepository).findById(1L);
    }

    @Test
    void getUserById_ShouldThrowException_WhenUserNotFound() {
        // Given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> userService.getUserById(999L));
        
        assertEquals("User not found with id: 999", exception.getMessage());
        verify(userRepository).findById(999L);
    }

    @Test
    void getUserByUsername_ShouldReturnUser_WhenUserExists() {
        // Given
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(userRepository.findRolesByUserId(1L)).thenReturn(java.util.List.of(adminRole));

        // When
        UserResponse response = userService.getUserByUsername("testuser");

        // Then
        assertNotNull(response);
        assertEquals("testuser", response.getUsername());

        verify(userRepository).findByUsername("testuser");
    }

    @Test
    void getUserByUsername_ShouldThrowException_WhenUserNotFound() {
        // Given
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> userService.getUserByUsername("nonexistent"));
        
        assertEquals("User not found with username: nonexistent", exception.getMessage());
        verify(userRepository).findByUsername("nonexistent");
    }

    @Test
    void updateUser_ShouldUpdateUser_WhenValidRequest() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode("newPassword123")).thenReturn("newEncodedPassword");
        when(userRepository.update(any(User.class))).thenReturn(1);
        when(userRepository.findRolesByUserId(1L)).thenReturn(java.util.List.of(adminRole));

        // When
        UserResponse response = userService.updateUser(1L, updateRequest);

        // Then
        assertNotNull(response);
        assertEquals("Updated", response.getFirstName());
        assertEquals("User", response.getLastName());
        assertTrue(response.getEnabled());

        verify(userRepository).findById(1L);
        verify(passwordEncoder).encode("newPassword123");
        verify(userRepository).update(any(User.class));
    }

    @Test
    void updateUser_ShouldThrowException_WhenUserNotFound() {
        // Given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> userService.updateUser(999L, updateRequest));
        
        assertEquals("User not found with id: 999", exception.getMessage());
        verify(userRepository).findById(999L);
        verify(userRepository, never()).update(any(User.class));
    }

    @Test
    void deleteUser_ShouldDeleteUser_WhenUserExists() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.deleteById(1L)).thenReturn(1);

        // When
        userService.deleteUser(1L);

        // Then
        verify(userRepository).findById(1L);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUser_ShouldThrowException_WhenUserNotFound() {
        // Given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> userService.deleteUser(999L));
        
        assertEquals("User not found with id: 999", exception.getMessage());
        verify(userRepository).findById(999L);
        verify(userRepository, never()).deleteById(anyLong());
    }

}
