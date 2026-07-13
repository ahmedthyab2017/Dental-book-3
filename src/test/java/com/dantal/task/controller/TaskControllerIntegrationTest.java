package com.dantal.task.controller;

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
class TaskControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private ObjectMapper objectMapper;

    private RestClient client;
    private String accessToken;

    @BeforeEach
    void setUp() throws Exception {
        RestClient bootstrap = RestClient.create("http://localhost:" + port);
        String email = "tasks-" + System.nanoTime() + "@test.com";
        String registerBody = """
                {
                  "clinicName": "Tasks Clinic",
                  "email": "%s",
                  "password": "admin12345",
                  "deviceName": "JUnit"
                }
                """.formatted(email);

        ResponseEntity<String> registerResponse = bootstrap.post()
                .uri("/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .body(registerBody)
                .retrieve()
                .toEntity(String.class);

        accessToken = objectMapper.readTree(registerResponse.getBody())
                .path("tokens").path("access").asText();

        client = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .build();
    }

    @Test
    void taskCrudMatchesFrontendContract() throws Exception {
        ResponseEntity<String> listEmpty = client.get()
                .uri("/v1/tasks")
                .retrieve()
                .toEntity(String.class);
        assertThat(listEmpty.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(objectMapper.readTree(listEmpty.getBody()).path("tasks").size()).isZero();

        String createBody = """
                {
                  "title": "Follow up patient",
                  "note": "Call tomorrow",
                  "assigneeStaffId": "s1",
                  "assigneeName": "Sara",
                  "assignerName": "Dr Ali"
                }
                """;

        ResponseEntity<String> createResponse = client.post()
                .uri("/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .body(createBody)
                .retrieve()
                .toEntity(String.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        JsonNode created = objectMapper.readTree(createResponse.getBody()).path("task");
        String taskId = created.path("id").asText();
        assertThat(taskId).isNotBlank();
        assertThat(created.path("status").asText()).isEqualTo("open");

        ResponseEntity<String> listResponse = client.get()
                .uri("/v1/tasks")
                .retrieve()
                .toEntity(String.class);
        assertThat(objectMapper.readTree(listResponse.getBody()).path("tasks").size()).isEqualTo(1);

        String patchBody = "{\"status\":\"done\"}";
        ResponseEntity<String> patchResponse = client.patch()
                .uri("/v1/tasks/" + taskId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(patchBody)
                .retrieve()
                .toEntity(String.class);
        assertThat(objectMapper.readTree(patchResponse.getBody()).path("task").path("status").asText())
                .isEqualTo("done");

        ResponseEntity<Void> deleteResponse = client.delete()
                .uri("/v1/tasks/" + taskId)
                .retrieve()
                .toBodilessEntity();
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}
