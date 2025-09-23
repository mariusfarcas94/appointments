package com.motioncare.user.repository;

import com.motioncare.user.model.Role;
import com.motioncare.user.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

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
        entityManager.persistAndFlush(user);

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
        User saved = entityManager.persistAndFlush(user);

        // When
        Optional<User> found = userRepository.findById(saved.getId());

        // Then
        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
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
                .build();

        entityManager.persistAndFlush(user1);
        entityManager.persistAndFlush(user2);

        // When
        List<User> users = userRepository.findAll();

        // Then
        assertEquals(2, users.size());
        assertTrue(users.stream().anyMatch(u -> u.getUsername().equals("testuser")));
        assertTrue(users.stream().anyMatch(u -> u.getUsername().equals("user2")));
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
        User saved = entityManager.persistAndFlush(user);
        saved.setFirstName("Updated");
        saved.setLastName("Name");

        // When
        int result = userRepository.update(saved);

        // Then
        assertEquals(1, result);
        
        // Verify user was updated
        Optional<User> updated = userRepository.findById(saved.getId());
        assertTrue(updated.isPresent());
        assertEquals("Updated", updated.get().getFirstName());
        assertEquals("Name", updated.get().getLastName());
    }

    @Test
    void deleteById_ShouldDeleteUser_WhenUserExists() {
        // Given
        User user = createTestUser();
        User saved = entityManager.persistAndFlush(user);

        // When
        int result = userRepository.deleteById(saved.getId());

        // Then
        assertEquals(1, result);
        
        // Verify user was deleted
        Optional<User> deleted = userRepository.findById(saved.getId());
        assertFalse(deleted.isPresent());
    }

    @Test
    void existsByUsername_ShouldReturnTrue_WhenUserExists() {
        // Given
        User user = createTestUser();
        entityManager.persistAndFlush(user);

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
        User savedUser = entityManager.persistAndFlush(user);
        
        Role role = createTestRole();
        Role savedRole = entityManager.persistAndFlush(role);

        // When
        userRepository.assignRole(savedUser.getId(), savedRole.getId());

        // Then
        // Verify role was assigned by checking roles
        List<Role> userRoles = userRepository.findRolesByUserId(savedUser.getId());
        assertEquals(1, userRoles.size());
        assertEquals("USER", userRoles.get(0).getName());
    }

    @Test
    void removeRole_ShouldRemoveRoleFromUser() {
        // Given
        User user = createTestUser();
        User savedUser = entityManager.persistAndFlush(user);
        
        Role role = createTestRole();
        Role savedRole = entityManager.persistAndFlush(role);
        
        // Assign role first
        userRepository.assignRole(savedUser.getId(), savedRole.getId());

        // When
        userRepository.removeRole(savedUser.getId(), savedRole.getId());

        // Then
        List<Role> userRoles = userRepository.findRolesByUserId(savedUser.getId());
        assertEquals(0, userRoles.size());
    }

    @Test
    void findRolesByUserId_ShouldReturnUserRoles() {
        // Given
        User user = createTestUser();
        User savedUser = entityManager.persistAndFlush(user);
        
        Role role1 = createTestRole();
        Role role2 = Role.builder()
                .name("ADMIN")
                .description("Admin role")
                .build();
        
        Role savedRole1 = entityManager.persistAndFlush(role1);
        Role savedRole2 = entityManager.persistAndFlush(role2);
        
        userRepository.assignRole(savedUser.getId(), savedRole1.getId());
        userRepository.assignRole(savedUser.getId(), savedRole2.getId());

        // When
        List<Role> roles = userRepository.findRolesByUserId(savedUser.getId());

        // Then
        assertEquals(2, roles.size());
        assertTrue(roles.stream().anyMatch(r -> r.getName().equals("USER")));
        assertTrue(roles.stream().anyMatch(r -> r.getName().equals("ADMIN")));
    }

    @Test
    void findRolesByUserId_ShouldReturnEmptyList_WhenUserHasNoRoles() {
        // Given
        User user = createTestUser();
        User savedUser = entityManager.persistAndFlush(user);

        // When
        List<Role> roles = userRepository.findRolesByUserId(savedUser.getId());

        // Then
        assertTrue(roles.isEmpty());
    }
}
