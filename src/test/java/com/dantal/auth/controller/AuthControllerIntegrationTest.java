package com.dantal.auth.controller;

import com.dantal.auth.dto.LoginRequest;
import com.dantal.auth.dto.RegisterRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class AuthControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private ObjectMapper objectMapper;

    private RestClient client() {
        return RestClient.create("http://localhost:" + port);
    }

    @Test
    void registerLoginAndRefreshMatchFrontendContract() throws Exception {
        RegisterRequest register = new RegisterRequest();
        register.setClinicName("Soran Dental");
        register.setEmail("owner@soran.test");
        register.setPassword("admin12345");
        register.setDeviceName("JUnit");

        ResponseEntity<String> registerResponse = client().post()
                .uri("/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .body(register)
                .retrieve()
                .toEntity(String.class);

        assertThat(registerResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        JsonNode registerBody = objectMapper.readTree(registerResponse.getBody());
        assertThat(registerBody.path("tokens").path("access").asText()).isNotBlank();
        assertThat(registerBody.path("tokens").path("refresh").asText()).isNotBlank();
        assertThat(registerBody.has("success")).isFalse();

        LoginRequest login = new LoginRequest();
        login.setEmail("owner@soran.test");
        login.setPassword("admin12345");

        ResponseEntity<String> loginResponse = client().post()
                .uri("/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(login)
                .retrieve()
                .toEntity(String.class);

        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        JsonNode loginBody = objectMapper.readTree(loginResponse.getBody());
        assertThat(loginBody.path("tokens").path("access").asText()).isNotBlank();

        String refreshToken = registerBody.path("tokens").path("refresh").asText();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String refreshBody = "{\"refreshToken\":\"" + refreshToken + "\"}";

        ResponseEntity<String> refreshResponse = client().post()
                .uri("/v1/auth/refresh")
                .headers(h -> h.addAll(headers))
                .body(refreshBody)
                .retrieve()
                .toEntity(String.class);

        assertThat(refreshResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        JsonNode refreshed = objectMapper.readTree(refreshResponse.getBody());
        assertThat(refreshed.path("tokens").path("access").asText()).isNotBlank();
        assertThat(refreshed.path("tokens").path("refresh").asText()).isNotBlank();
    }

    @Test
    void loginWithBadCredentialsReturnsMessage() throws Exception {
        LoginRequest login = new LoginRequest();
        login.setEmail("missing@test.com");
        login.setPassword("wrongpass1");

        try {
            client().post()
                    .uri("/v1/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(login)
                    .retrieve()
                    .toEntity(String.class);
        } catch (org.springframework.web.client.HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
            JsonNode body = objectMapper.readTree(ex.getResponseBodyAsString());
            assertThat(body.path("message").asText()).isNotBlank();
        }
    }
}
