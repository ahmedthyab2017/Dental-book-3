package com.dantal.clinical.plan;

import com.dantal.clinical.service.ClinicalResourceService;
import org.springframework.stereotype.Service;

@Service
public class PlanRecordService extends ClinicalResourceService<PlanRecord> {
    public PlanRecordService(PlanRecordRepository repository) {
        super(repository, "Treatment plan");
    }

    @Override
    protected PlanRecord newEntity() {
        return new PlanRecord();
    }
}
