package com.taskmanagement.userservice.unit.service;

import com.taskmanagement.userservice.application.dto.PasswordResetTokenRequest;
import com.taskmanagement.userservice.application.dto.ResetPasswordRequest;
import com.taskmanagement.userservice.application.service.EmailService;
import com.taskmanagement.userservice.application.service.ResetPasswordServiceImpl;
import com.taskmanagement.userservice.domain.entity.PasswordResetToken;
import com.taskmanagement.userservice.domain.entity.User;
import com.taskmanagement.userservice.domain.exception.ResetTokenExpiredOrUsedException;
import com.taskmanagement.userservice.domain.exception.ResetTokenNotFoundException;
import com.taskmanagement.userservice.domain.repository.PasswordResetTokenRepository;
import com.taskmanagement.userservice.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ResetPasswordService Unit Tests")
public class ResetPasswordServiceTest {

    // Set up mocks and test data
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks // Auto-inject @Mock into this service
    private ResetPasswordServiceImpl resetPasswordService;

    // Test data
    private User testUser;
    private PasswordResetToken testToken;
    private PasswordResetToken testExpiredToken;
    private PasswordResetToken testUsedToken;
    private UUID userId = UUID.randomUUID();

    @BeforeEach
        // run before each test
    void setUp() {
        testUser = User.builder()
                .id(userId)
                .email("test@example.com")
                .password("oldPasswordHash")
                .build();

        testToken = PasswordResetToken.builder()
                .token("valid-token")
                .userId(userId)
                .expiryAt(LocalDateTime.now().plusMinutes(15))
                .used(false)
                .build();

        testExpiredToken = PasswordResetToken.builder()
                .token("expired-token")
                .userId(userId)
                .expiryAt(LocalDateTime.now().minusMinutes(1))
                .used(false)
                .build();

        testUsedToken = PasswordResetToken.builder()
                .token("used-token")
                .userId(userId)
                .expiryAt(LocalDateTime.now().plusMinutes(15))
                .used(true)
                .build();
    }

    // Test methods would go here, Pattern: arrange-act-assert (AAA)

    @Test
    @DisplayName("Should send email when email exists")
    void createPasswordResetToken_WithValidEmail_SendsEmail(){
        // Arrange - prepare mocks and data
        PasswordResetTokenRequest request = new PasswordResetTokenRequest("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // Act - implement the action to test
        resetPasswordService.createPasswordResetToken(request);

        // Assert - verify the expected outcome
        verify(emailService).sendPasswordResetEmail(any());
        verify(passwordResetTokenRepository).save(any());
    }

    @Test
    @DisplayName("Should hash password before saving")
    void resetPassword_HashesPassword(){
        // Arrange
        String rawPassword = "Abc@1234";
        String hashed = "hashed_password";
        ResetPasswordRequest request = new ResetPasswordRequest("token", rawPassword);

        when(passwordResetTokenRepository.findByToken("token")).thenReturn(Optional.of(testToken));
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode(rawPassword)).thenReturn(hashed);

        // Act
        resetPasswordService.resetPassword(request);

        // Assert
        verify(passwordEncoder).encode(rawPassword); // Verify encoding called
        assertThat(testUser.getPassword())
                .isEqualTo(hashed) // Hashed Value
                .isNotEqualTo(rawPassword); // Not plain text
    }

    @Test
    @DisplayName("Should delete old tokens before creating new one")
    void createPasswordResetToken_DeletedOldTokens_BeforeCreatingNew() {
        // Arrange
        PasswordResetTokenRequest request = new PasswordResetTokenRequest("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // Act
        resetPasswordService.createPasswordResetToken(request);

        // Assert - verify deleteByUserId is called in order
        //verify(passwordResetTokenRepository).deleteByUserId(userId);

        // use this way the test will check if the logic is correct (delete old one first then save new one)
        InOrder inOrder = inOrder(passwordResetTokenRepository);
        inOrder.verify(passwordResetTokenRepository).deleteByUserId(userId);
        inOrder.verify(passwordResetTokenRepository).save(any(PasswordResetToken.class));
    }

    @Test
    @DisplayName("Should throw exception when token is expired")
    void resetPassword_WithExpiredToken_ThrowsException() {
        ResetPasswordRequest request = new ResetPasswordRequest("expired-token","Abc@1234");
        when(passwordResetTokenRepository.findByToken("expired-token")).thenReturn(Optional.of(testExpiredToken));

        assertThatThrownBy(() -> resetPasswordService.resetPassword(request))
                .isInstanceOf(ResetTokenExpiredOrUsedException.class)
                .hasMessageContaining("expired");
    }

    @Test
    @DisplayName("Should throw exception when token is used")
    void resetPassword_WithUsedToken_ThrowsException() {
        ResetPasswordRequest request = new ResetPasswordRequest("used-token","Abc@1234");
        when(passwordResetTokenRepository.findByToken("used-token")).thenReturn(Optional.of(testUsedToken));

        assertThatThrownBy(() -> resetPasswordService.resetPassword(request))
                .isInstanceOf(ResetTokenExpiredOrUsedException.class)
                .hasMessageContaining("used");
    }

    @Test
    @DisplayName("Should throw exception when token is invalid")
    void resetPassword_WithInvalidToken_ThrowsException() {
        ResetPasswordRequest request = new ResetPasswordRequest("invalid-random-token","Abc@1234");
        when(passwordResetTokenRepository.findByToken("invalid-random-token")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> resetPasswordService.resetPassword(request))
                .isInstanceOf(ResetTokenNotFoundException.class)
                .hasMessageContaining("Invalid");
    }

    @Test
    @DisplayName("Should not throw exception when email does not exist")
    void createPasswordResetToken_WithNonExistentEmail_NoException() {
        PasswordResetTokenRequest request = new PasswordResetTokenRequest("non.existing@email.com");

        when(userRepository.findByEmail("non.existing@email.com")).thenReturn(Optional.empty());

        assertThatCode(() -> resetPasswordService.createPasswordResetToken(request))
                .doesNotThrowAnyException();

        verify(passwordResetTokenRepository, never()).save(any());
        verify(emailService, never()).sendPasswordResetEmail(any());
    }

    @Test
    @DisplayName("Should mark token as used after successful reset")
    void resetPassword_MarksTokenAsUsed_AfterSuccess() {

        String rawPassword = "Abc@1234";
        String hashed = "hashed_password";
        ResetPasswordRequest request = new ResetPasswordRequest("valid-token", rawPassword);

        when(passwordResetTokenRepository.findByToken("valid-token"))
                .thenReturn(Optional.of(testToken));
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode(rawPassword))
                .thenReturn(hashed);

        resetPasswordService.resetPassword(request);

        assertThat(testToken.isUsed()).isTrue(); // Token is mark as used
        verify(passwordResetTokenRepository).save(testToken); // and saved
    }
}
