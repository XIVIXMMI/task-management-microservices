package com.taskmanagement.userservice.unit.service;

import com.taskmanagement.userservice.application.dto.ChangePasswordRequest;
import com.taskmanagement.userservice.application.service.UserService;
import com.taskmanagement.userservice.application.service.UserServiceImpl;
import com.taskmanagement.userservice.domain.entity.User;
import com.taskmanagement.userservice.domain.exception.InvalidPasswordException;
import com.taskmanagement.userservice.domain.exception.UnauthorizedException;
import com.taskmanagement.userservice.domain.exception.UserNotFoundException;
import com.taskmanagement.userservice.domain.repository.ProfileRepository;
import com.taskmanagement.userservice.domain.repository.UserRepository;
import com.taskmanagement.userservice.infrastructure.security.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ChangePasswordService Unit Test")
public class ChangePasswordServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private UUID userId;
    private User user;
    private CustomUserDetails userDetails;
    private ChangePasswordRequest validRequest;

    @BeforeEach
    void setUp(){
        userId = UUID.randomUUID();

        user = User.builder()
                .id(userId)
                .email("test@example.com")
                .password("$2a$10$encodedOldPassword") // BCrypt Encoded
                .build();

        userDetails = new CustomUserDetails(
                userId,
                "test@example.com",
                "$2a$10$encodedOldPassword",
                null,
                true,
                true,
                true,
                true
        );

        validRequest = new ChangePasswordRequest(
                "OldPassword123",
                "NewPassword456",
                "NewPassword456"
        );

        // Default context setup
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(userDetails);
    }

    @Test
    @DisplayName("Should Throw InvalidPasswordException when old password is incorrect")
    void changePassword_WithIncorrectOldPassword_ThrowException(){
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("OldPassword123", user.getPassword()))
                .thenReturn(false); // assume both passwords don't match

        assertThatThrownBy(() -> userService.changePassword(validRequest))
                .isInstanceOf(InvalidPasswordException.class)
                .hasMessage("Old Password is incorrect");

        verify(userRepository,never()).save(any());
    }

    @Test
    @DisplayName("Should throw InvalidPasswordException when new password equals current password")
    void changePassword_WithSamePassword_ThrowException(){
        ChangePasswordRequest samePasswordRequest = new ChangePasswordRequest(
                "OldPassword123",
                "OldPassword123",
                "OldPassword123"
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("OldPassword123",user.getPassword()))
                .thenReturn(true); // check old password correct
        when(passwordEncoder.matches("OldPassword123",user.getPassword()))
                .thenReturn(true); // check newPassword = oldPassword

        assertThatThrownBy(() -> userService.changePassword(samePasswordRequest))
                .isInstanceOf(InvalidPasswordException.class)
                .hasMessage("New password cannot be the same as current password");

        verify(userRepository,never()).save(any());
    }

    @Test
    @DisplayName("Should throw InvalidPasswordException when confirm password doesn't match")
    void changePassword_WithMismatchedConfirmPassword_ThrowException(){
        ChangePasswordRequest mismatchRequest = new ChangePasswordRequest(
                "OldPassword123",
                "NewPassword123",
                "ConfirmPassword123"
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("OldPassword123",user.getPassword()))
                .thenReturn(true);
        when(passwordEncoder.matches("NewPassword123",user.getPassword()))
                .thenReturn(false);

        assertThatThrownBy(() -> userService.changePassword(mismatchRequest))
                .isInstanceOf(InvalidPasswordException.class)
                .hasMessage("Confirm password does not match");

        verify(userRepository,never()).save(any());
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when user doesn't exist")
    void changePassword_WithNotExistentUser_ThrowException(){
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.changePassword(validRequest))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found");

        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(userRepository,never()).save(any());
    }

    @Test
    @DisplayName("Should throw UnauthorizedException when authentication is null")
    @MockitoSettings(strictness = Strictness.LENIENT)
    void changePassword_WithNullAuthentication_ThrowsException() {
        reset(securityContext);
        when(securityContext.getAuthentication()).thenReturn(null);
        assertThatThrownBy(() -> userService.changePassword(validRequest))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("User no authenticated");

        verify(userRepository, never()).findById(any());
    }

    @Test
    @DisplayName("Should throw UnauthorizedException when authentication is not authenticated")
    @MockitoSettings(strictness = Strictness.LENIENT)
    void changePassword_WithUnauthenticatedUser_ThrowsException(){
        reset(securityContext, authentication);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);
        assertThatThrownBy(() -> userService.changePassword(validRequest))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("User no authenticated");

        verify(userRepository, never()).findById(any());
    }

    @Test
    @DisplayName("Should throw UnauthorizedException when principal is not authenticated")
    @MockitoSettings(strictness = Strictness.LENIENT)
    void changePassword_WithInvalidPrincipal_ThrowsException(){
        reset(securityContext, authentication);
        when(securityContext.getAuthentication()).thenReturn(authentication);  // ADD THIS
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("anonymousUser");
        assertThatThrownBy(() -> userService.changePassword(validRequest))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Invalid authentication principal");

        verify(userRepository, never()).findById(any());
    }
}
