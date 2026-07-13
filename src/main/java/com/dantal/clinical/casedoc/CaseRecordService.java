package com.dantal.clinical.casedoc;

import com.dantal.clinical.service.ClinicalResourceService;
import org.springframework.stereotype.Service;

@Service
public class CaseRecordService extends ClinicalResourceService<CaseRecord> {
    public CaseRecordService(CaseRecordRepository repository) {
        super(repository, "Case");
    }

    @Override
    protected CaseRecord newEntity() {
        return new CaseRecord();
    }
}
