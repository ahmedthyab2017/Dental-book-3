package com.dantal.clinical.audit;

import com.dantal.clinical.entity.ClinicalResourceEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "audit_entries")
public class AuditEntryRecord extends ClinicalResourceEntity {
}
