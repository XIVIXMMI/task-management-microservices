package com.taskmanagement.userservice.application.service;

import com.taskmanagement.userservice.application.dto.ChangePasswordRequest;
import com.taskmanagement.userservice.application.dto.UserProfileResponse;

import java.util.UUID;

public interface UserService {

    UserProfileResponse getCurrentUserProfile();
    void changePassword(ChangePasswordRequest request);
    UserProfileResponse getUserProfileById(UUID uerId);

}
