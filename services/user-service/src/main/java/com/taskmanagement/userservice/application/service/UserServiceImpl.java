package com.taskmanagement.userservice.application.service;

import com.taskmanagement.userservice.application.dto.ChangePasswordRequest;
import com.taskmanagement.userservice.application.dto.UpdateProfileRequest;
import com.taskmanagement.userservice.application.dto.UserProfileResponse;
import com.taskmanagement.userservice.domain.entity.Profile;
import com.taskmanagement.userservice.domain.entity.User;
import com.taskmanagement.userservice.domain.exception.InvalidPasswordException;
import com.taskmanagement.userservice.domain.exception.ProfileNotFoundException;
import com.taskmanagement.userservice.domain.exception.UnauthorizedException;
import com.taskmanagement.userservice.domain.exception.UserNotFoundException;
import com.taskmanagement.userservice.domain.repository.ProfileRepository;
import com.taskmanagement.userservice.domain.repository.UserRepository;
import com.taskmanagement.userservice.infrastructure.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository; // Domain interface
    private final ProfileRepository profileRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public UserProfileResponse getCurrentUserProfile() {
        UUID userId = getUuid();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ProfileNotFoundException("Profile not found"));
        return UserProfileResponse.from(user,profile);
    }

    /**
     * Change user password. Old tokens remain valid until expiration (stateless JWT).
     * For token invalidation strategy, see docs/plan/token-invalidation-strategy.md
     */
    @Override
    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        UUID uuid = getUuid();
        User user = userRepository.findById(uuid)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        if(!passwordEncoder.matches(request.oldPassword(), user.getPassword())){
            throw new InvalidPasswordException("Old Password is incorrect");
        }
        if(passwordEncoder.matches(request.newPassword(), user.getPassword())){
            throw new InvalidPasswordException("New password cannot be the same as current password");
        }
        if(!request.newPassword().equals(request.confirmPassword())){
            throw new InvalidPasswordException("Confirm password does not match");
        }
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    @Override
    public UserProfileResponse getUserProfileById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException("Profile not found"));
        return UserProfileResponse.from(user,profile);
    }

    @Override
    @Transactional
    public UserProfileResponse updateUserProfile(UpdateProfileRequest request) {
        UUID userId = getUuid();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException("Profile not found"));
        updateProfileFields(profile,request);
        profileRepository.save(profile);
        return UserProfileResponse.from(user,profile);
    }

    @Override
    public UserProfileResponse updateUserProfileById(UUID userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException("Profile not found"));
        updateProfileFields(profile,request);
        profileRepository.save(profile);
        return UserProfileResponse.from(user,profile);
    }


    /*
    ====== UTILITY METHODS ======
     */
    private static UUID getUuid() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // defensive programming
        if( auth == null || !auth.isAuthenticated()){
            throw new UnauthorizedException("User not authenticated");
        }
        Object principal = auth.getPrincipal();
        // except edge case anonymous user
        if(!(principal instanceof CustomUserDetails userDetails)){
            throw new UnauthorizedException("Invalid authentication principal");
        }
        return userDetails.getId();
    }

    private void updateProfileFields(Profile profile, UpdateProfileRequest request){
        if(request.firstName() != null) profile.setFirstName(request.firstName());
        if(request.lastName() != null) profile.setLastName(request.lastName());
        if(request.dateOfBirth() != null) profile.setDateOfBirth(request.dateOfBirth());
        if(request.gender() != null) profile.setGender(request.gender());
        if(request.bio() != null) profile.setBio(request.bio());
    }
}