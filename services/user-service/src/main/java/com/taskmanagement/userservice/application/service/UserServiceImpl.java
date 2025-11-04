package com.taskmanagement.userservice.application.service;

import com.taskmanagement.userservice.domain.repository.UserRepository;

public class UserServiceImpl {

    private final UserRepository userRepository; // Domain interface

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}