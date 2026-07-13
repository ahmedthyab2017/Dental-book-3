package com.dantal.clinical.appointment;

import com.dantal.clinical.web.ClinicalResourceController;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/appointments")
@Tag(name = "Appointment")
@SecurityRequirement(name = "bearerAuth")
public class AppointmentRecordController extends ClinicalResourceController {
    public AppointmentRecordController(AppointmentRecordService service) {
        super(service, "appointments", "appointment");
    }
}
