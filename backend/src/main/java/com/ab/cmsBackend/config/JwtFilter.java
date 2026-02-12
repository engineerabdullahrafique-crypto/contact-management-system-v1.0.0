package com.ab.cmsBackend.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class JwtFilter extends OncePerRequestFilter {

    private final String SECRET_KEY = "my-very-long-and-secure-secret-key-that-must-be-at-least-64-characters-long-12345";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

                // Validate and parse the token
                // Use .parser() instead of .builder() for reading tokens
                String email = Jwts.parser()
                        .verifyWith(key) // Updated syntax for 0.12.5
                        .build()
                        .parseSignedClaims(token) // Updated syntax
                        .getPayload() // Use .getPayload() instead of .getBody()
                        .getSubject();

                if (email != null) {
                    // This line tells ContactService who the "Current User" is
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(email, null, new ArrayList<>());
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            } catch (Exception e) {
                // If token is invalid, the context stays empty and results in 403
                System.out.println("JWT Validation failed: " + e.getMessage());
            }
        }
        filterChain.doFilter(request, response);
    }
}