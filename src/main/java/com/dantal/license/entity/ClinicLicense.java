package com.dantal.license.entity;

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
@Table(name = "clinic_licenses")
@Getter
@Setter
@NoArgsConstructor
public class ClinicLicense {

    @Id
    @Column(name = "clinic_id")
    private UUID clinicId;

    @Column(name = "license_key", nullable = false)
    private String licenseKey;

    @Column(nullable = false, length = 32)
    private String tier = "clinic";

    @Column(name = "activated_at", nullable = false)
    private Instant activatedAt = Instant.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activated_by")
    private User activatedBy;
}
