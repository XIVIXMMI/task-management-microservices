package com.taskmanagement.userservice.presentation.rest.controller;

import com.taskmanagement.userservice.application.dto.LoginRequest;
import com.taskmanagement.userservice.application.dto.LoginResponse;
import com.taskmanagement.userservice.application.dto.RegisterRequest;
import com.taskmanagement.userservice.application.dto.RegisterResponse;
import com.taskmanagement.userservice.application.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/auth")
@Tag(name = "Authentication",
        description = "Endpoints for user authentication and authorization")
public class AuthController {

    private final AuthService authService;

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

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/users/{id}")
    public void deleteUser(@PathVariable UUID id) {
        // Only admins can delete users
    }

    /*
    // Features to add:
    - POST /auth/register (user registration)
    - POST /auth/login (email + password → JWT token)
    - POST /auth/logout (invalidate token)
    - POST /auth/refresh-token (extend session)
    - GET /auth/me (get current user info)



    // Features:
    - Send verification email on registration
    - POST /auth/verify-email?token=xxx
    - POST /auth/resend-verification

    Database changes needed:
    -- Add to users table (you have these in comments already!)
    ALTER TABLE users
        ADD COLUMN email_verified_at TIMESTAMPTZ,
        ADD COLUMN verification_token VARCHAR(255),
        ADD COLUMN verification_token_expires_at TIMESTAMPTZ;

    Why: Prevents fake registrations, confirms user owns the email.

     // Endpoints:
    - Reset password
    - POST /auth/forgot-password (send reset email)
    - POST /auth/reset-password (with token)
    - PUT /users/me/change-password (authenticated user)

    Why: Users forget passwords. This is expected functionality.
     */
}
