package com.taskmanagement.userservice.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String password;

    @ManyToMany
    @JoinTable( name = "user_roles",
    joinColumns = @JoinColumn(name = "user_id"),
    inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles; // (Many:Many)

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;

    /*  use JPA lifecycle callbacks:
        this way can get error if the timeZone is different
        violate DRY must copy and paste in every class
        -> can do baseEntity and enable JPA Auditing
        but can do it later to reduce complexity of project
    */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /*
    Could use those properties for scaling later

    private String phone;
    private LocalDateTime emailVerifiedAt;
    private LocalDateTime phoneVerifiedAt;
    private LocalDateTime lastLoginAt;
    private LocalDateTime lastPasswordResetAt;
    private LocalDateTime lastActivity;

    Or could also split into 'auth' table
    twoFactorEnabled
    authProvider
    authProviderId
    loginAttempts
    isLocked
    lockedUntil

     */
}
