package com.dantal.clinical.staff;

import com.dantal.clinical.service.ClinicalResourceService;
import org.springframework.stereotype.Service;

@Service
public class StaffRecordService extends ClinicalResourceService<StaffRecord> {
    public StaffRecordService(StaffRecordRepository repository) {
        super(repository, "Staff member");
    }

    @Override
    protected StaffRecord newEntity() {
        return new StaffRecord();
    }
}
