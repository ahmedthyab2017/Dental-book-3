package com.dantal.platform.service;

import com.dantal.auth.service.RefreshTokenService;
import com.dantal.clinic.entity.Clinic;
import com.dantal.clinic.repository.ClinicRepository;
import com.dantal.common.exception.BusinessException;
import com.dantal.common.exception.ResourceNotFoundException;
import com.dantal.license.repository.ClinicLicenseRepository;
import com.dantal.platform.dto.CreatePlatformUserRequest;
import com.dantal.platform.dto.PlatformClinicDetailResponse;
import com.dantal.platform.dto.PlatformUserResponse;
import com.dantal.platform.dto.UpdatePlatformUserRequest;
import com.dantal.user.entity.Role;
import com.dantal.user.entity.RoleName;
import com.dantal.user.entity.User;
import com.dantal.user.repository.RoleRepository;
import com.dantal.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PlatformUserService {

    private static final Set<RoleName> ASSIGNABLE_ROLES = EnumSet.of(
            RoleName.ADMIN,
            RoleName.DOCTOR,
            RoleName.DENTIST,
            RoleName.RECEPTIONIST,
            RoleName.ASSISTANT,
            RoleName.ACCOUNTANT
    );

    private final ClinicRepository clinicRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ClinicLicenseRepository clinicLicenseRepository;
    private final RefreshTokenService refreshTokenService;

    @Transactional(readOnly = true)
    public PlatformClinicDetailResponse getClinicDetail(UUID clinicId) {
        Clinic clinic = requireClinic(clinicId);
        List<User> users = userRepository.findByClinic_IdOrderByCreatedAtAsc(clinicId);
        String managerEmail = users.stream()
                .filter(u -> u.getRoles().stream().anyMatch(r -> r.getName() == RoleName.ADMIN))
                .findFirst()
                .map(User::getEmail)
                .orElse(null);
        return PlatformClinicDetailResponse.from(
                clinic,
                managerEmail,
                clinicLicenseRepository.findById(clinicId).orElse(null),
                users
        );
    }

    @Transactional(readOnly = true)
    public List<PlatformUserResponse> listUsers(UUID clinicId) {
        requireClinic(clinicId);
        return userRepository.findByClinic_IdOrderByCreatedAtAsc(clinicId).stream()
                .map(PlatformUserResponse::from)
                .toList();
    }

    @Transactional
    public PlatformUserResponse createUser(UUID clinicId, CreatePlatformUserRequest request) {
        Clinic clinic = requireClinic(clinicId);
        validateAssignableRole(request.getRole());

        String email = request.getEmail().toLowerCase(Locale.ROOT);
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new BusinessException("Email is already registered", HttpStatus.CONFLICT, "EMAIL_TAKEN");
        }

        Role role = requireRole(request.getRole());
        User user = new User();
        user.setClinic(clinic);
        user.setEmail(email);
        user.setPasswordHash(refreshTokenService.encodePassword(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());
        user.setEmailVerified(true);
        user.getRoles().add(role);
        userRepository.save(user);

        return PlatformUserResponse.from(user);
    }

    @Transactional
    public PlatformUserResponse updateUser(UUID clinicId, UUID userId, UpdatePlatformUserRequest request) {
        requireClinic(clinicId);
        User user = userRepository.findByIdAndClinic_Id(userId, clinicId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        if (user.getRoles().stream().anyMatch(r -> r.getName() == RoleName.SUPER_ADMIN)) {
            throw new BusinessException("Cannot modify platform super-admin accounts", HttpStatus.FORBIDDEN, "FORBIDDEN");
        }

        if (request.getActive() != null) {
            user.setActive(request.getActive());
        }
        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPasswordHash(refreshTokenService.encodePassword(request.getPassword()));
        }
        if (request.getRole() != null) {
            validateAssignableRole(request.getRole());
            user.getRoles().clear();
            user.getRoles().add(requireRole(request.getRole()));
        }

        userRepository.save(user);
        return PlatformUserResponse.from(user);
    }

    @Transactional
    public void deleteUser(UUID clinicId, UUID userId) {
        requireClinic(clinicId);
        User user = userRepository.findByIdAndClinic_Id(userId, clinicId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        if (user.getRoles().stream().anyMatch(r -> r.getName() == RoleName.SUPER_ADMIN)) {
            throw new BusinessException("Cannot delete platform super-admin accounts", HttpStatus.FORBIDDEN, "FORBIDDEN");
        }

        long adminCount = userRepository.findByClinicIdAndRoleName(clinicId, RoleName.ADMIN).stream()
                .filter(User::isActive)
                .count();
        boolean isAdmin = user.getRoles().stream().anyMatch(r -> r.getName() == RoleName.ADMIN);
        if (isAdmin && adminCount <= 1 && user.isActive()) {
            throw new BusinessException("Clinic must have at least one active admin", HttpStatus.BAD_REQUEST, "LAST_ADMIN");
        }

        userRepository.delete(user);
    }

    private Clinic requireClinic(UUID clinicId) {
        return clinicRepository.findById(clinicId)
                .orElseThrow(() -> new ResourceNotFoundException("Clinic", clinicId));
    }

    private Role requireRole(RoleName roleName) {
        return roleRepository.findByName(roleName)
                .orElseThrow(() -> new IllegalStateException("Role missing: " + roleName));
    }

    private void validateAssignableRole(RoleName roleName) {
        if (!ASSIGNABLE_ROLES.contains(roleName)) {
            throw new BusinessException("Role cannot be assigned to clinic users", HttpStatus.BAD_REQUEST, "ROLE_INVALID");
        }
    }
}
