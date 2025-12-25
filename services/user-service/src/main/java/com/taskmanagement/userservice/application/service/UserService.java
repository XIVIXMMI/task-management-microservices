package com.taskmanagement.userservice.application.service;

import com.taskmanagement.userservice.application.dto.ChangePasswordRequest;
import com.taskmanagement.userservice.application.dto.UpdateProfileRequest;
import com.taskmanagement.userservice.application.dto.UserProfileResponse;

import java.util.UUID;

public interface UserService {

    UserProfileResponse getCurrentUserProfile();
    void changePassword(ChangePasswordRequest request);
    UserProfileResponse getUserProfileById(UUID userId);
    UserProfileResponse updateUserProfile(UpdateProfileRequest request);
    UserProfileResponse updateUserProfileById(UUID userId, UpdateProfileRequest request);
}
