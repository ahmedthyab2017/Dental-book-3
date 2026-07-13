package com.dantal.auth.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TokenPairResponseTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void serializesFrontendTokenContract() throws Exception {
        TokenPairResponse response = TokenPairResponse.of("access-token", "refresh-token");

        JsonNode json = objectMapper.readTree(objectMapper.writeValueAsString(response));

        assertThat(json.has("access")).isFalse();
        assertThat(json.has("refresh")).isFalse();
        assertThat(json.path("tokens").path("access").asText()).isEqualTo("access-token");
        assertThat(json.path("tokens").path("refresh").asText()).isEqualTo("refresh-token");
        assertThat(response.getAccess()).isEqualTo("access-token");
        assertThat(response.getRefresh()).isEqualTo("refresh-token");
    }
}
