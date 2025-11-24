package com.taskmanagement.userservice.domain.repository;

import com.taskmanagement.userservice.domain.entity.Role;

import java.util.Optional;

public interface RoleRepository {

    Optional<Role> findByName(String name);
}
