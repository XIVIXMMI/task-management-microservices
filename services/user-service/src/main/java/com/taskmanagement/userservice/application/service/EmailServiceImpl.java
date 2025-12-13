package com.taskmanagement.userservice.application.service;

import com.taskmanagement.userservice.application.dto.SendEmailResetRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService{

    @Override
    public void sendPasswordResetEmail(SendEmailResetRequest request) {
        // Implementation for sending password reset email

        log.info("=================================");
        log.info("PASSWORD RESET EMAIL");
        log.info("To: {}", request.getEmail());
        log.info("Reset Link: {}", request.getResetLink());
        log.info("=================================");
    }
}
