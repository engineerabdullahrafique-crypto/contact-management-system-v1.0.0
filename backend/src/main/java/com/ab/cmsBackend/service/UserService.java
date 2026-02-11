package com.ab.cmsBackend.service;

import com.ab.cmsBackend.dto.RegisterDto;
import com.ab.cmsBackend.entity.User;
import com.ab.cmsBackend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    private User getCurrentUser() {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findByEmail(email).orElseThrow();
    }

    public RegisterDto getProfile() {
        User user = getCurrentUser();
        RegisterDto dto = new RegisterDto();
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        return dto;
    }

    public void changePassword(String newPassword) {
        User user = getCurrentUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    // Forgot Password Method

    public void initiatePasswordReset(String email) {
        logger.info("MS SQL: Initiating password reset for email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        // Generate unique token
        String token = UUID.randomUUID().toString();

        // Set token and expiry (24 hours from now)
        user.setResetToken(token);
        user.setResetTokenExpiry(LocalDateTime.now().plusHours(24));

        userRepository.save(user);

        // Send reset email
        emailService.sendPasswordResetEmail(user.getEmail(), token);

        logger.info("MS SQL: Password reset token generated for user ID: {}", user.getId());
    }

    public void resetPassword(String token, String newPassword) {
        logger.info("MS SQL: Processing password reset with token");

        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid or expired reset token"));

        // Check if token is expired
        if (user.getResetTokenExpiry() == null) {
            throw new RuntimeException("Reset token has no expiry date");
        }

        if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            // Clean up expired token
            user.setResetToken(null);
            user.setResetTokenExpiry(null);
            userRepository.save(user);
            throw new RuntimeException("Reset token has expired");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(newPassword));

        // Clear reset token
        user.setResetToken(null);
        user.setResetTokenExpiry(null);

        userRepository.save(user);

        logger.info("MS SQL: Password reset successfully for user ID: {}", user.getId());
    }

    public Map<String, Object> validateResetToken(String token) {
        Map<String, Object> response = new HashMap<>();

        try {
            User user = userRepository.findByResetToken(token)
                    .orElseThrow(() -> new RuntimeException("Invalid token"));

            if (user.getResetTokenExpiry() == null) {
                response.put("valid", false);
                response.put("message", "Token has no expiry date");
                return response;
            }

            boolean isValid = user.getResetTokenExpiry().isAfter(LocalDateTime.now());

            response.put("valid", isValid);
            response.put("message", isValid ? "Token is valid" : "Token has expired");
            response.put("email", user.getEmail());

            if (isValid) {
                long hoursRemaining = ChronoUnit.HOURS.between(LocalDateTime.now(), user.getResetTokenExpiry());
                response.put("hoursRemaining", hoursRemaining);
            }

            return response;

        } catch (Exception e) {
            logger.warn("MS SQL: Token validation failed: {}", e.getMessage());
            response.put("valid", false);
            response.put("message", "Invalid or expired token");
            return response;
        }
    }

    // Clean up expired tokens
    public void cleanupExpiredTokens() {
        List<User> usersWithExpiredTokens = userRepository.findUsersWithExpiredTokens();

        for (User user : usersWithExpiredTokens) {
            user.setResetToken(null);
            user.setResetTokenExpiry(null);
            logger.debug("Cleaned up expired token for user: {}", user.getEmail());
        }

        userRepository.saveAll(usersWithExpiredTokens);
        logger.info("Cleaned up {} expired password reset tokens", usersWithExpiredTokens.size());
    }
}