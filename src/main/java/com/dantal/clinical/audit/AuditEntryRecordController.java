package com.dantal.clinical.audit;

import com.dantal.clinical.web.ClinicalResourceController;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/audit-log")
@Tag(name = "Audit entry")
@SecurityRequirement(name = "bearerAuth")
public class AuditEntryRecordController extends ClinicalResourceController {
    public AuditEntryRecordController(AuditEntryRecordService service) {
        super(service, "auditLog", "entry");
    }
}
