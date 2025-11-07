package com.taskmanagement.userservice.infrastructure.security;

import com.taskmanagement.userservice.domain.entity.User;
import com.taskmanagement.userservice.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * CustomUserDetailService load user from Database
 * Spring Security invoked this service when authenticate
 */
@Service
@RequiredArgsConstructor

public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * When user login or validate JWT
     * Spring Security invoked this method with username (email)
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found with email " + email
                ));
        return CustomUserDetails.fromUser(user);
    }
}
