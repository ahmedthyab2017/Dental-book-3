package com.dantal.platform.dto;

import com.dantal.clinic.entity.Clinic;
import com.dantal.license.entity.ClinicLicense;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
public class PlatformClinicResponse {

    private UUID id;
    private String name;
    private String slug;
    private boolean active;
    private Instant createdAt;
    private String managerEmail;
    private String licenseTier;
    private boolean licenseActivated;

    public static PlatformClinicResponse from(Clinic clinic, String managerEmail, ClinicLicense license) {
        return PlatformClinicResponse.builder()
                .id(clinic.getId())
                .name(clinic.getName())
                .slug(clinic.getSlug())
                .active(clinic.isActive())
                .createdAt(clinic.getCreatedAt())
                .managerEmail(managerEmail)
                .licenseTier(license != null ? license.getTier() : null)
                .licenseActivated(license != null)
                .build();
    }
}
