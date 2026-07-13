package com.dantal.clinical.reminder;

import com.dantal.clinical.web.ClinicalResourceController;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/reminders")
@Tag(name = "Reminder")
@SecurityRequirement(name = "bearerAuth")
public class ReminderRecordController extends ClinicalResourceController {
    public ReminderRecordController(ReminderRecordService service) {
        super(service, "reminders", "reminder");
    }
}
