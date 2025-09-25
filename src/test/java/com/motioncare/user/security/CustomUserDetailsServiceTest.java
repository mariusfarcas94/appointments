package com.motioncare.user.security;

import com.motioncare.user.model.Role;
import com.motioncare.user.model.User;
import com.motioncare.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    private User testUser;
    private Role adminRole;
    private Role userRole;

    @BeforeEach
    void setUp() {
        adminRole = Role.builder()
                .id(1L)
                .name("ADMIN")
                .description("Administrator role")
                .build();

        userRole = Role.builder()
                .id(2L)
                .name("USER")
                .description("User role")
                .build();

        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .password("encodedPassword")
                .firstName("Test")
                .lastName("User")
                .enabled(true)
                .roles(null) // Will be set by the service
                .build();
    }

    @Test
    void loadUserByUsername_ShouldReturnUserDetails_WhenUserExists() {
        // Given
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(userRepository.findRolesByUserId(1L)).thenReturn(java.util.List.of(adminRole, userRole));

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

        // Then
        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        assertEquals("encodedPassword", userDetails.getPassword());
        assertTrue(userDetails.isEnabled());
        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isCredentialsNonExpired());

        // Check authorities
        assertEquals(2, userDetails.getAuthorities().size());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ADMIN")));
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("USER")));
    }

    @Test
    void loadUserByUsername_ShouldReturnUserDetails_WhenUserHasNoRoles() {
        // Given
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(userRepository.findRolesByUserId(1L)).thenReturn(java.util.List.of());

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

        // Then
        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        assertEquals("encodedPassword", userDetails.getPassword());
        assertTrue(userDetails.isEnabled());
        assertEquals(0, userDetails.getAuthorities().size());
    }

    @Test
    void loadUserByUsername_ShouldReturnUserDetails_WhenUserIsDisabled() {
        // Given
        User disabledUser = User.builder()
                .id(1L)
                .username("disableduser")
                .password("encodedPassword")
                .enabled(false)
                .build();

        when(userRepository.findByUsername("disableduser")).thenReturn(Optional.of(disabledUser));
        when(userRepository.findRolesByUserId(1L)).thenReturn(java.util.List.of(userRole));

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername("disableduser");

        // Then
        assertNotNull(userDetails);
        assertEquals("disableduser", userDetails.getUsername());
        assertFalse(userDetails.isEnabled());
    }

    @Test
    void loadUserByUsername_ShouldThrowException_WhenUserNotFound() {
        // Given
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // When & Then
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("nonexistent"));

        assertEquals("User not found with username: nonexistent", exception.getMessage());
    }

    @Test
    void loadUserByUsername_ShouldHandleNullUsername() {
        // Given
        when(userRepository.findByUsername(null)).thenReturn(Optional.empty());

        // When & Then
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername(null));

        assertEquals("User not found with username: null", exception.getMessage());
    }

    @Test
    void loadUserByUsername_ShouldHandleEmptyUsername() {
        // Given
        when(userRepository.findByUsername("")).thenReturn(Optional.empty());

        // When & Then
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername(""));

        assertEquals("User not found with username: ", exception.getMessage());
    }

    @Test
    void loadUserByUsername_ShouldLoadRolesCorrectly_WhenUserHasMultipleRoles() {
        // Given
        Role superAdminRole = Role.builder()
                .id(3L)
                .name("SUPER_ADMIN")
                .description("Super Administrator role")
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(userRepository.findRolesByUserId(1L)).thenReturn(java.util.List.of(adminRole, userRole, superAdminRole));

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

        // Then
        assertNotNull(userDetails);
        assertEquals(3, userDetails.getAuthorities().size());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ADMIN")));
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("USER")));
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("SUPER_ADMIN")));
    }
}
