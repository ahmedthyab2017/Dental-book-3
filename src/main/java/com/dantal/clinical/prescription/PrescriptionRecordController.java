package com.dantal.clinical.prescription;

import com.dantal.clinical.web.ClinicalResourceController;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/prescriptions")
@Tag(name = "Prescription")
@SecurityRequirement(name = "bearerAuth")
public class PrescriptionRecordController extends ClinicalResourceController {
    public PrescriptionRecordController(PrescriptionRecordService service) {
        super(service, "prescriptions", "prescription");
    }
}
