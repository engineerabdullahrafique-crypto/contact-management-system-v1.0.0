package com.ab.cmsBackend.controller;

import com.ab.cmsBackend.dto.LoginDto;
import com.ab.cmsBackend.dto.RegisterDto;
import com.ab.cmsBackend.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterDto registerDto) {
        logger.info("Registering user: {}", registerDto.getEmail());
        authService.register(registerDto);
        return ResponseEntity.ok("User registered");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDto loginDto) {
        logger.info("Logging in user: {}", loginDto.getEmail());
        String token = authService.login(loginDto);
        return ResponseEntity.ok(token);
    }
}
