package com.ab.cmsBackend.service;

import com.ab.cmsBackend.dto.LoginDto;
import com.ab.cmsBackend.dto.RegisterDto;
import com.ab.cmsBackend.entity.User;
import com.ab.cmsBackend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

// This annotation tells JUnit to use Mockito for mocking
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    // Mock objects (fake objects) for dependencies
    @Mock
    private UserRepository userRepository;  // Fake database

    @Mock
    private PasswordEncoder passwordEncoder;  // Fake password encoder

    // The service we want to test (it will use our mock objects)
    @InjectMocks
    private AuthService authService;

    // Test data
    private RegisterDto registerDto;
    private LoginDto loginDto;
    private User testUser;

    // This runs before each test
    @BeforeEach
    void setUp() {
        // Setup test data for registration
        registerDto = new RegisterDto();
        registerDto.setEmail("test@example.com");
        registerDto.setPhone("1234567890");
        registerDto.setPassword("password123");

        // Setup test data for login
        loginDto = new LoginDto();
        loginDto.setEmail("test@example.com");
        loginDto.setPassword("password123");

        // Setup a test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setPhone("1234567890");
        testUser.setPassword("encodedPassword123");
    }

    // Registration Test

    // Test 1: Successful registration
    @Test
    void register_ShouldSaveUser_WhenValidData() {
        // Given: Mock the password encoder to return encoded password
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword123");

        // When: Call the register method
        authService.register(registerDto);

        // Then: Verify the user was saved with correct data
        verify(userRepository, times(1)).save(any(User.class));

        // You can also capture the saved user to verify fields
        verify(passwordEncoder, times(1)).encode("password123");
    }

    // Test 2: Registration encodes password
    @Test
    void register_ShouldEncodePassword() {
        // Given: Setup password encoder mock
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword123");

        // When: Call register
        authService.register(registerDto);

        // Then: Verify password was encoded
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(argThat(user ->
                user.getEmail().equals("test@example.com") &&
                        user.getPhone().equals("1234567890")
        ));
    }

    // Test 3: Registration with different user data
    @Test
    void register_ShouldWorkWithDifferentUserData() {
        // Given: Different registration data
        RegisterDto differentDto = new RegisterDto();
        differentDto.setEmail("another@example.com");
        differentDto.setPhone("5555555555");
        differentDto.setPassword("differentPass");

        when(passwordEncoder.encode("differentPass")).thenReturn("encodedDifferentPass");

        // When: Call register
        authService.register(differentDto);

        // Then: Verify user was saved
        verify(userRepository).save(argThat(user ->
                user.getEmail().equals("another@example.com") &&
                        user.getPhone().equals("5555555555")
        ));
    }

    // Login Test

    // Test 4: Successful login
    @Test
    void login_ShouldReturnToken_WhenCredentialsAreValid() {
        // Given: Mock the repository to return user
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // Given: Mock password encoder to return true for password match
        when(passwordEncoder.matches("password123", "encodedPassword123")).thenReturn(true);

        // When: Call login method
        String token = authService.login(loginDto);

        // Then: Should return a token (JWT string)
        assertNotNull(token, "Token should not be null");
        assertFalse(token.isEmpty(), "Token should not be empty");

        // Verify the mocks were called
        verify(userRepository).findByEmail("test@example.com");
        verify(passwordEncoder).matches("password123", "encodedPassword123");
    }

    // Test 5: Login fails when user not found
    @Test
    void login_ShouldThrowException_WhenUserNotFound() {
        // Given: Mock repository to return empty (user not found)
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // Given: Create login DTO with non-existent email
        LoginDto wrongLoginDto = new LoginDto();
        wrongLoginDto.setEmail("nonexistent@example.com");
        wrongLoginDto.setPassword("password123");

        // When & Then: Should throw exception
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.login(wrongLoginDto);
        });

        assertEquals("User not found", exception.getMessage());

        // Verify repository was called
        verify(userRepository).findByEmail("nonexistent@example.com");
        // Password encoder should NOT be called if user not found
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    // Test 6: Login fails when password is wrong
    @Test
    void login_ShouldThrowException_WhenPasswordIsInvalid() {
        // Given: Mock repository to return user
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // Given: Mock password encoder to return false (wrong password)
        when(passwordEncoder.matches("wrongPassword", "encodedPassword123")).thenReturn(false);

        // Given: Create login DTO with wrong password
        LoginDto wrongPasswordDto = new LoginDto();
        wrongPasswordDto.setEmail("test@example.com");
        wrongPasswordDto.setPassword("wrongPassword");

        // When & Then: Should throw exception
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.login(wrongPasswordDto);
        });

        assertEquals("Invalid credentials", exception.getMessage());

        // Verify both mocks were called
        verify(userRepository).findByEmail("test@example.com");
        verify(passwordEncoder).matches("wrongPassword", "encodedPassword123");
    }

    // Test 7: Login with different user
    @Test
    void login_ShouldWorkWithDifferentUser() {
        // Given: Create a different user
        User anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setEmail("another@example.com");
        anotherUser.setPassword("encodedPass456");

        // Given: Mock repository to return this user
        when(userRepository.findByEmail("another@example.com")).thenReturn(Optional.of(anotherUser));

        // Given: Mock password encoder
        when(passwordEncoder.matches("password456", "encodedPass456")).thenReturn(true);

        // Given: Create login DTO for this user
        LoginDto anotherLoginDto = new LoginDto();
        anotherLoginDto.setEmail("another@example.com");
        anotherLoginDto.setPassword("password456");

        // When: Call login
        String token = authService.login(anotherLoginDto);

        // Then: Should return token
        assertNotNull(token);

        // Verify mocks were called
        verify(userRepository).findByEmail("another@example.com");
        verify(passwordEncoder).matches("password456", "encodedPass456");
    }

    // Test 8: Token should contain email as subject
    @Test
    void login_ShouldGenerateTokenWithEmailSubject() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", "encodedPassword123")).thenReturn(true);

        // When
        String token = authService.login(loginDto);

        // Then: The token should be a JWT (starts with 3 parts separated by dots)
        assertTrue(token.split("\\.").length == 3, "Token should be a valid JWT with 3 parts");

        // Note: We can't easily check the content without decoding, but we can check it's not empty
        assertFalse(token.isEmpty());
    }

    // Test 9: Login with empty password
    @Test
    void login_ShouldHandleEmptyPassword() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // When password is empty, matches will be called with empty string
        when(passwordEncoder.matches("", "encodedPassword123")).thenReturn(false);

        // Given: Login DTO with empty password
        LoginDto emptyPasswordDto = new LoginDto();
        emptyPasswordDto.setEmail("test@example.com");
        emptyPasswordDto.setPassword("");

        // When & Then: Should throw exception
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.login(emptyPasswordDto);
        });

        assertEquals("Invalid credentials", exception.getMessage());
    }

    // Test 10: Login with null password
    @Test
    void login_ShouldHandleNullPassword() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // When password is null, matches will be called with null
        when(passwordEncoder.matches(null, "encodedPassword123")).thenReturn(false);

        // Given: Login DTO with null password
        LoginDto nullPasswordDto = new LoginDto();
        nullPasswordDto.setEmail("test@example.com");
        nullPasswordDto.setPassword(null);

        // When & Then: Should throw exception
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.login(nullPasswordDto);
        });

        assertEquals("Invalid credentials", exception.getMessage());
    }

    // Edge Cases

    // Test 11: Register with null data
    @Test
    void register_ShouldHandleNullData() {
        // Given: Create RegisterDto with null values
        RegisterDto nullDto = new RegisterDto();
        nullDto.setEmail(null);
        nullDto.setPhone(null);
        nullDto.setPassword(null);

        // When: Mock password encoder to handle null
        when(passwordEncoder.encode(null)).thenReturn("encodedNull");

        // When: Call register (should not throw exception)
        assertDoesNotThrow(() -> {
            authService.register(nullDto);
        });

        // Then: Verify save was called
        verify(userRepository).save(any(User.class));
    }

    // Test 12: Multiple registrations
    @Test
    void register_ShouldHandleMultipleRegistrations() {
        // Given: First registration
        RegisterDto firstDto = new RegisterDto();
        firstDto.setEmail("first@example.com");
        firstDto.setPassword("pass1");

        RegisterDto secondDto = new RegisterDto();
        secondDto.setEmail("second@example.com");
        secondDto.setPassword("pass2");

        when(passwordEncoder.encode("pass1")).thenReturn("encoded1");
        when(passwordEncoder.encode("pass2")).thenReturn("encoded2");

        // When: Register two users
        authService.register(firstDto);
        authService.register(secondDto);

        // Then: Should save twice
        verify(userRepository, times(2)).save(any(User.class));
        verify(passwordEncoder, times(1)).encode("pass1");
        verify(passwordEncoder, times(1)).encode("pass2");
    }
}