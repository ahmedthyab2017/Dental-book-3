package com.dantal.clinical.payment;

import com.dantal.clinical.service.ClinicalResourceService;
import org.springframework.stereotype.Service;

@Service
public class PaymentRecordService extends ClinicalResourceService<PaymentRecord> {
    public PaymentRecordService(PaymentRecordRepository repository) {
        super(repository, "Payment");
    }

    @Override
    protected PaymentRecord newEntity() {
        return new PaymentRecord();
    }
}
