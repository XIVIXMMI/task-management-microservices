package com.taskmanagement.userservice.presentation.rest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1")
public class RoleController {

    /*
      // Endpoints:
    - GET /roles (list all roles)
    - POST /roles (create custom role)
    - PUT /roles/{roleId} (update role)
    - POST /roles/{roleId}/permissions (add permissions)
    - DELETE /roles/{roleId}/permissions/{resource}/{action}

  // User role assignment:
    - POST /users/{userId}/roles (assign role to user)
    - DELETE /users/{userId}/roles/{roleId} (remove role)
    - GET /users/{userId}/permissions (all permissions for user)

    Why: Makes your RBAC system actually usable. Right now you have the database structure but no API to manage it.
     */
}
