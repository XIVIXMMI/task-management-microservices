package com.taskmanagement.userservice.application.service;

import com.taskmanagement.userservice.application.dto.*;

import java.sql.Ref;

public interface AuthService {

    LoginResponse login(LoginRequest request);
    RegisterResponse register(RegisterRequest request);
    LoginResponse refreshToken(RefreshTokenRequest request);
    void logout(String token);
    
}