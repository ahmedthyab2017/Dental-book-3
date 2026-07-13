package com.dantal.clinical.settlement;

import com.dantal.clinical.service.ClinicalResourceService;
import org.springframework.stereotype.Service;

@Service
public class SettlementRecordService extends ClinicalResourceService<SettlementRecord> {
    public SettlementRecordService(SettlementRecordRepository repository) {
        super(repository, "Settlement");
    }

    @Override
    protected SettlementRecord newEntity() {
        return new SettlementRecord();
    }
}
