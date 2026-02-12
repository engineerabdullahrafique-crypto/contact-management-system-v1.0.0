package com.ab.cmsBackend.repository;

import com.ab.cmsBackend.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

// This annotation tells Spring to configure only the JPA components (repositories, entities)
// we uses an in-memory H2 database for testing
@DataJpaTest
class UserRepositoryTest {

    // TestEntityManager helps us set up test data
    @Autowired
    private TestEntityManager entityManager;

    // The repository we want to test
    @Autowired
    private UserRepository userRepository;

    // Test data that we'll use in multiple tests
    private User user1;
    private User user2;

    // runs before each test
    @BeforeEach
    void setUp() {
        // Create first test user
        user1 = new User();
        user1.setEmail("john@example.com");
        user1.setPhone("1234567890");
        user1.setPassword("password123");

        // Create second test user
        user2 = new User();
        user2.setEmail("jane@example.com");
        user2.setPhone("0987654321");
        user2.setPassword("password456");

        // Save users to test database
        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.flush();
    }

    // Test 1: Find user by ID (JpaRepository built-in method)
    @Test
    void findById_ShouldReturnUser_WhenUserExists() {
        // When: Try to find user by ID
        Optional<User> foundUser = userRepository.findById(user1.getId());

        // Then: Check if user was found and has correct email
        assertTrue(foundUser.isPresent(), "User should be found");
        assertEquals("john@example.com", foundUser.get().getEmail(), "Email should match");
    }

    // Test 2: Find user by ID when user doesn't exist
    @Test
    void findById_ShouldReturnEmpty_WhenUserDoesNotExist() {
        // When: Try to find user with ID that doesn't exist
        Optional<User> foundUser = userRepository.findById(999L);

        // Then: Optional should be empty
        assertFalse(foundUser.isPresent(), "User should not be found");
    }

    // Test 3: Find user by email (Your custom method)
    @Test
    void findByEmail_ShouldReturnUser_WhenEmailExists() {
        // When: Try to find user by email
        Optional<User> foundUser = userRepository.findByEmail("john@example.com");

        // Then: Check if user was found
        assertTrue(foundUser.isPresent(), "User should be found by email");
        assertEquals("john@example.com", foundUser.get().getEmail(), "Email should match");
        assertEquals("1234567890", foundUser.get().getPhone(), "Phone should match");
    }

    // Test 4: Find user by email when email doesn't exist
    @Test
    void findByEmail_ShouldReturnEmpty_WhenEmailDoesNotExist() {
        // When: Try to find user with email that doesn't exist
        Optional<User> foundUser = userRepository.findByEmail("nonexistent@example.com");

        // Then: Optional should be empty
        assertFalse(foundUser.isPresent(), "User should not be found");
    }

    // Test 5: Find user by email (case-sensitive)
    @Test
    void findByEmail_ShouldBeCaseSensitive() {
        // When: Try to find user with different case
        Optional<User> foundUser = userRepository.findByEmail("JOHN@EXAMPLE.COM"); // Uppercase

        // Then: Should not find because email is case-sensitive
        assertFalse(foundUser.isPresent(), "Email search should be case-sensitive");
    }

    // Test 6: Save new user
    @Test
    void save_ShouldSaveNewUser() {
        // Given: Create a new user
        User newUser = new User();
        newUser.setEmail("new@example.com");
        newUser.setPhone("5555555555");
        newUser.setPassword("newpassword");

        // When: Save the user
        User savedUser = userRepository.save(newUser);

        // Then: Check saved user has ID and correct data
        assertNotNull(savedUser.getId(), "Saved user should have an ID");
        assertEquals("new@example.com", savedUser.getEmail(), "Email should match");
        assertEquals("5555555555", savedUser.getPhone(), "Phone should match");

        // Also verify it exists in database
        Optional<User> foundUser = userRepository.findById(savedUser.getId());
        assertTrue(foundUser.isPresent(), "User should exist in database");
    }

    // Test 7: Update existing user
    @Test
    void save_ShouldUpdateExistingUser() {
        // Given: Get user and change email
        user1.setEmail("updated@example.com");

        // When: Save the updated user
        User updatedUser = userRepository.save(user1);

        // Then: Check user was updated
        assertEquals("updated@example.com", updatedUser.getEmail(), "Email should be updated");
        assertEquals(user1.getId(), updatedUser.getId(), "ID should remain the same");

        // Verify in database
        Optional<User> foundUser = userRepository.findById(user1.getId());
        assertTrue(foundUser.isPresent());
        assertEquals("updated@example.com", foundUser.get().getEmail());
    }

    // Test 8: Delete user
    @Test
    void delete_ShouldRemoveUser() {
        // Given: Get user ID
        Long userId = user1.getId();

        // When: Delete the user
        userRepository.delete(user1);

        // Then: User should not exist in database
        Optional<User> foundUser = userRepository.findById(userId);
        assertFalse(foundUser.isPresent(), "User should be deleted");
    }

    // Test 9: Find all users
    @Test
    void findAll_ShouldReturnAllUsers() {
        // When: Get all users
        List<User> allUsers = userRepository.findAll();

        // Then: Should return 2 users
        assertEquals(2, allUsers.size(), "Should find 2 users");

        // Check both emails exist in the list
        List<String> emails = allUsers.stream()
                .map(User::getEmail)
                .toList();

        assertTrue(emails.contains("john@example.com"));
        assertTrue(emails.contains("jane@example.com"));
    }

    // Test 10: Count users
    @Test
    void count_ShouldReturnTotalNumberOfUsers() {
        // When: Count all users
        long count = userRepository.count();

        // Then: Should be 2
        assertEquals(2, count, "Should count 2 users");
    }

    // Test 11: Save multiple users
    @Test
    void saveAll_ShouldSaveMultipleUsers() {
        // Given: Create multiple users
        User user3 = new User();
        user3.setEmail("user3@example.com");
        user3.setPhone("1111111111");
        user3.setPassword("pass3");

        User user4 = new User();
        user4.setEmail("user4@example.com");
        user4.setPhone("2222222222");
        user4.setPassword("pass4");

        List<User> newUsers = List.of(user3, user4);

        // When: Save all users at once
        List<User> savedUsers = userRepository.saveAll(newUsers);

        // Then: All users should be saved
        assertEquals(2, savedUsers.size());

        // Check total count in database
        long count = userRepository.count();
        assertEquals(4, count, "Should have 4 users total");
    }

    // Test 12: Check if user exists by ID
    @Test
    void existsById_ShouldReturnTrue_WhenUserExists() {
        // When: Check if user exists
        boolean exists = userRepository.existsById(user1.getId());

        // Then: Should return true
        assertTrue(exists, "User should exist");
    }

    @Test
    void existsById_ShouldReturnFalse_WhenUserDoesNotExist() {
        // When: Check with non-existent ID
        boolean exists = userRepository.existsById(999L);

        // Then: Should return false
        assertFalse(exists, "User should not exist");
    }
}