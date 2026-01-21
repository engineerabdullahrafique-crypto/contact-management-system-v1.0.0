package com.ab.cmsBackend.controller;

import com.ab.cmsBackend.dto.RegisterDto;
import com.ab.cmsBackend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}