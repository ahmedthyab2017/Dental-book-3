package com.dantal.clinic.controller;

import com.dantal.clinic.service.ClinicDataService;
import com.dantal.security.ClinicalAccess;
import com.dantal.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/v1/clinic")
@RequiredArgsConstructor
@ClinicalAccess
@Tag(name = "Clinic data", description = "Clinic settings and full database export/import")
@SecurityRequirement(name = "bearerAuth")
public class ClinicDataController {

    private final ClinicDataService clinicDataService;

    @GetMapping("/settings")
    @Operation(summary = "Get clinic settings (meta payload)")
    public Map<String, Object> getSettings(@AuthenticationPrincipal UserPrincipal principal) {
        return Map.of("meta", clinicDataService.getSettings(principal));
    }

    @PutMapping("/settings")
    @Operation(summary = "Replace clinic settings (meta payload)")
    public Map<String, Object> updateSettings(@AuthenticationPrincipal UserPrincipal principal,
                                              @RequestBody Map<String, Object> body) {
        return Map.of("meta", clinicDataService.updateSettings(principal, body));
    }

    @GetMapping("/export")
    @Operation(summary = "Export full clinic database (DentistDb shape)")
    public Map<String, Object> exportAll(@AuthenticationPrincipal UserPrincipal principal) {
        return clinicDataService.exportAll(principal);
    }

    @PostMapping("/import")
    @Operation(summary = "Import full clinic database (DentistDb shape)")
    public ResponseEntity<Map<String, String>> importAll(@AuthenticationPrincipal UserPrincipal principal,
                                                           @RequestBody Map<String, Object> body) {
        clinicDataService.importAll(principal, body);
        return ResponseEntity.ok(Map.of("status", "imported"));
    }
}
