package com.dantal.auth.service;

import com.dantal.auth.dto.ChangePasswordRequest;
import com.dantal.auth.dto.ForgotPasswordRequest;
import com.dantal.auth.dto.LoginRequest;
import com.dantal.auth.dto.LogoutRequest;
import com.dantal.auth.dto.RefreshRequest;
import com.dantal.auth.dto.RegisterRequest;
import com.dantal.auth.dto.ResetPasswordRequest;
import com.dantal.auth.dto.TokenPairResponse;
import com.dantal.auth.dto.UserResponse;
import com.dantal.auth.entity.PasswordResetToken;
import com.dantal.auth.repository.PasswordResetTokenRepository;
import com.dantal.clinic.entity.Clinic;
import com.dantal.clinic.repository.ClinicRepository;
import com.dantal.common.exception.BusinessException;
import com.dantal.license.service.LicenseService;
import com.dantal.mail.MailService;
import com.dantal.common.exception.ResourceNotFoundException;
import com.dantal.common.exception.UnauthorizedException;
import com.dantal.user.entity.Role;
import com.dantal.user.entity.RoleName;
import com.dantal.user.entity.User;
import com.dantal.user.repository.RoleRepository;
import com.dantal.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Locale;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final Duration LOCKOUT_DURATION = Duration.ofMinutes(15);
    private static final Duration RESET_TOKEN_TTL = Duration.ofMinutes(30);
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ClinicRepository clinicRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final RefreshTokenService refreshTokenService;
    private final MailService mailService;
    private final LicenseService licenseService;

    @Transactional
    public TokenPairResponse register(RegisterRequest request) {
        String email = request.getEmail().toLowerCase(Locale.ROOT);
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new BusinessException("Email is already registered", HttpStatus.CONFLICT, "EMAIL_TAKEN");
        }

        Role adminRole = roleRepository.findByName(RoleName.ADMIN)
                .orElseThrow(() -> new IllegalStateException("ADMIN role missing from database"));

        Clinic clinic = new Clinic();
        clinic.setName(request.getClinicName().trim());
        clinic.setSlug(generateUniqueSlug(request.getClinicName()));
        clinicRepository.save(clinic);

        User user = new User();
        user.setClinic(clinic);
        user.setEmail(email);
        user.setPasswordHash(refreshTokenService.encodePassword(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());
        user.setEmailVerified(true);
        user.getRoles().add(adminRole);
        userRepository.save(user);

        licenseService.activateDefaultForClinic(clinic.getId(), user);

        log.info("Registered clinic '{}' with owner {}", clinic.getName(), user.getEmail());
        return refreshTokenService.issueTokenPair(user, request.getDeviceId(), request.getDeviceName());
    }

    @Transactional
    public TokenPairResponse login(LoginRequest request) {
        User user = userRepository.findByEmailIgnoreCase(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (user.getLockedUntil() != null && user.getLockedUntil().isAfter(Instant.now())) {
            throw new UnauthorizedException("Account is temporarily locked due to repeated failed logins");
        }

        if (!user.isActive()) {
            throw new UnauthorizedException("Account is disabled");
        }

        if (!refreshTokenService.matchesPassword(request.getPassword(), user.getPasswordHash())) {
            registerFailedAttempt(user);
            throw new UnauthorizedException("Invalid email or password");
        }

        user.setFailedAttempts((short) 0);
        user.setLockedUntil(null);
        userRepository.save(user);

        log.info("User {} logged in", user.getEmail());
        return refreshTokenService.issueTokenPair(user, request.getDeviceId(), request.getDeviceName());
    }

    @Transactional
    public TokenPairResponse refresh(RefreshRequest request) {
        return refreshTokenService.rotateRefreshToken(request.getRefreshToken(), request.getDeviceId());
    }

    @Transactional
    public void logout(LogoutRequest request) {
        refreshTokenService.revokeToken(request.getRefreshToken());
    }

    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        return UserResponse.from(user);
    }

    @Transactional
    public void changePassword(UUID userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        if (!refreshTokenService.matchesPassword(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException("Current password is incorrect");
        }

        user.setPasswordHash(refreshTokenService.encodePassword(request.getNewPassword()));
        userRepository.save(user);
        refreshTokenService.revokeAllForUser(userId);
        log.info("User {} changed password", user.getEmail());
    }

    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        userRepository.findByEmailIgnoreCase(request.getEmail()).ifPresent(user -> {
            String rawToken = generateRawToken();
            PasswordResetToken token = new PasswordResetToken();
            token.setUser(user);
            token.setTokenHash(hashToken(rawToken));
            token.setExpiresAt(Instant.now().plus(RESET_TOKEN_TTL));
            passwordResetTokenRepository.save(token);
            mailService.sendPasswordReset(user.getEmail(), rawToken);
            log.info("Password reset token issued for user {}", user.getEmail());
        });
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        String hash = hashToken(request.getToken());
        PasswordResetToken token = passwordResetTokenRepository.findByTokenHashAndUsedAtIsNull(hash)
                .orElseThrow(() -> new UnauthorizedException("Invalid or expired reset token"));

        if (!token.isActive()) {
            throw new UnauthorizedException("Invalid or expired reset token");
        }

        User user = token.getUser();
        user.setPasswordHash(refreshTokenService.encodePassword(request.getNewPassword()));
        userRepository.save(user);

        token.setUsedAt(Instant.now());
        passwordResetTokenRepository.save(token);

        refreshTokenService.revokeAllForUser(user.getId());
        log.info("Password reset completed for user {}", user.getEmail());
    }

    private String generateUniqueSlug(String clinicName) {
        String base = clinicName.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9\\u0600-\\u06FF]+", "-")
                .replaceAll("^-+|-+$", "");
        if (base.isBlank()) {
            base = "clinic";
        }
        String slug = base;
        int attempt = 0;
        while (clinicRepository.existsBySlug(slug)) {
            attempt++;
            slug = base + "-" + attempt;
        }
        return slug;
    }

    private void registerFailedAttempt(User user) {
        short attempts = (short) (user.getFailedAttempts() + 1);
        user.setFailedAttempts(attempts);
        if (attempts >= MAX_FAILED_ATTEMPTS) {
            user.setLockedUntil(Instant.now().plus(LOCKOUT_DURATION));
            log.warn("User {} locked after {} failed login attempts", user.getEmail(), attempts);
        }
        userRepository.save(user);
    }

    private static String generateRawToken() {
        byte[] bytes = new byte[48];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private static String hashToken(String raw) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(raw.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
