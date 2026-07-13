package com.dantal.license.controller;

import com.dantal.license.dto.ActivateLicenseRequest;
import com.dantal.license.dto.ActivateLicenseResponse;
import com.dantal.license.service.LicenseService;
import com.dantal.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/license")
@RequiredArgsConstructor
@Tag(name = "License", description = "Clinic license activation")
@SecurityRequirement(name = "bearerAuth")
public class LicenseController {

    private final LicenseService licenseService;

    @PostMapping("/activate")
    @Operation(summary = "Activate a license key for the current clinic")
    public ActivateLicenseResponse activate(@AuthenticationPrincipal UserPrincipal principal,
                                            @Valid @RequestBody ActivateLicenseRequest request) {
        return licenseService.activate(principal, request.getKey());
    }
}
