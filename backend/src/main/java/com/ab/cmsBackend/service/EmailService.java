package com.ab.cmsBackend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Value("${app.frontend-url:http://localhost:3000}")
    private String frontendUrl;

    public void sendPasswordResetEmail(String toEmail, String token) {
        try {
            String resetLink = frontendUrl + "/reset-password?token=" + token;

            // For MS SQL Server
            logger.info("=== Password Reset Request ===");
            logger.info("Timestamp: {}", java.time.LocalDateTime.now());
            logger.info("For User: {}", toEmail);
            logger.info("Reset Token: {}", token);
            logger.info("Reset Link: {}", resetLink);
            logger.info("Token Expires: {}", java.time.LocalDateTime.now().plusHours(24));
            logger.info("==============================");

            // Console output for testing
            System.out.println("\n" + "=".repeat(60));
            System.out.println("📧 Password Reset Email (MS SQL Server)");
            System.out.println("=".repeat(60));
            System.out.println("To: " + toEmail);
            System.out.println("Reset Link: " + resetLink);
            System.out.println("Token: " + token);
            System.out.println("Generated: " + java.time.LocalDateTime.now());
            System.out.println("Expires: " + java.time.LocalDateTime.now().plusHours(24));
            System.out.println("=".repeat(60) + "\n");

        } catch (Exception e) {
            logger.error("Error in password reset email service: {}", e.getMessage());
        }
    }
}