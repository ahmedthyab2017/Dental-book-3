package com.dantal.clinic.repository;

import com.dantal.clinic.entity.ClinicSettings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ClinicSettingsRepository extends JpaRepository<ClinicSettings, UUID> {
}
