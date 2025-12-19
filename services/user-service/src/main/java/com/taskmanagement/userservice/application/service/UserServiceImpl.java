package com.taskmanagement.userservice.application.service;

import com.taskmanagement.userservice.application.dto.UserProfileResponse;
import com.taskmanagement.userservice.domain.entity.Profile;
import com.taskmanagement.userservice.domain.entity.User;
import com.taskmanagement.userservice.domain.exception.ProfileNotFoundException;
import com.taskmanagement.userservice.domain.exception.UserNotFoundException;
import com.taskmanagement.userservice.domain.repository.ProfileRepository;
import com.taskmanagement.userservice.domain.repository.UserRepository;
import com.taskmanagement.userservice.infrastructure.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository; // Domain interface
    private final ProfileRepository profileRepository;

    @Override
    public UserProfileResponse getCurrentUserProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        UUID userId = userDetails.getId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ProfileNotFoundException("Profile not found"));
        return UserProfileResponse.from(user,profile);
    }
}