package com.taskmanagement.userservice.domain.repository;

import com.taskmanagement.userservice.domain.entity.PasswordResetToken;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface PasswordResetTokenRepository {

    void deleteByUserId(UUID userId);
    Optional<PasswordResetToken> findByToken(String token);
    PasswordResetToken save(PasswordResetToken resetToken);
    void deleteByExpiryAtBefore(LocalDateTime now);
}
