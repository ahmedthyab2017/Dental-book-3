package com.dantal.clinical.repository;

import com.dantal.clinical.entity.ClinicalResourceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@NoRepositoryBean
public interface ClinicalResourceRepository<T extends ClinicalResourceEntity>
        extends JpaRepository<T, String> {

    List<T> findByClinicIdOrderBySortKeyDesc(UUID clinicId);

    Optional<T> findByIdAndClinicId(String id, UUID clinicId);

    void deleteByClinicId(UUID clinicId);

    long countByClinicId(UUID clinicId);
}
