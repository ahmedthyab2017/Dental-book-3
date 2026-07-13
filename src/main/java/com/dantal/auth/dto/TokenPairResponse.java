package com.dantal.auth.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Token pair returned by auth endpoints.
 * Matches the frontend contract: { tokens: { access, refresh } }
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenPairResponse {

    private Tokens tokens;

    public static TokenPairResponse of(String access, String refresh) {
        return TokenPairResponse.builder()
                .tokens(new Tokens(access, refresh))
                .build();
    }

    @JsonIgnore
    public String getAccess() {
        return tokens != null ? tokens.getAccess() : null;
    }

    @JsonIgnore
    public String getRefresh() {
        return tokens != null ? tokens.getRefresh() : null;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Tokens {
        private String access;
        private String refresh;
    }
}
