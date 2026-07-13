package com.dantal.platform.controller;

import com.dantal.common.dto.ApiResponse;
import com.dantal.platform.dto.CreatePlatformClinicRequest;
import com.dantal.platform.dto.CreatePlatformClinicResult;
import com.dantal.platform.dto.PlatformClinicResponse;
import com.dantal.platform.dto.UpdatePlatformClinicRequest;
import com.dantal.platform.security.PlatformAccess;
import com.dantal.platform.service.PlatformClinicService;
import com.dantal.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    @GetMapping
    @Operation(summary = "List all clinics")
    public ApiResponse<List<PlatformClinicResponse>> listClinics() {
        return ApiResponse.ok(platformClinicService.listClinics());
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
}
