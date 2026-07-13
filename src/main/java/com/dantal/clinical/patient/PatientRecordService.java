package com.dantal.clinical.patient;

import com.dantal.clinical.service.ClinicalResourceService;
import org.springframework.stereotype.Service;

@Service
public class PatientRecordService extends ClinicalResourceService<PatientRecord> {
    public PatientRecordService(PatientRecordRepository repository) {
        super(repository, "Patient");
    }

    @Override
    protected PatientRecord newEntity() {
        return new PatientRecord();
    }
}
