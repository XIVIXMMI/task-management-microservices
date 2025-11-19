package com.taskmanagement.userservice.infrastructure.persistence;

import com.taskmanagement.userservice.domain.entity.Profile;
import com.taskmanagement.userservice.domain.repository.ProfileRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaProfileRepository extends JpaRepository<Profile, UUID>, ProfileRepository {
}
