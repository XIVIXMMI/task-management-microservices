package com.taskmanagement.userservice.application.dto;

import jakarta.validation.constraints.NotNull;

public record RefreshTokenRequest(
    @NotNull String refreshToken
) {
}