package com.ab.cmsBackend.repository;

import com.ab.cmsBackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    // New method for password reset
    @Query("SELECT u FROM User u WHERE u.resetToken = :token")
    Optional<User> findByResetToken(@Param("token") String token);

    // Method to find expired tokens
    @Query("SELECT u FROM User u WHERE u.resetTokenExpiry < CURRENT_TIMESTAMP AND u.resetToken IS NOT NULL")
    List<User> findUsersWithExpiredTokens();
}