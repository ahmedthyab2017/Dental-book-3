package com.dantal.sync.repository;

import com.dantal.sync.entity.ClinicBackup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ClinicBackupRepository extends JpaRepository<ClinicBackup, UUID> {
}
