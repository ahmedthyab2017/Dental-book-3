package com.dantal.clinical.prescription;

import com.dantal.clinical.service.ClinicalResourceService;
import org.springframework.stereotype.Service;

@Service
public class PrescriptionRecordService extends ClinicalResourceService<PrescriptionRecord> {
    public PrescriptionRecordService(PrescriptionRecordRepository repository) {
        super(repository, "Prescription");
    }

    @Override
    protected PrescriptionRecord newEntity() {
        return new PrescriptionRecord();
    }
}
