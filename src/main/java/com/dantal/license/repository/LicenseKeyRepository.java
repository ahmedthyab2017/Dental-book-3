package com.dantal.license.repository;

import com.dantal.license.entity.LicenseKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface LicenseKeyRepository extends JpaRepository<LicenseKey, UUID> {

    Optional<LicenseKey> findByKeyHashAndActiveTrue(String keyHash);
}
