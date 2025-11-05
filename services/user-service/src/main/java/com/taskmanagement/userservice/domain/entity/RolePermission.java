package com.taskmanagement.userservice.domain.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor // JPA need a non-param constructor
public class RolePermission {

    @Enumerated(EnumType.STRING)
    private Resource resource;
    @Enumerated(EnumType.STRING)
    private Action action;
}
