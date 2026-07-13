package com.dantal.license.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "license_keys")
@Getter
@Setter
@NoArgsConstructor
public class LicenseKey {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "key_hash", nullable = false, unique = true)
    private String keyHash;

    @Column(nullable = false, length = 32)
    private String tier = "clinic";

    @Column(name = "max_activations", nullable = false)
    private int maxActivations = 1;

    @Column(name = "activations_count", nullable = false)
    private int activationsCount = 0;

    @Column(name = "expires_at")
    private Instant expiresAt;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public boolean canActivate() {
        return active
                && (expiresAt == null || expiresAt.isAfter(Instant.now()))
                && activationsCount < maxActivations;
    }
}
