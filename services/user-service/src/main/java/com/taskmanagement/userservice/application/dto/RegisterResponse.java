package com.taskmanagement.userservice.application.dto;

import com.taskmanagement.userservice.domain.entity.Gender;
import com.taskmanagement.userservice.domain.entity.Profile;
import com.taskmanagement.userservice.domain.entity.Role;
import com.taskmanagement.userservice.domain.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterResponse {

    private UUID id;
    private String email;
    private Set<String> roles; // Should using String not entity to response client
    private LocalDateTime createdAt;

    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private Gender gender;

    public static RegisterResponse from(User user, Profile profile){
        RegisterResponseBuilder builder = RegisterResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt());

        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            builder.roles(user.getRoles().stream()
                    .map(Role::getName)
                    .collect(Collectors.toSet()));
        }

        if(profile != null){
            builder.firstName(profile.getFirstName())
                    .lastName(profile.getLastName())
                    .dateOfBirth(profile.getDateOfBirth())
                    .gender(profile.getGender());
        }

        return builder.build();
    }

    public static RegisterResponse from(User user){
        return from(user, null);
    }
}