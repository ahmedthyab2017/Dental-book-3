package com.dantal.clinical.referral;

import com.dantal.clinical.web.ClinicalResourceController;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/referrals")
@Tag(name = "Referral")
@SecurityRequirement(name = "bearerAuth")
public class ReferralRecordController extends ClinicalResourceController {
    public ReferralRecordController(ReferralRecordService service) {
        super(service, "referrals", "referral");
    }
}
