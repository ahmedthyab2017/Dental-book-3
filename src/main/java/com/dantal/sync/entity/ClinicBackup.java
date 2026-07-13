package com.dantal.sync.entity;

import com.dantal.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
@Table(name = "clinic_backups")
@Getter
@Setter
@NoArgsConstructor
public class ClinicBackup {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "clinic_id", nullable = false)
    private UUID clinicId;

    @Column(length = 100)
    private String label;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String ciphertext;

    @Column(nullable = false, length = 64)
    private String iv;

    @Column(nullable = false, length = 64)
    private String salt;

    @Column(nullable = false, length = 32)
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
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();
}
