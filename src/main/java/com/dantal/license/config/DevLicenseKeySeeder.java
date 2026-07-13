package com.dantal.license.config;

import com.dantal.common.util.TokenHasher;
import com.dantal.license.entity.LicenseKey;
import com.dantal.license.repository.LicenseKeyRepository;
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
public class DevLicenseKeySeeder implements ApplicationRunner {

    private static final String DEV_LICENSE_KEY = "DANTAL-DEV-CLINIC";

    private final LicenseKeyRepository licenseKeyRepository;

    @Override
    public void run(ApplicationArguments args) {
        String hash = TokenHasher.sha256(DEV_LICENSE_KEY);
        if (licenseKeyRepository.findByKeyHashAndActiveTrue(hash).isPresent()) {
            return;
        }
        LicenseKey key = new LicenseKey();
        key.setKeyHash(hash);
        key.setTier("clinic");
        key.setMaxActivations(999);
        key.setActivationsCount(0);
        key.setActive(true);
        licenseKeyRepository.save(key);
        log.info("Seeded dev license key");
    }
}
