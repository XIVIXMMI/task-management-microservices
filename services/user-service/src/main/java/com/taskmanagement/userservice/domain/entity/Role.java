package com.taskmanagement.userservice.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "roles")
@Getter
@Setter
@SQLDelete(sql = "UPDATE roles SET deleted_at = NOW() WHERE id = ?") // Implement soft delete don't need write more code
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;
    private String description;

    @ManyToMany(mappedBy = "roles")
    private Set<User> users; // (many:many)

    @ElementCollection
    @CollectionTable(name = "role_permissions",
    joinColumns = @JoinColumn(name = "role_id"))
    private Set<RolePermission> permissions; // combine resource and action

    @Column(name = "created_by")
    private UUID createdBy;
    @Column(name = "updated_by")
    private UUID updatedBy;

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
}
