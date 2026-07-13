package com.dantal.clinical.appointment;

import com.dantal.clinical.service.ClinicalResourceService;
import org.springframework.stereotype.Service;

@Service
public class AppointmentRecordService extends ClinicalResourceService<AppointmentRecord> {
    public AppointmentRecordService(AppointmentRecordRepository repository) {
        super(repository, "Appointment");
    }

    @Override
    protected AppointmentRecord newEntity() {
        return new AppointmentRecord();
    }
}
