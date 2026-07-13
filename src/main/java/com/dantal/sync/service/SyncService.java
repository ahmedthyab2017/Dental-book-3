package com.dantal.sync.service;

import com.dantal.common.exception.BusinessException;
import com.dantal.common.security.ClinicContext;
import com.dantal.security.UserPrincipal;
import com.dantal.sync.dto.EncryptedEnvelopeDto;
import com.dantal.sync.dto.SyncConflictResponse;
import com.dantal.sync.dto.SyncPullResponse;
import com.dantal.sync.dto.SyncPushRequest;
import com.dantal.sync.dto.SyncPushResponse;
import com.dantal.sync.entity.ClinicBackup;
import com.dantal.sync.entity.ClinicSyncState;
import com.dantal.sync.repository.ClinicBackupRepository;
import com.dantal.sync.repository.ClinicSyncStateRepository;
import com.dantal.user.entity.User;
import com.dantal.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SyncService {

    private final ClinicSyncStateRepository syncStateRepository;
    private final ClinicBackupRepository backupRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Optional<SyncPullResponse> pull(UserPrincipal principal, long sinceVersion) {
        UUID clinicId = ClinicContext.requireClinicId(principal);
        return syncStateRepository.findById(clinicId)
                .filter(state -> state.getSyncVersion() > sinceVersion && state.getCiphertext() != null)
                .map(this::toPullResponse);
    }

    @Transactional
    public SyncPushResponse push(UserPrincipal principal, SyncPushRequest request) {
        UUID clinicId = ClinicContext.requireClinicId(principal);
        User user = userRepository.getReferenceById(principal.getId());
        long baseVersion = request.getBaseVersion() != null ? request.getBaseVersion() : 0L;

        ClinicSyncState state = syncStateRepository.findById(clinicId).orElseGet(() -> {
            ClinicSyncState created = new ClinicSyncState();
            created.setClinicId(clinicId);
            created.setSyncVersion(0);
            return created;
        });

        if (state.getSyncVersion() != baseVersion) {
            throw new BusinessException(
                    "Sync version conflict",
                    HttpStatus.CONFLICT,
                    "SYNC_CONFLICT");
        }

        applyEnvelope(state, request);
        state.setSyncVersion(state.getSyncVersion() + 1);
        state.setUpdatedBy(user);
        state.setUpdatedAt(Instant.now());
        syncStateRepository.save(state);

        return new SyncPushResponse(state.getSyncVersion());
    }

    public SyncConflictResponse conflictBody(UserPrincipal principal, long clientBaseVersion) {
        UUID clinicId = ClinicContext.requireClinicId(principal);
        long serverVersion = syncStateRepository.findById(clinicId)
                .map(ClinicSyncState::getSyncVersion)
                .orElse(0L);
        return SyncConflictResponse.builder()
                .serverVersion(serverVersion)
                .clientBaseVersion(clientBaseVersion)
                .message("Server has a newer sync version")
                .build();
    }

    @Transactional
    public ClinicBackup createBackup(UserPrincipal principal, EncryptedEnvelopeDto request, String label) {
        UUID clinicId = ClinicContext.requireClinicId(principal);
        User user = userRepository.getReferenceById(principal.getId());

        ClinicBackup backup = new ClinicBackup();
        backup.setClinicId(clinicId);
        backup.setLabel(label != null && !label.isBlank() ? label : "manual");
        applyEnvelope(backup, request);
        backup.setCreatedBy(user);
        return backupRepository.save(backup);
    }

    private SyncPullResponse toPullResponse(ClinicSyncState state) {
        return SyncPullResponse.builder()
                .ciphertext(state.getCiphertext())
                .iv(state.getIv())
                .salt(state.getSalt())
                .alg(state.getAlg())
                .kdf(buildKdfMap(state.getKdfName(), state.getKdfIterations(), state.getKdfHash()))
                .version(state.getSyncVersion())
                .build();
    }

    private void applyEnvelope(ClinicSyncState state, EncryptedEnvelopeDto dto) {
        state.setCiphertext(dto.getCiphertext());
        state.setIv(dto.getIv());
        state.setSalt(dto.getSalt());
        state.setAlg(dto.getAlg() != null ? dto.getAlg() : "AES-256-GCM");
        applyKdf(state, dto.getKdf());
        state.setClientUpdatedAt(dto.getClientUpdatedAt());
    }

    private void applyEnvelope(ClinicBackup backup, EncryptedEnvelopeDto dto) {
        backup.setCiphertext(dto.getCiphertext());
        backup.setIv(dto.getIv());
        backup.setSalt(dto.getSalt());
        backup.setAlg(dto.getAlg() != null ? dto.getAlg() : "AES-256-GCM");
        applyKdf(backup, dto.getKdf());
        backup.setClientUpdatedAt(dto.getClientUpdatedAt());
    }

    private void applyKdf(ClinicSyncState state, Map<String, Object> kdf) {
        if (kdf == null) {
            return;
        }
        state.setKdfName(stringValue(kdf.get("name")));
        state.setKdfIterations(intValue(kdf.get("iterations")));
        state.setKdfHash(stringValue(kdf.get("hash")));
    }

    private void applyKdf(ClinicBackup backup, Map<String, Object> kdf) {
        if (kdf == null) {
            return;
        }
        backup.setKdfName(stringValue(kdf.get("name")));
        backup.setKdfIterations(intValue(kdf.get("iterations")));
        backup.setKdfHash(stringValue(kdf.get("hash")));
    }

    private Map<String, Object> buildKdfMap(String name, Integer iterations, String hash) {
        if (name == null && iterations == null && hash == null) {
            return null;
        }
        Map<String, Object> kdf = new LinkedHashMap<>();
        if (name != null) {
            kdf.put("name", name);
        }
        if (iterations != null) {
            kdf.put("iterations", iterations);
        }
        if (hash != null) {
            kdf.put("hash", hash);
        }
        return kdf;
    }

    private String stringValue(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private Integer intValue(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.intValue();
        }
        return Integer.parseInt(String.valueOf(value));
    }
}
