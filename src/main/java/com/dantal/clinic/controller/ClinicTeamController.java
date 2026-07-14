package com.dantal.clinic.controller;

import com.dantal.clinic.service.ClinicTeamService;
import com.dantal.common.dto.ApiResponse;
import com.dantal.platform.dto.CreatePlatformUserRequest;
import com.dantal.platform.dto.PlatformUserResponse;
import com.dantal.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/clinic/team/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Clinic team", description = "Clinic manager staff account provisioning")
public class ClinicTeamController {

    private final ClinicTeamService clinicTeamService;

    @GetMapping
    @Operation(summary = "List clinic team login accounts")
    public ApiResponse<List<PlatformUserResponse>> listUsers(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.ok(clinicTeamService.listTeam(principal));
    }

    @PostMapping
    @Operation(summary = "Create a staff login account for this clinic")
    public ResponseEntity<ApiResponse<PlatformUserResponse>> createUser(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody CreatePlatformUserRequest request) {
        PlatformUserResponse user = clinicTeamService.createTeamMember(principal, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Team member created", user));
    }
}
