package com.taskmanagement.userservice.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record PasswordResetTokenRequest(
    @NotBlank
    @Email
    String email
) {}
