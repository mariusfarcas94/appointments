package com.motioncare.user.repository;

import com.motioncare.user.model.Role;
import com.motioncare.user.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
@Transactional
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RoleRepository roleRepository;

    private User createTestUser() {
        return User.builder()
                .username("testuser")
                .password("encodedPassword")
                .firstName("Test")
                .lastName("User")
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private Role createTestRole() {
        return Role.builder()
                .name("USER")
                .description("Test role")
                .build();
    }

    @Test
    void findByUsername_ShouldReturnUser_WhenUserExists() {
        // Given
        User user = createTestUser();
        userRepository.save(user);

        // When
        Optional<User> found = userRepository.findByUsername("testuser");

        // Then
        assertTrue(found.isPresent());
        assertEquals("testuser", found.get().getUsername());
        assertEquals("Test", found.get().getFirstName());
        assertEquals("User", found.get().getLastName());
    }

    @Test
    void findByUsername_ShouldReturnEmpty_WhenUserDoesNotExist() {
        // When
        Optional<User> found = userRepository.findByUsername("nonexistent");

        // Then
        assertFalse(found.isPresent());
    }

    @Test
    void findById_ShouldReturnUser_WhenUserExists() {
        // Given
        User user = createTestUser();
        userRepository.save(user);

        // When
        Optional<User> found = userRepository.findById(user.getId());

        // Then
        assertTrue(found.isPresent());
        assertEquals(user.getId(), found.get().getId());
        assertEquals("testuser", found.get().getUsername());
    }

    @Test
    void findById_ShouldReturnEmpty_WhenUserDoesNotExist() {
        // When
        Optional<User> found = userRepository.findById(999L);

        // Then
        assertFalse(found.isPresent());
    }

    @Test
    void findAll_ShouldReturnAllUsers() {
        // Given
        User user1 = createTestUser();
        User user2 = User.builder()
                .username("user2")
                .password("password2")
                .firstName("User")
                .lastName("Two")
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        userRepository.save(user1);
        userRepository.save(user2);

        // When
        List<User> users = userRepository.findAll();

        // Then (should be 3: 2 test users + 1 default admin user)
        assertEquals(3, users.size());
        assertTrue(users.stream().anyMatch(u -> u.getUsername().equals("testuser")));
        assertTrue(users.stream().anyMatch(u -> u.getUsername().equals("user2")));
        assertTrue(users.stream().anyMatch(u -> u.getUsername().equals("admin")));
    }

    @Test
    void save_ShouldSaveUser_WhenValidUser() {
        // Given
        User user = createTestUser();

        // When
        int result = userRepository.save(user);

        // Then
        assertEquals(1, result);
        assertNotNull(user.getId());
        
        // Verify user was saved
        Optional<User> saved = userRepository.findById(user.getId());
        assertTrue(saved.isPresent());
        assertEquals("testuser", saved.get().getUsername());
    }

    @Test
    void update_ShouldUpdateUser_WhenValidUser() {
        // Given
        User user = createTestUser();
        userRepository.save(user);
        user.setFirstName("Updated");
        user.setLastName("Name");
        user.setUpdatedAt(LocalDateTime.now());

        // When
        int result = userRepository.update(user);

        // Then
        assertEquals(1, result);
        
        // Verify user was updated
        Optional<User> updated = userRepository.findById(user.getId());
        assertTrue(updated.isPresent());
        assertEquals("Updated", updated.get().getFirstName());
        assertEquals("Name", updated.get().getLastName());
    }

    @Test
    void deleteById_ShouldDeleteUser_WhenUserExists() {
        // Given
        User user = createTestUser();
        userRepository.save(user);

        // When
        int result = userRepository.deleteById(user.getId());

        // Then
        assertEquals(1, result);
        
        // Verify user was deleted
        Optional<User> deleted = userRepository.findById(user.getId());
        assertFalse(deleted.isPresent());
    }

    @Test
    void existsByUsername_ShouldReturnTrue_WhenUserExists() {
        // Given
        User user = createTestUser();
        userRepository.save(user);

        // When
        boolean exists = userRepository.existsByUsername("testuser");

        // Then
        assertTrue(exists);
    }

    @Test
    void existsByUsername_ShouldReturnFalse_WhenUserDoesNotExist() {
        // When
        boolean exists = userRepository.existsByUsername("nonexistent");

        // Then
        assertFalse(exists);
    }

    @Test
    void assignRole_ShouldAssignRoleToUser() {
        // Given
        User user = createTestUser();
        userRepository.save(user);
        
        // Use existing USER role from database schema
        Optional<Role> existingRole = roleRepository.findByName("USER");
        assertTrue(existingRole.isPresent());

        // When
        userRepository.assignRole(user.getId(), existingRole.get().getId());

        // Then
        // Verify role was assigned by checking roles
        List<Role> userRoles = userRepository.findRolesByUserId(user.getId());
        assertEquals(1, userRoles.size());
        assertEquals("USER", userRoles.get(0).getName());
    }

    @Test
    void removeRole_ShouldRemoveRoleFromUser() {
        // Given
        User user = createTestUser();
        userRepository.save(user);
        
        // Use existing USER role from database schema
        Optional<Role> existingRole = roleRepository.findByName("USER");
        assertTrue(existingRole.isPresent());
        
        // Assign role first
        userRepository.assignRole(user.getId(), existingRole.get().getId());

        // When
        userRepository.removeRole(user.getId(), existingRole.get().getId());

        // Then
        List<Role> userRoles = userRepository.findRolesByUserId(user.getId());
        assertEquals(0, userRoles.size());
    }

    @Test
    void findRolesByUserId_ShouldReturnUserRoles() {
        // Given
        User user = createTestUser();
        userRepository.save(user);
        
        // Use existing roles from database schema
        Optional<Role> userRole = roleRepository.findByName("USER");
        Optional<Role> adminRole = roleRepository.findByName("ADMIN");
        assertTrue(userRole.isPresent());
        assertTrue(adminRole.isPresent());
        
        userRepository.assignRole(user.getId(), userRole.get().getId());
        userRepository.assignRole(user.getId(), adminRole.get().getId());

        // When
        List<Role> roles = userRepository.findRolesByUserId(user.getId());

        // Then
        assertEquals(2, roles.size());
        assertTrue(roles.stream().anyMatch(r -> r.getName().equals("USER")));
        assertTrue(roles.stream().anyMatch(r -> r.getName().equals("ADMIN")));
    }

    @Test
    void findRolesByUserId_ShouldReturnEmptyList_WhenUserHasNoRoles() {
        // Given
        User user = createTestUser();
        userRepository.save(user);

        // When
        List<Role> roles = userRepository.findRolesByUserId(user.getId());

        // Then
        assertTrue(roles.isEmpty());
    }
}
