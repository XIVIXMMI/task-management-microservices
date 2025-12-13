package com.taskmanagement.userservice.application.service;

import com.taskmanagement.userservice.application.dto.PasswordResetTokenRequest;
import com.taskmanagement.userservice.application.dto.ResetPasswordRequest;

public interface ResetPasswordService {

    void createPasswordResetToken(PasswordResetTokenRequest request) throws InterruptedException;
    void resetPassword(ResetPasswordRequest request);
    void cleanupExpiredTokens();
}
