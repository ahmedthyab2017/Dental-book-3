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
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("dev")
@RequiredArgsConstructor
public class DevSuperAdminSeeder implements ApplicationRunner {

    public static final String DEV_SUPER_ADMIN_EMAIL = "superadmin@dantal.local";
    public static final String DEV_SUPER_ADMIN_PASSWORD = "SuperAdmin123!";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenService refreshTokenService;

    @Override
    public void run(ApplicationArguments args) {
        if (userRepository.findByEmailIgnoreCase(DEV_SUPER_ADMIN_EMAIL).isPresent()) {
            return;
        }

        Role superAdminRole = roleRepository.findByName(RoleName.SUPER_ADMIN)
                .orElseThrow(() -> new IllegalStateException("SUPER_ADMIN role missing from database"));

        User user = new User();
        user.setEmail(DEV_SUPER_ADMIN_EMAIL);
        user.setPasswordHash(refreshTokenService.encodePassword(DEV_SUPER_ADMIN_PASSWORD));
        user.setFirstName("Platform");
        user.setLastName("Admin");
        user.setEmailVerified(true);
        user.getRoles().add(superAdminRole);
        userRepository.save(user);

        log.info("Seeded dev super-admin account: {}", DEV_SUPER_ADMIN_EMAIL);
    }
}
