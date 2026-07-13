package com.dantal.clinical.settlement;

import com.dantal.clinical.web.ClinicalResourceController;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/settlements")
@Tag(name = "Settlement")
@SecurityRequirement(name = "bearerAuth")
public class SettlementRecordController extends ClinicalResourceController {
    public SettlementRecordController(SettlementRecordService service) {
        super(service, "settlements", "settlement");
    }
}
