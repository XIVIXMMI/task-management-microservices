package com.taskmanagement.userservice.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class LoginResponse {
    @Schema(description = "JWT access token", example = "")
    private String accessToken;
    @Schema(description = "JWT refresh token", example = "")
    private String refreshToken;
    @Schema(description = "Expire time", example = "")
    private  Long expiredIn;
    @Schema(description = "Type of token", example = "")
    private  String tokenType;
    @Schema(description = "Unique UUID of user", example = "")
    private UUID userId;
    @Schema(description = "Unique email of user", example = "")
    private  String email;
    @Schema(description = "First name to display user", example = "")
    private String firstName;
}
