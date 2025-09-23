package com.motioncare.user.security;

import com.motioncare.user.model.Role;
import com.motioncare.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtUtilTest {

    @InjectMocks
    private JwtUtil jwtUtil;

    private User testUser;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        // Set test values using reflection
        ReflectionTestUtils.setField(jwtUtil, "secret", "mySecretKey123456789012345678901234567890");
        ReflectionTestUtils.setField(jwtUtil, "expiration", 86400000L); // 24 hours

        Role adminRole = Role.builder()
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
                .roles(Set.of(adminRole))
                .build();

        userDetails = org.springframework.security.core.userdetails.User.builder()
                .username("testuser")
                .password("encodedPassword")
                .authorities("ADMIN")
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }

    @Test
    void generateToken_ShouldGenerateValidToken_WhenValidUserDetails() {
        // When
        String token = jwtUtil.generateToken(userDetails);

        // Then
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.contains(".")); // JWT format has dots
    }

    @Test
    void generateToken_ShouldGenerateValidToken_WhenValidUser() {
        // When
        String token = jwtUtil.generateToken(testUser);

        // Then
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.contains(".")); // JWT format has dots
    }

    @Test
    void extractUsername_ShouldExtractUsername_WhenValidToken() {
        // Given
        String token = jwtUtil.generateToken(userDetails);

        // When
        String username = jwtUtil.extractUsername(token);

        // Then
        assertEquals("testuser", username);
    }

    @Test
    void extractExpiration_ShouldExtractExpiration_WhenValidToken() {
        // Given
        String token = jwtUtil.generateToken(userDetails);

        // When
        java.util.Date expiration = jwtUtil.extractExpiration(token);

        // Then
        assertNotNull(expiration);
        assertTrue(expiration.after(new java.util.Date()));
    }

    @Test
    void validateToken_ShouldReturnTrue_WhenValidTokenAndUserDetails() {
        // Given
        String token = jwtUtil.generateToken(userDetails);

        // When
        Boolean isValid = jwtUtil.validateToken(token, userDetails);

        // Then
        assertTrue(isValid);
    }

    @Test
    void validateToken_ShouldReturnFalse_WhenInvalidUsername() {
        // Given
        String token = jwtUtil.generateToken(userDetails);
        UserDetails differentUser = org.springframework.security.core.userdetails.User.builder()
                .username("differentuser")
                .password("password")
                .authorities("USER")
                .build();

        // When
        Boolean isValid = jwtUtil.validateToken(token, differentUser);

        // Then
        assertFalse(isValid);
    }

    @Test
    void extractClaim_ShouldExtractSubject_WhenValidToken() {
        // Given
        String token = jwtUtil.generateToken(userDetails);

        // When
        String subject = jwtUtil.extractClaim(token, claims -> claims.getSubject());

        // Then
        assertEquals("testuser", subject);
    }

    @Test
    void extractClaim_ShouldExtractIssuedAt_WhenValidToken() {
        // Given
        String token = jwtUtil.generateToken(userDetails);

        // When
        java.util.Date issuedAt = jwtUtil.extractClaim(token, claims -> claims.getIssuedAt());

        // Then
        assertNotNull(issuedAt);
        assertTrue(issuedAt.before(new java.util.Date()) || issuedAt.equals(new java.util.Date()));
    }

    @Test
    void generateToken_ShouldGenerateValidTokens_WhenCalledMultipleTimes() {
        // When
        String token1 = jwtUtil.generateToken(userDetails);
        String token2 = jwtUtil.generateToken(userDetails);

        // Then
        assertNotNull(token1);
        assertNotNull(token2);
        assertTrue(token1.contains("."));
        assertTrue(token2.contains("."));
        // Tokens might be the same if generated at the same time, which is valid
    }

    @Test
    void extractUsername_ShouldThrowException_WhenInvalidToken() {
        // Given
        String invalidToken = "invalid.token.here";

        // When & Then
        assertThrows(Exception.class, () -> jwtUtil.extractUsername(invalidToken));
    }

    @Test
    void validateToken_ShouldThrowException_WhenInvalidToken() {
        // Given
        String invalidToken = "invalid.token.here";

        // When & Then
        assertThrows(Exception.class, () -> jwtUtil.validateToken(invalidToken, userDetails));
    }
}
