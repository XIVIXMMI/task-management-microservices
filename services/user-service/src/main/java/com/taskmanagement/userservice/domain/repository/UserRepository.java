package com.taskmanagement.userservice.domain.repository;

import com.taskmanagement.userservice.domain.entity.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    Optional<User> findByEmail(String email);
    User save(User user);
    Optional<User> findById(UUID id);
    void deleteById(UUID id);
    boolean existsByEmail(String email);
}
