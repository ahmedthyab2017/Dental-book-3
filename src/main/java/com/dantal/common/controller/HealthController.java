package com.dantal.common.controller;

import com.dantal.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/v1/health")
@Tag(name = "Health", description = "Application health checks")
public class HealthController {

    @GetMapping
    @Operation(summary = "Health check", description = "Returns application health status")
    public ApiResponse<Map<String, String>> health() {
        return ApiResponse.ok("Dantal API is running", Map.of(
                "status", "UP",
                "service", "dantal-api",
                "version", "0.1.0"
        ));
    }
}
