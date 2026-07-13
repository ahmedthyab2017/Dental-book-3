package com.dantal.clinical.inventory;

import com.dantal.clinical.web.ClinicalResourceController;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/inventory")
@Tag(name = "Inventory item")
@SecurityRequirement(name = "bearerAuth")
public class InventoryRecordController extends ClinicalResourceController {
    public InventoryRecordController(InventoryRecordService service) {
        super(service, "inventory", "item");
    }
}
