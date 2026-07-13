package com.dantal.clinical.referral;

import com.dantal.clinical.service.ClinicalResourceService;
import org.springframework.stereotype.Service;

@Service
public class ReferralRecordService extends ClinicalResourceService<ReferralRecord> {
    public ReferralRecordService(ReferralRecordRepository repository) {
        super(repository, "Referral");
    }

    @Override
    protected ReferralRecord newEntity() {
        return new ReferralRecord();
    }
}
