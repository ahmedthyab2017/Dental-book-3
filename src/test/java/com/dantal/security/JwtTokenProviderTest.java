package com.dantal.security;

import com.dantal.config.properties.JwtProperties;
import com.dantal.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenProviderTest {

    private JwtTokenProvider provider;

    @BeforeEach
    void setUp() {
        JwtProperties props = new JwtProperties();
        props.setSecret("test-secret-key-minimum-256-bits-long-for-hmac-sha256-algorithm-requirement");
        props.setIssuer("dantal-test");
        props.setAccessTokenExpirationMinutes(15L);

        provider = new JwtTokenProvider(props);
        provider.init();
    }

    @Test
    void generatesAndValidatesAccessToken() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        user.setEmail("admin@dantal.app");
        user.setPasswordHash("encoded-password");
        UserPrincipal principal = new UserPrincipal(user);

        String token = provider.generateAccessToken(principal);

        assertThat(token).isNotBlank();
        assertThat(provider.isTokenValid(token)).isTrue();
        assertThat(provider.getUserIdFromToken(token)).isEqualTo(userId);
    }
}
