package com.dantal.clinical.plan;

import com.dantal.clinical.web.ClinicalResourceController;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/plans")
@Tag(name = "Treatment plan")
@SecurityRequirement(name = "bearerAuth")
public class PlanRecordController extends ClinicalResourceController {
    public PlanRecordController(PlanRecordService service) {
        super(service, "plans", "plan");
    }
}
