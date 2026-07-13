package com.dantal.clinical.audit;

import com.dantal.clinical.service.ClinicalResourceService;
import org.springframework.stereotype.Service;

@Service
public class AuditEntryRecordService extends ClinicalResourceService<AuditEntryRecord> {
    public AuditEntryRecordService(AuditEntryRecordRepository repository) {
        super(repository, "Audit entry");
    }

    @Override
    protected AuditEntryRecord newEntity() {
        return new AuditEntryRecord();
    }
}
