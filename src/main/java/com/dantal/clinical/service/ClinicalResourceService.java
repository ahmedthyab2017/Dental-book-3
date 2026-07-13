package com.dantal.clinical.service;

import com.dantal.clinical.entity.ClinicalResourceEntity;
import com.dantal.clinical.repository.ClinicalResourceRepository;
import com.dantal.common.exception.BusinessException;
import com.dantal.common.exception.ResourceNotFoundException;
import com.dantal.common.security.ClinicContext;
import com.dantal.security.UserPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class ClinicalResourceService<T extends ClinicalResourceEntity> {

    private final ClinicalResourceRepository<T> repository;
    private final String resourceLabel;

    protected ClinicalResourceService(ClinicalResourceRepository<T> repository, String resourceLabel) {
        this.repository = repository;
        this.resourceLabel = resourceLabel;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> list(UserPrincipal principal) {
        UUID clinicId = ClinicContext.requireClinicId(principal);
        return repository.findByClinicIdOrderBySortKeyDesc(clinicId).stream()
                .map(ClinicalResourceEntity::getPayload)
                .toList();
    }

    @Transactional(readOnly = true)
    public Map<String, Object> get(UserPrincipal principal, String id) {
        return findOwned(principal, id).getPayload();
    }

    @Transactional
    public Map<String, Object> create(UserPrincipal principal, Map<String, Object> body) {
        UUID clinicId = ClinicContext.requireClinicId(principal);
        String id = requireId(body);
        if (repository.existsById(id)) {
            throw new BusinessException(resourceLabel + " already exists", HttpStatus.CONFLICT, "DUPLICATE_ID");
        }
        T entity = newEntity();
        entity.setId(id);
        entity.setClinicId(clinicId);
        entity.setPayload(new LinkedHashMap<>(body));
        entity.setSortKey(ClinicalResourceEntity.extractSortKey(body));
        return repository.save(entity).getPayload();
    }

    @Transactional
    public Map<String, Object> update(UserPrincipal principal, String id, Map<String, Object> body) {
        T entity = findOwned(principal, id);
        Map<String, Object> merged = new LinkedHashMap<>(entity.getPayload());
        merged.putAll(body);
        merged.put("id", id);
        entity.setPayload(merged);
        entity.setSortKey(ClinicalResourceEntity.extractSortKey(merged));
        return repository.save(entity).getPayload();
    }

    @Transactional
    public Map<String, Object> upsert(UserPrincipal principal, Map<String, Object> body) {
        String id = requireId(body);
        UUID clinicId = ClinicContext.requireClinicId(principal);
        return repository.findByIdAndClinicId(id, clinicId)
                .map(existing -> update(principal, id, body))
                .orElseGet(() -> create(principal, body));
    }

    @Transactional
    public void delete(UserPrincipal principal, String id) {
        T entity = findOwned(principal, id);
        repository.delete(entity);
    }

    @Transactional
    public void replaceAll(UserPrincipal principal, List<Map<String, Object>> items) {
        UUID clinicId = ClinicContext.requireClinicId(principal);
        repository.deleteByClinicId(clinicId);
        for (Map<String, Object> item : items) {
            create(principal, item);
        }
    }

    protected T findOwned(UserPrincipal principal, String id) {
        UUID clinicId = ClinicContext.requireClinicId(principal);
        return repository.findByIdAndClinicId(id, clinicId)
                .orElseThrow(() -> new ResourceNotFoundException(resourceLabel, id));
    }

    protected abstract T newEntity();

    private static String requireId(Map<String, Object> body) {
        Object id = body.get("id");
        if (id == null || String.valueOf(id).isBlank()) {
            throw new BusinessException("id is required", HttpStatus.BAD_REQUEST, "ID_REQUIRED");
        }
        return String.valueOf(id).trim();
    }
}
