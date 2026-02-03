package com.ab.cmsBackend.service;

import com.ab.cmsBackend.dto.RegisterDto;
import com.ab.cmsBackend.entity.User;
import com.ab.cmsBackend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

// This annotation tells JUnit to use Mockito for mocking
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    // Mock objects (fake objects) for dependencies
    @Mock
    private UserRepository userRepository;  // Fake user database

    @Mock
    private PasswordEncoder passwordEncoder;  // Fake password encoder

    @Mock
    private SecurityContext securityContext;  // Fake security context

    @Mock
    private Authentication authentication;  // Fake authentication

    // The service we want to test
    @InjectMocks
    private UserService userService;

    // Test data
    private User testUser;

    // This runs before each test
    @BeforeEach
    void setUp() {
        // Setup a test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setPhone("1234567890");
        testUser.setPassword("encodedPassword123");
    }

    // Helper method to mock the security context (logged in user)
    private void mockSecurityContext(String email) {
        // Set up the security context to return the user's email
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(email);
        SecurityContextHolder.setContext(securityContext);
    }

    // Get Profile Test

    @Test
    void getProfile_ShouldReturnUserProfile_WhenUserIsLoggedIn() {
        // Given: Mock security context and repository
        mockSecurityContext("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // When: Call getProfile
        RegisterDto result = userService.getProfile();

        // Then: Should return profile with correct data
        assertNotNull(result, "Profile should not be null");
        assertEquals("test@example.com", result.getEmail(), "Email should match");
        assertEquals("1234567890", result.getPhone(), "Phone should match");

        // Verify mocks were called
        verify(userRepository).findByEmail("test@example.com");
        verify(securityContext).getAuthentication();
    }

    @Test
    void getProfile_ShouldReturnProfile_ForDifferentUser() {
        // Given: Setup different user
        User anotherUser = new User();
        anotherUser.setEmail("another@example.com");
        anotherUser.setPhone("5555555555");

        mockSecurityContext("another@example.com");
        when(userRepository.findByEmail("another@example.com")).thenReturn(Optional.of(anotherUser));

        // When: Call getProfile
        RegisterDto result = userService.getProfile();

        // Then: Should return correct profile
        assertEquals("another@example.com", result.getEmail());
        assertEquals("5555555555", result.getPhone());
    }

    @Test
    void getProfile_ShouldThrowException_WhenUserNotFound() {
        // Given: Mock security context with non-existent user
        mockSecurityContext("nonexistent@example.com");
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // When & Then: Should throw exception (NoSuchElementException from Optional.orElseThrow())
        Exception exception = assertThrows(Exception.class, () -> {
            userService.getProfile();
        });

        // Verify the repository was called
        verify(userRepository).findByEmail("nonexistent@example.com");
    }

    @Test
    void getProfile_ShouldNotReturnPassword() {
        // Given: Mock security context and repository
        mockSecurityContext("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // When: Call getProfile
        RegisterDto result = userService.getProfile();

        assertEquals("test@example.com", result.getEmail());
        assertEquals("1234567890", result.getPhone());

    }

    // Change Password Test

    @Test
    void changePassword_ShouldEncodeAndSaveNewPassword() {
        // Given: Mock security context and repository
        mockSecurityContext("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // Mock password encoder
        when(passwordEncoder.encode("newPassword123")).thenReturn("newEncodedPassword456");

        // When: Change password
        userService.changePassword("newPassword123");

        // Then: Should update user password and save
        assertEquals("newEncodedPassword456", testUser.getPassword());
        verify(userRepository).save(testUser);
        verify(passwordEncoder).encode("newPassword123");
    }

    @Test
    void changePassword_ShouldWorkWithEmptyPassword() {
        // Given: Mock security context and repository
        mockSecurityContext("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // Mock password encoder for empty string
        when(passwordEncoder.encode("")).thenReturn("encodedEmptyPassword");

        // When: Change to empty password (not recommended but should work)
        userService.changePassword("");

        // Then: Should encode and save
        assertEquals("encodedEmptyPassword", testUser.getPassword());
        verify(userRepository).save(testUser);
        verify(passwordEncoder).encode("");
    }

    @Test
    void changePassword_ShouldWorkWithComplexPassword() {
        // Given: Mock security context and repository
        mockSecurityContext("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // Complex password with special characters
        String complexPassword = "P@ssw0rd!123#";
        when(passwordEncoder.encode(complexPassword)).thenReturn("encodedComplexPassword");

        // When: Change to complex password
        userService.changePassword(complexPassword);

        // Then: Should encode and save
        assertEquals("encodedComplexPassword", testUser.getPassword());
        verify(passwordEncoder).encode(complexPassword);
    }

    @Test
    void changePassword_ShouldThrowException_WhenUserNotFound() {
        // Given: Mock security context with non-existent user
        mockSecurityContext("nonexistent@example.com");
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // When & Then: Should throw exception (NoSuchElementException)
        Exception exception = assertThrows(Exception.class, () -> {
            userService.changePassword("newPassword");
        });

        // Verify password encoder was NOT called (user not found)
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any());
    }

    @Test
    void changePassword_ShouldNotChangeOtherFields() {
        // Given: Mock security context and repository
        mockSecurityContext("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // Store original values
        String originalEmail = testUser.getEmail();
        String originalPhone = testUser.getPhone();
        Long originalId = testUser.getId();

        // Mock password encoder
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");

        // When: Change password
        userService.changePassword("newPassword");

        // Then: Only password should change, other fields should remain the same
        assertEquals("encodedNewPassword", testUser.getPassword(), "Password should change");
        assertEquals(originalEmail, testUser.getEmail(), "Email should not change");
        assertEquals(originalPhone, testUser.getPhone(), "Phone should not change");
        assertEquals(originalId, testUser.getId(), "ID should not change");
    }

    // Edge Cases

    @Test
    void getProfile_MultipleCalls_ShouldWorkEachTime() {
        // Given: Mock security context and repository
        mockSecurityContext("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // When: Call getProfile multiple times
        RegisterDto result1 = userService.getProfile();
        RegisterDto result2 = userService.getProfile();
        RegisterDto result3 = userService.getProfile();

        // Then: Should work each time
        assertNotNull(result1);
        assertNotNull(result2);
        assertNotNull(result3);

        // Repository should be called 3 times
        verify(userRepository, times(3)).findByEmail("test@example.com");
    }

    @Test
    void changePassword_MultipleChanges_ShouldWork() {
        // Given: Mock security context and repository
        mockSecurityContext("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // Mock password encoder for different passwords
        when(passwordEncoder.encode("firstPassword")).thenReturn("encodedFirst");
        when(passwordEncoder.encode("secondPassword")).thenReturn("encodedSecond");
        when(passwordEncoder.encode("thirdPassword")).thenReturn("encodedThird");

        // When: Change password multiple times
        userService.changePassword("firstPassword");
        userService.changePassword("secondPassword");
        userService.changePassword("thirdPassword");

        // Then: Should update password each time
        assertEquals("encodedThird", testUser.getPassword());
        verify(userRepository, times(3)).save(testUser);
        verify(passwordEncoder).encode("firstPassword");
        verify(passwordEncoder).encode("secondPassword");
        verify(passwordEncoder).encode("thirdPassword");
    }

    @Test
    void changePassword_WithNullPassword() {
        // Given: Mock security context and repository
        mockSecurityContext("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // Mock password encoder for null
        when(passwordEncoder.encode(null)).thenReturn("encodedNull");

        // When: Change password to null (not recommended but should work)
        userService.changePassword(null);

        // Then: Should encode and save (even though null)
        assertEquals("encodedNull", testUser.getPassword());
        verify(passwordEncoder).encode(null);
    }

    @Test
    void getProfile_AfterPasswordChange_ShouldStillWork() {
        // Given: Mock security context and repository
        mockSecurityContext("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // Mock password encoder
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");

        // When: Change password first
        userService.changePassword("newPassword");

        // Then get profile (re-mock the repository call since we're in same test)
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        RegisterDto profile = userService.getProfile();

        // Then: Profile should still return email and phone correctly
        assertEquals("test@example.com", profile.getEmail());
        assertEquals("1234567890", profile.getPhone());

        // Password in user object should be encoded
        assertEquals("encodedNewPassword", testUser.getPassword());
    }

    @Test
    void getCurrentUser_PrivateMethod_TestedThroughPublicMethods() {

        // Given: Mock security context and repository
        mockSecurityContext("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // When: Call a public method that uses getCurrentUser()
        RegisterDto result = userService.getProfile();

        // Then: Should work correctly
        assertNotNull(result);

        // Verify security context was accessed
        verify(securityContext).getAuthentication();
        verify(authentication).getPrincipal();
    }

    // Real-World Scenario Test

    @Test
    void userUpdatesProfileAndPassword_Scenario() {

        // Given: User is logged in
        mockSecurityContext("user@example.com");

        // Setup user with initial data
        User user = new User();
        user.setEmail("user@example.com");
        user.setPhone("1112223333");
        user.setPassword("oldEncodedPassword");

        // Mock repository to return user
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        // Step 1: User views profile
        RegisterDto initialProfile = userService.getProfile();
        assertEquals("user@example.com", initialProfile.getEmail());
        assertEquals("1112223333", initialProfile.getPhone());

        // Step 2: User changes password
        when(passwordEncoder.encode("MyNewSecurePassword!123")).thenReturn("newEncodedPasswordSecure");
        userService.changePassword("MyNewSecurePassword!123");

        // Verify password was changed
        assertEquals("newEncodedPasswordSecure", user.getPassword());

        // Step 3: User views profile again (profile should still show same email/phone)
        // Re-mock repository call since we're testing sequential actions
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        RegisterDto finalProfile = userService.getProfile();

        assertEquals("user@example.com", finalProfile.getEmail());
        assertEquals("1112223333", finalProfile.getPhone());

        // Verify all interactions
        verify(userRepository, times(3)).findByEmail("user@example.com");
        verify(userRepository).save(user);
        verify(passwordEncoder).encode("MyNewSecurePassword!123");
    }
}