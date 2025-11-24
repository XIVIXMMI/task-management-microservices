package com.taskmanagement.userservice.application.service;

import com.taskmanagement.userservice.application.dto.*;
import com.taskmanagement.userservice.domain.entity.Profile;
import com.taskmanagement.userservice.domain.entity.Role;
import com.taskmanagement.userservice.domain.entity.User;
import com.taskmanagement.userservice.domain.exception.InvalidRefreshTokenException;
import com.taskmanagement.userservice.domain.exception.EmailExistedException;
import com.taskmanagement.userservice.domain.exception.RoleNotFoundException;
import com.taskmanagement.userservice.domain.repository.ProfileRepository;
import com.taskmanagement.userservice.domain.repository.RoleRepository;
import com.taskmanagement.userservice.domain.repository.UserRepository;
import com.taskmanagement.userservice.infrastructure.security.CustomUserDetails;
import com.taskmanagement.userservice.infrastructure.security.CustomUserDetailsService;
import com.taskmanagement.userservice.application.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final RoleRepository roleRepository;

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    private static final Long EXPIRED_TIME = 900L; // Minute 15
    private static final String TOKEN_TYPE = "Bearer";

    @Override
    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String accessToken = jwtUtil.generateToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiredIn(EXPIRED_TIME)
                .tokenType(TOKEN_TYPE)
                .email(userDetails.getEmail())
                .userId(UUID.fromString(userDetails.getId().toString()))
                .build();
    }

    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        if(userRepository.existsByEmail(request.getEmail())){
            throw new EmailExistedException("Email is already existed!!!");
        }

        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RoleNotFoundException("Default role USER not found"));

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(Set.of(userRole))
                .build();

        User savedUser = userRepository.save(user);

        Profile profile = Profile.builder()
                .userId(savedUser.getId())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .build();

        Profile savedProfile = profileRepository.save(profile);

        return RegisterResponse.from(savedUser,savedProfile);
    }

    @Override
    public LoginResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.refreshToken();

        if(!jwtUtil.validateToken(refreshToken)){
            throw new InvalidRefreshTokenException("Token is invalid or expired");
        }

        if(!jwtUtil.isRefreshToken(refreshToken)){
            throw new InvalidRefreshTokenException("Token is not a refresh token");
        }

        String username = jwtUtil.extractUsername(refreshToken);

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
        String newAccessToken = jwtUtil.generateToken(customUserDetails);
        String newRefreshToken = jwtUtil.generateRefreshToken(customUserDetails);

        return LoginResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType(TOKEN_TYPE)
                .expiredIn(EXPIRED_TIME)
                .email(customUserDetails.getEmail())
                .userId(customUserDetails.getId())
                .build();
    }

    @Override
    public void logout(String token) {
        // may be can add token to the blacklist
        log.info("User logged out - token removed client-side");
    }
}
