package com.taskmanagement.userservice.application.dto;

import com.taskmanagement.userservice.domain.entity.Profile;
import com.taskmanagement.userservice.domain.entity.Role;
import com.taskmanagement.userservice.domain.entity.User;
import lombok.Builder;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Builder
public record UserProfileResponse(
        String email,
        String firstName,
        String lastName,
        Set<String> roles
) {
    // static factory method
    public static UserProfileResponse from(User user, Profile profile){
        return UserProfileResponse.builder()
                .email(user.getEmail())
                .firstName(profile.getFirstName())
                .lastName(profile.getLastName())
                .roles(user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toSet()))
                .build();
    }
}
