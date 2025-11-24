package com.taskmanagement.userservice.infrastructure.persistence;

import com.taskmanagement.userservice.domain.entity.Role;
import com.taskmanagement.userservice.domain.repository.RoleRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaRoleRepository extends JpaRepository<Role, UUID>, RoleRepository {
}
