package com.taskmanagement.userservice.application.dto;

import com.taskmanagement.userservice.domain.entity.Gender;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdateProfileRequest(
        @Size(min  = 1, max = 50, message = "First name must be between 1 and 50 characters")
        String firstName,
        @Size(min = 1, max = 50, message = "Last name must be between 1 and 50 characters")
        String lastName,
        @Past(message = "Date of birth must be in the past")
        LocalDate dateOfBirth,
        Gender gender,
        @Size(max = 500, message = "Bio must not exceed 500 characters")
        String bio
) {
}
