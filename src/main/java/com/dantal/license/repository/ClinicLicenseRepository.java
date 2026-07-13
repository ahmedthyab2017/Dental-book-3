package com.dantal.license.repository;

import com.dantal.license.entity.ClinicLicense;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ClinicLicenseRepository extends JpaRepository<ClinicLicense, UUID> {
}
