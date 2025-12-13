package com.taskmanagement.userservice.application.service;

import com.taskmanagement.userservice.application.dto.SendEmailResetRequest;

public interface EmailService {

    void sendPasswordResetEmail(SendEmailResetRequest request);
}
