package com.dantal.clinical.inventory;

import com.dantal.clinical.service.ClinicalResourceService;
import org.springframework.stereotype.Service;

@Service
public class InventoryRecordService extends ClinicalResourceService<InventoryRecord> {
    public InventoryRecordService(InventoryRecordRepository repository) {
        super(repository, "Inventory item");
    }

    @Override
    protected InventoryRecord newEntity() {
        return new InventoryRecord();
    }
}
