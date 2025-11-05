package com.taskmanagement.userservice.infrastructure.persistence;

import com.taskmanagement.userservice.domain.entity.User;
import com.taskmanagement.userservice.domain.repository.UserRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface JpaUserRepository extends JpaRepository<User, UUID>, UserRepository {
}
