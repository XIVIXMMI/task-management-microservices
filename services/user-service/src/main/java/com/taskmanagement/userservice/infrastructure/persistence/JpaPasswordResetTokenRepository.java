package com.taskmanagement.userservice.infrastructure.persistence;

import com.taskmanagement.userservice.domain.entity.PasswordResetToken;
import com.taskmanagement.userservice.domain.repository.PasswordResetTokenRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaPasswordResetTokenRepository extends JpaRepository<PasswordResetToken, UUID>, PasswordResetTokenRepository {
}
