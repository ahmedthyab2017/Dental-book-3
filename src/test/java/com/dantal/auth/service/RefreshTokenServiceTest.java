package com.dantal.auth.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RefreshTokenServiceTest {

    @Test
    void hashTokenIsDeterministic() {
        String raw = "sample-refresh-token-value";
        assertThat(RefreshTokenService.hashToken(raw))
                .isEqualTo(RefreshTokenService.hashToken(raw));
        assertThat(RefreshTokenService.hashToken(raw)).hasSize(64);
    }
}
