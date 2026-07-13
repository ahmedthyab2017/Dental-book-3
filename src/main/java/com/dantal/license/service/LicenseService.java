package com.dantal.license.service;

import com.dantal.common.exception.BusinessException;
import com.dantal.common.security.ClinicContext;
import com.dantal.common.util.TokenHasher;
import com.dantal.license.dto.ActivateLicenseResponse;
import com.dantal.license.entity.ClinicLicense;
import com.dantal.license.entity.LicenseKey;
import com.dantal.license.repository.ClinicLicenseRepository;
import com.dantal.license.repository.LicenseKeyRepository;
import com.dantal.security.UserPrincipal;
import com.dantal.user.entity.User;
import com.dantal.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class LicenseService {

    private static final String DEV_LICENSE_KEY = "DANTAL-DEV-CLINIC";
    private static final String DEFAULT_PLATFORM_LICENSE_KEY = "platform-auto";
    private static final String DEFAULT_PLATFORM_TIER = "clinic";

    private final LicenseKeyRepository licenseKeyRepository;
    private final ClinicLicenseRepository clinicLicenseRepository;
    private final UserRepository userRepository;

    @Transactional
    public ActivateLicenseResponse activate(UserPrincipal principal, String rawKey) {
        UUID clinicId = ClinicContext.requireClinicId(principal);
        String key = rawKey.trim();

        if (key.isBlank()) {
            throw new BusinessException("License key is required", HttpStatus.BAD_REQUEST, "LICENSE_REQUIRED");
        }

        String tier = resolveTier(key);
        User user = userRepository.getReferenceById(principal.getId());

        ClinicLicense license = clinicLicenseRepository.findById(clinicId).orElseGet(ClinicLicense::new);
        license.setClinicId(clinicId);
        license.setLicenseKey(key);
        license.setTier(tier);
        license.setActivatedAt(Instant.now());
        license.setActivatedBy(user);
        clinicLicenseRepository.save(license);

        log.info("License activated for clinic {} tier={}", clinicId, tier);
        return new ActivateLicenseResponse("License activated successfully", tier);
    }

    @Transactional
    public String activateDefaultForClinic(UUID clinicId, User activatedBy) {
        ClinicLicense license = clinicLicenseRepository.findById(clinicId).orElseGet(ClinicLicense::new);
        license.setClinicId(clinicId);
        license.setLicenseKey(DEFAULT_PLATFORM_LICENSE_KEY);
        license.setTier(DEFAULT_PLATFORM_TIER);
        license.setActivatedAt(Instant.now());
        license.setActivatedBy(activatedBy);
        clinicLicenseRepository.save(license);

        log.info("Default license activated for clinic {} tier={}", clinicId, DEFAULT_PLATFORM_TIER);
        return DEFAULT_PLATFORM_TIER;
    }

    @Transactional
    public String activateForClinic(UUID clinicId, User activatedBy, String rawKey) {
        String key = rawKey.trim();
        if (key.isBlank()) {
            throw new BusinessException("License key is required", HttpStatus.BAD_REQUEST, "LICENSE_REQUIRED");
        }

        String tier = resolveTier(key);
        ClinicLicense license = clinicLicenseRepository.findById(clinicId).orElseGet(ClinicLicense::new);
        license.setClinicId(clinicId);
        license.setLicenseKey(key);
        license.setTier(tier);
        license.setActivatedAt(Instant.now());
        license.setActivatedBy(activatedBy);
        clinicLicenseRepository.save(license);

        log.info("Platform activated license for clinic {} tier={}", clinicId, tier);
        return tier;
    }

    private String resolveTier(String key) {
        if (DEV_LICENSE_KEY.equals(key)) {
            return "clinic";
        }

        LicenseKey licenseKey = licenseKeyRepository.findByKeyHashAndActiveTrue(TokenHasher.sha256(key))
                .orElseThrow(() -> new BusinessException("Invalid license key", HttpStatus.BAD_REQUEST, "LICENSE_INVALID"));

        if (!licenseKey.canActivate()) {
            throw new BusinessException("License key is expired or exhausted", HttpStatus.BAD_REQUEST, "LICENSE_UNAVAILABLE");
        }

        licenseKey.setActivationsCount(licenseKey.getActivationsCount() + 1);
        licenseKeyRepository.save(licenseKey);
        return licenseKey.getTier();
    }
}
