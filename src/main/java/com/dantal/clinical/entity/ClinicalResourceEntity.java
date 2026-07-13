package com.dantal.clinical.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@MappedSuperclass
public abstract class ClinicalResourceEntity {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "clinic_id", nullable = false)
    private UUID clinicId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "json")
    private Map<String, Object> payload = new LinkedHashMap<>();

    @Column(name = "sort_key", nullable = false)
    private long sortKey;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
        if (sortKey == 0L) {
            sortKey = extractSortKey(payload);
        }
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
        sortKey = extractSortKey(payload);
    }

    public static long extractSortKey(Map<String, Object> payload) {
        if (payload == null) {
            return 0L;
        }
        Object createdAt = payload.get("createdAt");
        if (createdAt == null) {
            createdAt = payload.get("at");
        }
        if (createdAt == null) {
            createdAt = payload.get("dueAt");
        }
        if (createdAt instanceof Number number) {
            return number.longValue();
        }
        return System.currentTimeMillis();
    }
}
