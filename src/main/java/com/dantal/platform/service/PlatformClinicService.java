package com.dantal.platform.service;

import com.dantal.auth.service.RefreshTokenService;
import com.dantal.clinic.entity.Clinic;
import com.dantal.clinic.repository.ClinicRepository;
import com.dantal.common.exception.BusinessException;
import com.dantal.common.exception.ResourceNotFoundException;
import com.dantal.license.entity.ClinicLicense;
import com.dantal.license.repository.ClinicLicenseRepository;
import com.dantal.license.service.LicenseService;
import com.dantal.platform.dto.CreatePlatformClinicRequest;
import com.dantal.platform.dto.CreatePlatformClinicResult;
import com.dantal.platform.dto.PlatformClinicResponse;
import com.dantal.platform.dto.UpdatePlatformClinicRequest;
import com.dantal.user.entity.Role;
import com.dantal.user.entity.RoleName;
import com.dantal.user.entity.User;
import com.dantal.user.repository.RoleRepository;
import com.dantal.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlatformClinicService {

    private final ClinicRepository clinicRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ClinicLicenseRepository clinicLicenseRepository;
    private final RefreshTokenService refreshTokenService;
    private final LicenseService licenseService;

    @Transactional(readOnly = true)
    public List<PlatformClinicResponse> listClinics() {
        List<Clinic> clinics = clinicRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
        List<PlatformClinicResponse> result = new ArrayList<>(clinics.size());
        for (Clinic clinic : clinics) {
            String managerEmail = userRepository.findByClinicIdAndRoleName(clinic.getId(), RoleName.ADMIN)
                    .stream()
                    .findFirst()
                    .map(User::getEmail)
                    .orElse(null);
            ClinicLicense license = clinicLicenseRepository.findById(clinic.getId()).orElse(null);
            result.add(PlatformClinicResponse.from(clinic, managerEmail, license));
        }
        return result;
    }

    @Transactional
    public CreatePlatformClinicResult createClinic(CreatePlatformClinicRequest request, UUID createdByUserId) {
        String email = request.getManagerEmail().toLowerCase(Locale.ROOT);
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new BusinessException("Email is already registered", HttpStatus.CONFLICT, "EMAIL_TAKEN");
        }

        Role adminRole = roleRepository.findByName(RoleName.ADMIN)
                .orElseThrow(() -> new IllegalStateException("ADMIN role missing from database"));

        Clinic clinic = new Clinic();
        clinic.setName(request.getClinicName().trim());
        clinic.setSlug(generateUniqueSlug(request.getClinicName()));
        clinicRepository.save(clinic);

        User manager = new User();
        manager.setClinic(clinic);
        manager.setEmail(email);
        manager.setPasswordHash(refreshTokenService.encodePassword(request.getManagerPassword()));
        manager.setFirstName(request.getManagerFirstName());
        manager.setLastName(request.getManagerLastName());
        manager.setPhone(request.getManagerPhone());
        manager.setEmailVerified(true);
        manager.getRoles().add(adminRole);
        userRepository.save(manager);

        boolean licenseActivated = false;
        ClinicLicense license = null;
        if (request.getLicenseKey() != null && !request.getLicenseKey().isBlank()) {
            User platformUser = userRepository.getReferenceById(createdByUserId);
            licenseService.activateForClinic(clinic.getId(), platformUser, request.getLicenseKey());
            license = clinicLicenseRepository.findById(clinic.getId()).orElse(null);
            licenseActivated = true;
        }

        log.info("Platform user {} created clinic '{}' with manager {}", createdByUserId, clinic.getName(), manager.getEmail());

        return CreatePlatformClinicResult.builder()
                .clinic(PlatformClinicResponse.from(clinic, manager.getEmail(), license))
                .managerEmail(manager.getEmail())
                .managerPassword(request.getManagerPassword())
                .licenseActivated(licenseActivated)
                .build();
    }

    @Transactional
    public PlatformClinicResponse updateClinic(UUID clinicId, UpdatePlatformClinicRequest request) {
        Clinic clinic = clinicRepository.findById(clinicId)
                .orElseThrow(() -> new ResourceNotFoundException("Clinic", clinicId));

        if (request.getActive() != null) {
            clinic.setActive(request.getActive());
            clinicRepository.save(clinic);
        }

        String managerEmail = userRepository.findByClinicIdAndRoleName(clinic.getId(), RoleName.ADMIN)
                .stream()
                .findFirst()
                .map(User::getEmail)
                .orElse(null);
        ClinicLicense license = clinicLicenseRepository.findById(clinic.getId()).orElse(null);
        return PlatformClinicResponse.from(clinic, managerEmail, license);
    }

    private String generateUniqueSlug(String clinicName) {
        String base = clinicName.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9\\u0600-\\u06FF]+", "-")
                .replaceAll("^-+|-+$", "");
        if (base.isBlank()) {
            base = "clinic";
        }
        String slug = base;
        int attempt = 0;
        while (clinicRepository.existsBySlug(slug)) {
            attempt++;
            slug = base + "-" + attempt;
        }
        return slug;
    }
}
