package com.taskmanagement.userservice.presentation.rest.controller;

import com.taskmanagement.userservice.application.dto.ChangePasswordRequest;
import com.taskmanagement.userservice.application.dto.MessageResponse;
import com.taskmanagement.userservice.application.dto.UserProfileResponse;
import com.taskmanagement.userservice.application.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/users")
@Tag(name = "User",
        description = "Endpoints for user management")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "Get current user profile",
    description = "Get authenticated user's profile information")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<UserProfileResponse> getCurrentUser() {
        UserProfileResponse response = userService.getCurrentUserProfile();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/change-password")
    @Operation(summary = "Change user password",
    description = "Set new password for authenticated user")
    public ResponseEntity<MessageResponse> changePassword(
            @Valid @RequestBody ChangePasswordRequest request
            ) {
        userService.changePassword(request);
        return ResponseEntity.ok(new MessageResponse("Password change successfully"));
    }

    /*
    - GET /users/{userId}/profile
    - PUT /users/{userId}/profile (update profile)
    - POST /users/{userId}/profile/avatar (upload photo)
    - DELETE /users/{userId}/profile/avatar

    -> User search and pagination
      // Endpoints:
    - GET /users?search=john&page=0&size=20&sort=createdAt,desc
    - GET /users?role=ADMIN
    - GET /users?status=ACTIVE
     */
}
