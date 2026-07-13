package com.dantal.clinical.vendor;

import com.dantal.clinical.web.ClinicalResourceController;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/vendors")
@Tag(name = "Vendor")
@SecurityRequirement(name = "bearerAuth")
public class VendorRecordController extends ClinicalResourceController {
    public VendorRecordController(VendorRecordService service) {
        super(service, "vendors", "vendor");
    }
}
