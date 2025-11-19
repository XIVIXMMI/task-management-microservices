package com.taskmanagement.userservice.presentation.rest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/users")
public class UserController {

    /*
    - GET /users/{userId}/profile
    - PUT /users/{userId}/profile (update profile)
    - POST /users/{userId}/profile/avatar (upload photo)
    - DELETE /users/{userId}/profile/avatar

    -> User search and pagination
      // Endpoints:
    - GET /users?search=john&page=0&size=20&sort=createdAt,desc
    - GET /users?role=ADMIN
    - GET /users?status=ACTIVE
     */
}
