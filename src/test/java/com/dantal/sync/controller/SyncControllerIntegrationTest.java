package com.dantal.sync.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class SyncControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private ObjectMapper objectMapper;

    private String accessToken;

    @BeforeEach
    void setUp() throws Exception {
        RestClient client = RestClient.create("http://localhost:" + port);
        String email = "sync-" + System.nanoTime() + "@test.com";
        String registerBody = """
                {
                  "clinicName": "Sync Clinic",
                  "email": "%s",
                  "password": "admin12345",
                  "deviceName": "JUnit"
                }
                """.formatted(email);

        ResponseEntity<String> registerResponse = client.post()
                .uri("/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .body(registerBody)
                .retrieve()
                .toEntity(String.class);

        JsonNode tokens = objectMapper.readTree(registerResponse.getBody()).path("tokens");
        accessToken = tokens.path("access").asText();
        assertThat(accessToken).isNotBlank();
    }

    @Test
    void syncPushPullAndBackupMatchFrontendContract() throws Exception {
        RestClient client = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .build();

        ResponseEntity<String> initialPull = client.get()
                .uri("/v1/sync/pull?sinceVersion=0")
                .retrieve()
                .toEntity(String.class);
        assertThat(initialPull.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        String pushBody = """
                {
                  "ciphertext": "abc",
                  "iv": "iv",
                  "salt": "salt",
                  "alg": "AES-256-GCM",
                  "kdf": { "name": "PBKDF2", "iterations": 210000, "hash": "SHA-256" },
                  "baseVersion": 0,
                  "clientUpdatedAt": "2026-07-08T12:00:00Z"
                }
                """;

        ResponseEntity<String> pushResponse = client.post()
                .uri("/v1/sync/push")
                .contentType(MediaType.APPLICATION_JSON)
                .body(pushBody)
                .retrieve()
                .toEntity(String.class);

        assertThat(pushResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        JsonNode pushJson = objectMapper.readTree(pushResponse.getBody());
        assertThat(pushJson.path("version").asLong()).isEqualTo(1L);

        ResponseEntity<String> pullResponse = client.get()
                .uri("/v1/sync/pull?sinceVersion=0")
                .retrieve()
                .toEntity(String.class);

        assertThat(pullResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        JsonNode pullJson = objectMapper.readTree(pullResponse.getBody());
        assertThat(pullJson.path("version").asLong()).isEqualTo(1L);
        assertThat(pullJson.path("ciphertext").asText()).isEqualTo("abc");

        try {
            client.post()
                    .uri("/v1/sync/push")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(pushBody)
                    .retrieve()
                    .toEntity(String.class);
        } catch (org.springframework.web.client.HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            JsonNode conflictJson = objectMapper.readTree(ex.getResponseBodyAsString());
            assertThat(conflictJson.path("serverVersion").asLong()).isEqualTo(1L);
        }

        String backupBody = """
                {
                  "ciphertext": "backup-data",
                  "iv": "iv2",
                  "salt": "salt2",
                  "label": "manual",
                  "clientUpdatedAt": "2026-07-08T12:00:00Z"
                }
                """;

        ResponseEntity<String> backupResponse = client.post()
                .uri("/v1/backups")
                .contentType(MediaType.APPLICATION_JSON)
                .body(backupBody)
                .retrieve()
                .toEntity(String.class);

        assertThat(backupResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        JsonNode backupJson = objectMapper.readTree(backupResponse.getBody());
        assertThat(backupJson.path("id").asText()).isNotBlank();
        assertThat(backupJson.path("label").asText()).isEqualTo("manual");
    }
}
