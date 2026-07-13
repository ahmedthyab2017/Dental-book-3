package com.dantal.platform.config;

import com.dantal.auth.service.RefreshTokenService;
import com.dantal.user.entity.Role;
import com.dantal.user.entity.RoleName;
import com.dantal.user.entity.User;
import com.dantal.user.repository.RoleRepository;
import com.dantal.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("prod")
@ConditionalOnProperty(name = "dantal.seed.super-admin-enabled", havingValue = "true")
@RequiredArgsConstructor
public class ProdSuperAdminSeeder implements ApplicationRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenService refreshTokenService;
    private final ProdSuperAdminSeedProperties seedProperties;

    @Override
    public void run(ApplicationArguments args) {
        String email = seedProperties.getEmail().trim().toLowerCase();
        if (email.isBlank() || seedProperties.getPassword().isBlank()) {
            log.warn("Super-admin seed skipped: email or password not configured");
            return;
        }
        if (userRepository.findByEmailIgnoreCase(email).isPresent()) {
            return;
        }

        Role superAdminRole = roleRepository.findByName(RoleName.SUPER_ADMIN)
                .orElseThrow(() -> new IllegalStateException("SUPER_ADMIN role missing from database"));

        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(refreshTokenService.encodePassword(seedProperties.getPassword()));
        user.setFirstName("Platform");
        user.setLastName("Admin");
        user.setEmailVerified(true);
        user.getRoles().add(superAdminRole);
        userRepository.save(user);

        log.info("Seeded production super-admin account: {}", email);
    }
}
