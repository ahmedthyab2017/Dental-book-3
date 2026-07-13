package com.dantal.sync.repository;

import com.dantal.sync.entity.ClinicSyncState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ClinicSyncStateRepository extends JpaRepository<ClinicSyncState, UUID> {
}
