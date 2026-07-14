package com.dantal.clinic.service;

import com.dantal.auth.service.RefreshTokenService;
import com.dantal.clinic.entity.Clinic;
import com.dantal.clinic.repository.ClinicRepository;
import com.dantal.common.exception.BusinessException;
import com.dantal.common.exception.ForbiddenException;
import com.dantal.common.security.ClinicContext;
import com.dantal.platform.dto.CreatePlatformUserRequest;
import com.dantal.platform.dto.PlatformUserResponse;
import com.dantal.security.UserPrincipal;
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
public class ClinicTeamService {

    private static final Set<RoleName> CLINIC_MANAGER_ASSIGNABLE = EnumSet.of(
            RoleName.DOCTOR,
            RoleName.DENTIST,
            RoleName.RECEPTIONIST,
            RoleName.ASSISTANT,
            RoleName.ACCOUNTANT
    );

    private final ClinicRepository clinicRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenService refreshTokenService;

    @Transactional(readOnly = true)
    public List<PlatformUserResponse> listTeam(UserPrincipal principal) {
        UUID clinicId = ClinicContext.requireClinicId(principal);
        requireClinicAdmin(principal);
        return userRepository.findByClinic_IdOrderByCreatedAtAsc(clinicId).stream()
                .map(PlatformUserResponse::from)
                .toList();
    }

    @Transactional
    public PlatformUserResponse createTeamMember(UserPrincipal principal, CreatePlatformUserRequest request) {
        UUID clinicId = ClinicContext.requireClinicId(principal);
        requireClinicAdmin(principal);
        validateStaffRole(request.getRole());

        Clinic clinic = clinicRepository.findById(clinicId)
                .orElseThrow(() -> new BusinessException("Clinic not found", HttpStatus.NOT_FOUND, "CLINIC_NOT_FOUND"));

        String email = request.getEmail().toLowerCase(Locale.ROOT);
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new BusinessException("Email is already registered", HttpStatus.CONFLICT, "EMAIL_TAKEN");
        }

        Role role = roleRepository.findByName(request.getRole())
                .orElseThrow(() -> new IllegalStateException("Role missing: " + request.getRole()));

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

    private void requireClinicAdmin(UserPrincipal principal) {
        if (!principal.getRoles().contains(RoleName.ADMIN)) {
            throw new ForbiddenException("Only clinic managers can manage team accounts");
        }
    }

    private void validateStaffRole(RoleName roleName) {
        if (!CLINIC_MANAGER_ASSIGNABLE.contains(roleName)) {
            throw new BusinessException("Clinic managers can only add staff roles", HttpStatus.BAD_REQUEST, "ROLE_INVALID");
        }
    }
}
