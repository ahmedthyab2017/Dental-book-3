package com.dantal.clinical.reminder;

import com.dantal.clinical.service.ClinicalResourceService;
import org.springframework.stereotype.Service;

@Service
public class ReminderRecordService extends ClinicalResourceService<ReminderRecord> {
    public ReminderRecordService(ReminderRecordRepository repository) {
        super(repository, "Reminder");
    }

    @Override
    protected ReminderRecord newEntity() {
        return new ReminderRecord();
    }
}
