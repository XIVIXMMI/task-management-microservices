package com.taskmanagement.userservice.application.service;

import com.taskmanagement.userservice.application.dto.UserProfileResponse;

public interface UserService {

    UserProfileResponse getCurrentUserProfile();
}
