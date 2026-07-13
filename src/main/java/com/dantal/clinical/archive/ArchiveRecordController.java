package com.dantal.clinical.archive;

import com.dantal.clinical.web.ClinicalResourceController;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/archives")
@Tag(name = "Archive")
@SecurityRequirement(name = "bearerAuth")
public class ArchiveRecordController extends ClinicalResourceController {
    public ArchiveRecordController(ArchiveRecordService service) {
        super(service, "archives", "archive");
    }
}
