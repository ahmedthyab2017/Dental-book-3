package com.dantal.clinical.service;

import com.dantal.clinical.service.ClinicalResourceService;
import org.springframework.stereotype.Service;

@Service
public class ServiceRecordService extends ClinicalResourceService<ServiceRecord> {
    public ServiceRecordService(ServiceRecordRepository repository) {
        super(repository, "Service");
    }

    @Override
    protected ServiceRecord newEntity() {
        return new ServiceRecord();
    }
}
