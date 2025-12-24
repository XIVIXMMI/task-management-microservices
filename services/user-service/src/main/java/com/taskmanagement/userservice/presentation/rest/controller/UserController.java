package com.taskmanagement.userservice.presentation.rest.controller;

import com.taskmanagement.userservice.application.dto.ChangePasswordRequest;
import com.taskmanagement.userservice.application.dto.MessageResponse;
import com.taskmanagement.userservice.application.dto.UpdateProfileRequest;
import com.taskmanagement.userservice.application.dto.UserProfileResponse;
import com.taskmanagement.userservice.application.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

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

    @PutMapping("/me/password")
    @Operation(summary = "Change user password",
    description = "Change password for authenticated user")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<MessageResponse> changePassword(
            @Valid @RequestBody ChangePasswordRequest request
            ) {
        userService.changePassword(request);
        return ResponseEntity.ok(new MessageResponse("Password changed successfully"));
    }

    @GetMapping("/{userId}/profile")
    @Operation(summary = "Get user profile by ID",
    description = "Get user profile by specified user's ID ")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<UserProfileResponse> getUserProfileById(
            @PathVariable UUID userId){
        UserProfileResponse response = userService.getUserProfileById(userId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/me/update")
    @Operation(summary = "Update current user profile",
    description = "Update profile for authenticated user")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<UserProfileResponse> updateCurrentUserProfile(
            @Valid @RequestBody UpdateProfileRequest request
            ) {
        UserProfileResponse response = userService.updateUserProfile(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{userId}/profile/update")
    @Operation(summary = "Update user profile by ID",
    description = "Update user profile by specified ID")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<UserProfileResponse> updateUserProfileById(
            @PathVariable UUID userId,
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        UserProfileResponse response = userService.updateUserProfileById(userId,request);
        return ResponseEntity.ok(response);
    }


    /*
    - POST /users/{userId}/profile/avatar (upload photo)
    - DELETE /users/{userId}/profile/avatar

    -> User search and pagination
      // Endpoints:
    - GET /users?search=john&page=0&size=20&sort=createdAt,desc
    - GET /users?role=ADMIN
    - GET /users?status=ACTIVE
     */
}
