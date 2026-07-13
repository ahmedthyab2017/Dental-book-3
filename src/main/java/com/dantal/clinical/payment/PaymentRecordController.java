package com.dantal.clinical.payment;

import com.dantal.clinical.web.ClinicalResourceController;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/payments")
@Tag(name = "Payment")
@SecurityRequirement(name = "bearerAuth")
public class PaymentRecordController extends ClinicalResourceController {
    public PaymentRecordController(PaymentRecordService service) {
        super(service, "payments", "payment");
    }
}
