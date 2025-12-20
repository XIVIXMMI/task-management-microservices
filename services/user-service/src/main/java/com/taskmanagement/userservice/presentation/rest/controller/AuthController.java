package com.taskmanagement.userservice.presentation.rest.controller;

import com.taskmanagement.userservice.application.dto.*;
import com.taskmanagement.userservice.application.service.AuthService;
import com.taskmanagement.userservice.application.service.ResetPasswordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/auth")
@Tag(name = "Authentication",
        description = "Endpoints for user authentication and authorization")
public class AuthController {

    private final AuthService authService;
    private final ResetPasswordService resetPasswordService;

    @PostMapping("/login")
    @Operation(summary = "User Login",
            description = "Authenticate user and return JWT tokens")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    @Operation(summary = "User Register",
            description = "Create new User")
    public ResponseEntity<RegisterResponse> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        RegisterResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "Refresh Token",
            description = "Refresh JWT tokens using a valid refresh token")
    public ResponseEntity<LoginResponse> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    @PostMapping("/password/forgot")
    @Operation(summary = "Forgot Password",
            description = "Send password reset email to user")
    public ResponseEntity<MessageResponse> forgotPassword(
            @Valid @RequestBody PasswordResetTokenRequest request
    ) {
        resetPasswordService.createPasswordResetToken(request);
        return ResponseEntity.ok(new MessageResponse("If the email exists, a reset link has been sent") );
    }

    @PostMapping("/password/reset/confirm")
    @Operation(summary = "Reset Password",
            description = "Reset user password using reset token")
    public ResponseEntity<MessageResponse> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request
    ) {
        resetPasswordService.resetPassword(request);
        return ResponseEntity.ok(new MessageResponse("Password has been reset successfully") );
    }

    /*
    // Features to add:
    - GET /auth/me (get current user info)

    // Features:
    - Send verification email on registration
    - POST /auth/verify-email?token=xxx
    - POST /auth/resend-verification

    Database changes needed:
    -- Add to users' table (you have these in comments already!)
    ALTER TABLE users
        ADD COLUMN email_verified_at TIMESTAMPTZ,
        ADD COLUMN verification_token VARCHAR(255),
        ADD COLUMN verification_token_expires_at TIMESTAMPTZ;

    Why: Prevents fake registrations, confirms user owns the email.

     // Endpoints:
    - PUT /users/me/change-password (authenticated user)

    Why: Users forget passwords. This is expected functionality.
     */
}
