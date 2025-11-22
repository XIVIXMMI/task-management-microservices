package com.taskmanagement.userservice.presentation.rest.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/auth")
public class AuthController {



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
