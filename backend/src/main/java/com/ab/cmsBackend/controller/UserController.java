package com.ab.cmsBackend.controller;

import com.ab.cmsBackend.dto.ForgotPasswordDto;
import com.ab.cmsBackend.dto.RegisterDto;
import com.ab.cmsBackend.dto.ResetPasswordDto;
import com.ab.cmsBackend.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public ResponseEntity<RegisterDto> getProfile() {
        // Assume user is authenticated, get from security context
        logger.info("Fetching user profile");
        return ResponseEntity.ok(userService.getProfile());
    }

    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody String newPassword) {
        logger.info("Changing password");
        userService.changePassword(newPassword);
        return ResponseEntity.ok("Password changed");
    }

    // Forgot Password Endpoints

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(
            @Valid @RequestBody ForgotPasswordDto request) {
        logger.info("MS SQL: Forgot password request for email: {}", request.getEmail());

        try {
            userService.initiatePasswordReset(request.getEmail());

            Map<String, String> response = new HashMap<>();
            response.put("message", "Password reset email sent successfully");
            response.put("note", "Check server logs for reset link (development mode)");

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            // For security, return success even if user doesn't exist
            logger.warn("MS SQL: Password reset request handled - {}", e.getMessage());

            Map<String, String> response = new HashMap<>();
            response.put("message", "If an account exists with this email, you will receive a password reset link");

            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(
            @Valid @RequestBody ResetPasswordDto request) {
        logger.info("MS SQL: Processing password reset");

        userService.resetPassword(request.getToken(), request.getNewPassword());

        Map<String, String> response = new HashMap<>();
        response.put("message", "Password has been reset successfully");
        response.put("status", "success");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/validate-reset-token/{token}")
    public ResponseEntity<Map<String, Object>> validateResetToken(@PathVariable String token) {
        logger.info("MS SQL: Validating reset token");

        Map<String, Object> validationResult = userService.validateResetToken(token);

        return ResponseEntity.ok(validationResult);
    }

}