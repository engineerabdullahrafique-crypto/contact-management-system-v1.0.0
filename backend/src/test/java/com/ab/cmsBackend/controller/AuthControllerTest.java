package com.ab.cmsBackend.controller;

import com.ab.cmsBackend.dto.LoginDto;
import com.ab.cmsBackend.dto.RegisterDto;
import com.ab.cmsBackend.exception.GlobalExceptionHandler;
import com.ab.cmsBackend.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private ObjectMapper objectMapper;
    private RegisterDto registerDto;
    private LoginDto loginDto;

    @BeforeEach
    void setUp() {
        // Adding GlobalExceptionHandler to MockMvc setup
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();

        // Setup RegisterDto
        registerDto = new RegisterDto();
        registerDto.setEmail("test@example.com");
        registerDto.setPhone("1234567890");
        registerDto.setPassword("password123");

        // Setup LoginDto
        loginDto = new LoginDto();
        loginDto.setEmail("test@example.com");
        loginDto.setPassword("password123");
    }

    @Test
    void register_ShouldReturnSuccessMessage() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered"));

        verify(authService, times(1)).register(any(RegisterDto.class));
    }

    @Test
    void login_ShouldReturnToken() throws Exception {
        // Arrange
        String expectedToken = "jwt-token-12345";
        when(authService.login(any(LoginDto.class))).thenReturn(expectedToken);

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedToken));

        verify(authService, times(1)).login(any(LoginDto.class));
    }

    @Test
    void login_ShouldHandleInvalidCredentials() throws Exception {
        // Arrange
        String errorMessage = "Invalid credentials";
        when(authService.login(any(LoginDto.class)))
                .thenThrow(new RuntimeException(errorMessage));

        // Act & Assert - Returns 500 (as GlobalExceptionHandler returns 500 for all exceptions)
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("An error occurred: " + errorMessage));

        verify(authService, times(1)).login(any(LoginDto.class));
    }

    @Test
    void register_ShouldHandleDuplicateEmail() throws Exception {
        // Arrange
        String errorMessage = "Email already exists";
        doThrow(new RuntimeException(errorMessage))
                .when(authService).register(any(RegisterDto.class));

        // Act & Assert - Returns 500 (as GlobalExceptionHandler returns 500 for all exceptions)
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDto)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("An error occurred: " + errorMessage));

        verify(authService, times(1)).register(any(RegisterDto.class));
    }

    @Test
    void register_ShouldWorkWithoutPhone() throws Exception {
        // Arrange - DTO without phone
        registerDto.setPhone(null);

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDto)))
                .andExpect(status().isOk());

        verify(authService, times(1)).register(any(RegisterDto.class));
    }
}