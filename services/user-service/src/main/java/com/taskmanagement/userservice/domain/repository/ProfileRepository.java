package com.taskmanagement.userservice.domain.repository;

import com.taskmanagement.userservice.domain.entity.Profile;

import java.util.Optional;
import java.util.UUID;

public interface ProfileRepository {

    Profile save(Profile profile);
    Optional<Profile> findByUserId(UUID userId);
}
