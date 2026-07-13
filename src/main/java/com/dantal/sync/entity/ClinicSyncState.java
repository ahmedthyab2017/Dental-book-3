package com.dantal.sync.entity;

import com.dantal.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "clinic_sync_state")
@Getter
@Setter
@NoArgsConstructor
public class ClinicSyncState {

    @Id
    @Column(name = "clinic_id")
    private UUID clinicId;

    @Column(name = "sync_version", nullable = false)
    private long syncVersion = 0;

    @Column(columnDefinition = "TEXT")
    private String ciphertext;

    @Column(length = 64)
    private String iv;

    @Column(length = 64)
    private String salt;

    @Column(length = 32)
    private String alg = "AES-256-GCM";

    @Column(name = "kdf_name", length = 32)
    private String kdfName;

    @Column(name = "kdf_iterations")
    private Integer kdfIterations;

    @Column(name = "kdf_hash", length = 32)
    private String kdfHash;

    @Column(name = "client_updated_at")
    private Instant clientUpdatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    private User updatedBy;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();
}
