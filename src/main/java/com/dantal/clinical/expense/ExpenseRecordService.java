package com.dantal.clinical.expense;

import com.dantal.clinical.service.ClinicalResourceService;
import org.springframework.stereotype.Service;

@Service
public class ExpenseRecordService extends ClinicalResourceService<ExpenseRecord> {
    public ExpenseRecordService(ExpenseRecordRepository repository) {
        super(repository, "Expense");
    }

    @Override
    protected ExpenseRecord newEntity() {
        return new ExpenseRecord();
    }
}
