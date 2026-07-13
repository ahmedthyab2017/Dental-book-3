package com.dantal.sync.controller;

import com.dantal.common.exception.BusinessException;
import com.dantal.security.UserPrincipal;
import com.dantal.sync.dto.BackupRequest;
import com.dantal.sync.dto.BackupResponse;
import com.dantal.sync.dto.SyncPullResponse;
import com.dantal.sync.dto.SyncPushRequest;
import com.dantal.sync.dto.SyncPushResponse;
import com.dantal.sync.entity.ClinicBackup;
import com.dantal.sync.service.SyncService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
@Tag(name = "Cloud Sync", description = "Encrypted end-to-end sync for clinic data")
@SecurityRequirement(name = "bearerAuth")
public class SyncController {

    private final SyncService syncService;

    @GetMapping("/sync/pull")
    @Operation(summary = "Pull encrypted clinic snapshot when server version is newer")
    public ResponseEntity<SyncPullResponse> pull(@AuthenticationPrincipal UserPrincipal principal,
                                                 @RequestParam(name = "sinceVersion", defaultValue = "0") long sinceVersion) {
        return syncService.pull(principal, sinceVersion)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PostMapping("/sync/push")
    @Operation(summary = "Push encrypted clinic snapshot with optimistic versioning")
    public ResponseEntity<?> push(@AuthenticationPrincipal UserPrincipal principal,
                                  @Valid @RequestBody SyncPushRequest request) {
        try {
            SyncPushResponse response = syncService.push(principal, request);
            return ResponseEntity.ok(response);
        } catch (BusinessException ex) {
            if ("SYNC_CONFLICT".equals(ex.getCode())) {
                long baseVersion = request.getBaseVersion() != null ? request.getBaseVersion() : 0L;
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(syncService.conflictBody(principal, baseVersion));
            }
            throw ex;
        }
    }

    @PostMapping("/backups")
    @Operation(summary = "Store an encrypted manual backup")
    public BackupResponse backup(@AuthenticationPrincipal UserPrincipal principal,
                                 @Valid @RequestBody BackupRequest request) {
        ClinicBackup backup = syncService.createBackup(principal, request, request.getLabel());
        return BackupResponse.builder()
                .id(backup.getId().toString())
                .label(backup.getLabel())
                .createdAt(backup.getCreatedAt())
                .build();
    }
}
