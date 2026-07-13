package com.dantal.clinical.vendor;

import com.dantal.clinical.service.ClinicalResourceService;
import org.springframework.stereotype.Service;

@Service
public class VendorRecordService extends ClinicalResourceService<VendorRecord> {
    public VendorRecordService(VendorRecordRepository repository) {
        super(repository, "Vendor");
    }

    @Override
    protected VendorRecord newEntity() {
        return new VendorRecord();
    }
}
