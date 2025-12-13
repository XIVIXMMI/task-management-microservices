package com.taskmanagement.userservice.application.service;

import com.taskmanagement.userservice.application.dto.PasswordResetTokenRequest;
import com.taskmanagement.userservice.application.dto.ResetPasswordRequest;
import com.taskmanagement.userservice.application.dto.SendEmailResetRequest;
import com.taskmanagement.userservice.domain.entity.PasswordResetToken;
import com.taskmanagement.userservice.domain.entity.User;
import com.taskmanagement.userservice.domain.exception.EmailNotFoundException;
import com.taskmanagement.userservice.domain.exception.UserNotFoundException;
import com.taskmanagement.userservice.domain.repository.PasswordResetTokenRepository;
import com.taskmanagement.userservice.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResetPasswordServiceImpl implements ResetPasswordService{

    private static final String FRONTEND_DOMAIN_NAME = "my-frontend-app.com";
    private static final int TOKEN_LENGTH = 32;
    private static final int TOKEN_EXPIRATION_MINUTES = 15;

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetRepository;
    private final EmailService emailService;

    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
//    @RateLimiter(name = "passwordResetLimiter", limit = 5, duration = 60)
    public void createPasswordResetToken(PasswordResetTokenRequest request) throws InterruptedException {
        String email = request.email();
        User user = userRepository.findByEmail(email).orElse(null);

        if(user != null) {
            // Delete existing tokens for the user
            passwordResetRepository.deleteByUserId(user.getId());

            String token = generateSecureToken();
            LocalDateTime expiry = LocalDateTime.now().plusMinutes(TOKEN_EXPIRATION_MINUTES);
            PasswordResetToken resetToken = PasswordResetToken.builder()
                    .userId(user.getId())
                    .token(token)
                    .expiryAt(expiry)
                    .used(false)
                    .build();
            passwordResetRepository.save(resetToken);

            String resetLink = "https://" + FRONTEND_DOMAIN_NAME + "/reset-password?token=" + token;
            emailService.sendPasswordResetEmail(
                    new SendEmailResetRequest(email, resetLink)
            );
        } else {
            log.info("Password reset requested for non-existing email: {}", email);
            Thread.sleep(1000); // Simulate processing time
        }
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        String token = request.token();
        PasswordResetToken resetToken = passwordResetRepository.findByToken(token)
                .orElseThrow( () -> new IllegalArgumentException("Invalid password reset token"));
        if( resetToken.isUsed() || resetToken.getExpiryAt().isBefore(LocalDateTime.now())){
            throw new IllegalArgumentException("Password reset token is either used or expired");
        }
        User user = userRepository.findById(resetToken.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User's token not found"));
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);

        resetToken.setUsed(true);
        passwordResetRepository.save(resetToken);
    }

    @Override
    @Scheduled(cron = "0 0 * * * ?") // Runs every hour
    public void cleanupExpiredTokens() {
        passwordResetRepository.deleteByExpiryAtBefore(LocalDateTime.now());
    }

    private String generateSecureToken(){
        SecureRandom token = new SecureRandom();
        byte[] bytes = new byte[TOKEN_LENGTH];
        token.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
