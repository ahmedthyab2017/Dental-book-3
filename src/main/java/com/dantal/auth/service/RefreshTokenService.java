package com.dantal.auth.service;

import com.dantal.auth.dto.TokenPairResponse;
import com.dantal.auth.entity.RefreshToken;
import com.dantal.auth.repository.RefreshTokenRepository;
import com.dantal.common.exception.UnauthorizedException;
import com.dantal.config.properties.JwtProperties;
import com.dantal.security.JwtTokenProvider;
import com.dantal.security.UserPrincipal;
import com.dantal.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtProperties jwtProperties;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public TokenPairResponse issueTokenPair(User user, String deviceId, String deviceName) {
        UserPrincipal principal = new UserPrincipal(user);
        String accessToken = jwtTokenProvider.generateAccessToken(principal);
        String rawRefresh = generateRawRefreshToken();
        refreshTokenRepository.save(buildRefreshToken(user, rawRefresh, deviceId, deviceName));
        log.info("Issued token pair for user {}", user.getEmail());
        return TokenPairResponse.of(accessToken, rawRefresh);
    }

    @Transactional
    public TokenPairResponse rotateRefreshToken(String rawRefreshToken, String deviceId) {
        String hash = hashToken(rawRefreshToken);
        RefreshToken existing = refreshTokenRepository.findByTokenHashAndRevokedAtIsNull(hash)
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        if (!existing.isActive()) {
            throw new UnauthorizedException("Refresh token expired or revoked");
        }

        String newRawRefresh = generateRawRefreshToken();
        RefreshToken replacement = buildRefreshToken(
                existing.getUser(),
                newRawRefresh,
                deviceId != null ? deviceId : existing.getDeviceId(),
                existing.getDeviceName());
        refreshTokenRepository.save(replacement);

        existing.setRevokedAt(Instant.now());
        existing.setReplacedBy(replacement);
        refreshTokenRepository.save(existing);

        UserPrincipal principal = new UserPrincipal(existing.getUser());
        String accessToken = jwtTokenProvider.generateAccessToken(principal);
        log.info("Rotated refresh token for user {}", existing.getUser().getEmail());
        return TokenPairResponse.of(accessToken, newRawRefresh);
    }

    @Transactional
    public void revokeToken(String rawRefreshToken) {
        if (rawRefreshToken == null || rawRefreshToken.isBlank()) {
            return;
        }
        String hash = hashToken(rawRefreshToken);
        refreshTokenRepository.findByTokenHashAndRevokedAtIsNull(hash).ifPresent(token -> {
            token.setRevokedAt(Instant.now());
            refreshTokenRepository.save(token);
            log.info("Revoked refresh token for user {}", token.getUser().getEmail());
        });
    }

    @Transactional
    public void revokeAllForUser(UUID userId) {
        int count = refreshTokenRepository.revokeAllByUserId(userId, Instant.now());
        log.info("Revoked {} refresh tokens for user {}", count, userId);
    }

    public boolean matchesPassword(String raw, String encoded) {
        return passwordEncoder.matches(raw, encoded);
    }

    public String encodePassword(String raw) {
        return passwordEncoder.encode(raw);
    }

    private RefreshToken buildRefreshToken(User user, String rawToken, String deviceId, String deviceName) {
        RefreshToken entity = new RefreshToken();
        entity.setUser(user);
        entity.setTokenHash(hashToken(rawToken));
        entity.setDeviceId(deviceId);
        entity.setDeviceName(deviceName);
        entity.setExpiresAt(Instant.now().plusSeconds(jwtProperties.getRefreshTokenExpirationDays() * 86400L));
        return entity;
    }

    private String generateRawRefreshToken() {
        byte[] bytes = new byte[48];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    static String hashToken(String raw) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(raw.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
