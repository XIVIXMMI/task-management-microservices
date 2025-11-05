package com.taskmanagement.userservice.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Entity
@Table(name = "profiles")
@Getter
@Setter
@SQLDelete(sql = "UPDATE profiles SET deleted_at = NOW() WHERE id = ?") // Implement soft delete don't need write more code
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    private String firstName;
    private String lastName;
    private String photoUrl;
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    private Gender gender;
    private String bio;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    /*  use JPA lifecycle callbacks:
        this way can get error if the timeZone is different
        violate DRY must copy and paste in every class
        -> can do baseEntity and enable JPA Auditing
        but can do it later to reduce complexity of project
    */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now(ZoneId.of("UTC"));
        updatedAt = LocalDateTime.now(ZoneId.of("UTC"));
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now(ZoneId.of("UTC"));
    }

    /*
    Could use for scaling later

    timezone
    language
    address

     */
}
