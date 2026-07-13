package com.dantal.clinical.archive;

import com.dantal.clinical.service.ClinicalResourceService;
import org.springframework.stereotype.Service;

@Service
public class ArchiveRecordService extends ClinicalResourceService<ArchiveRecord> {
    public ArchiveRecordService(ArchiveRecordRepository repository) {
        super(repository, "Archive");
    }

    @Override
    protected ArchiveRecord newEntity() {
        return new ArchiveRecord();
    }
}
