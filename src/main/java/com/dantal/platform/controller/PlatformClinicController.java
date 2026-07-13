package com.dantal.platform.controller;

import com.dantal.common.dto.ApiResponse;
import com.dantal.platform.dto.CreatePlatformClinicRequest;
import com.dantal.platform.dto.CreatePlatformClinicResult;
import com.dantal.platform.dto.CreatePlatformUserRequest;
import com.dantal.platform.dto.PlatformClinicDetailResponse;
import com.dantal.platform.dto.PlatformClinicResponse;
import com.dantal.platform.dto.PlatformUserResponse;
import com.dantal.platform.dto.UpdatePlatformClinicRequest;
import com.dantal.platform.dto.UpdatePlatformUserRequest;
import com.dantal.platform.security.PlatformAccess;
import com.dantal.platform.service.PlatformClinicService;
import com.dantal.platform.service.PlatformUserService;
import com.dantal.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/platform/clinics")
@RequiredArgsConstructor
@PlatformAccess
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Platform", description = "Super-admin clinic provisioning")
public class PlatformClinicController {

    private final PlatformClinicService platformClinicService;
    private final PlatformUserService platformUserService;

    @GetMapping
    @Operation(summary = "List all clinics")
    public ApiResponse<List<PlatformClinicResponse>> listClinics() {
        return ApiResponse.ok(platformClinicService.listClinics());
    }

    @GetMapping("/{clinicId}")
    @Operation(summary = "Get clinic details with users")
    public ApiResponse<PlatformClinicDetailResponse> getClinic(@PathVariable UUID clinicId) {
        return ApiResponse.ok(platformUserService.getClinicDetail(clinicId));
    }

    @PostMapping
    @Operation(summary = "Create a clinic and manager account")
    public ResponseEntity<ApiResponse<CreatePlatformClinicResult>> createClinic(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody CreatePlatformClinicRequest request) {
        CreatePlatformClinicResult result = platformClinicService.createClinic(request, principal.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Clinic created. Share manager credentials with the clinic owner.", result));
    }

    @PatchMapping("/{clinicId}")
    @Operation(summary = "Update clinic status")
    public ApiResponse<PlatformClinicResponse> updateClinic(
            @PathVariable UUID clinicId,
            @RequestBody UpdatePlatformClinicRequest request) {
        return ApiResponse.ok(platformClinicService.updateClinic(clinicId, request));
    }

    @GetMapping("/{clinicId}/users")
    @Operation(summary = "List clinic users")
    public ApiResponse<List<PlatformUserResponse>> listUsers(@PathVariable UUID clinicId) {
        return ApiResponse.ok(platformUserService.listUsers(clinicId));
    }

    @PostMapping("/{clinicId}/users")
    @Operation(summary = "Create clinic user with role")
    public ResponseEntity<ApiResponse<PlatformUserResponse>> createUser(
            @PathVariable UUID clinicId,
            @Valid @RequestBody CreatePlatformUserRequest request) {
        PlatformUserResponse user = platformUserService.createUser(clinicId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("User created", user));
    }

    @PatchMapping("/{clinicId}/users/{userId}")
    @Operation(summary = "Update clinic user role, status, or password")
    public ApiResponse<PlatformUserResponse> updateUser(
            @PathVariable UUID clinicId,
            @PathVariable UUID userId,
            @Valid @RequestBody UpdatePlatformUserRequest request) {
        return ApiResponse.ok(platformUserService.updateUser(clinicId, userId, request));
    }

    @DeleteMapping("/{clinicId}/users/{userId}")
    @Operation(summary = "Delete clinic user")
    public ApiResponse<Void> deleteUser(@PathVariable UUID clinicId, @PathVariable UUID userId) {
        platformUserService.deleteUser(clinicId, userId);
        return ApiResponse.ok("User deleted", null);
    }
}
