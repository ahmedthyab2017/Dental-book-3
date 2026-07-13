package com.dantal.clinical.staff;

import com.dantal.clinical.web.ClinicalResourceController;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/staff")
@Tag(name = "Staff member")
@SecurityRequirement(name = "bearerAuth")
public class StaffRecordController extends ClinicalResourceController {
    public StaffRecordController(StaffRecordService service) {
        super(service, "staff", "staffMember");
    }
}
