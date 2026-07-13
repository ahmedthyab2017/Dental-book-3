import os

base = r"c:\Users\hp\Desktop\New folder (3)\demo\src\main\java\com\dantal\clinical"
resources = [
    ("PatientRecord", "patient", "patients", "/v1/patients", "patients", "patient", "Patient"),
    ("AppointmentRecord", "appointment", "appointments", "/v1/appointments", "appointments", "appointment", "Appointment"),
    ("StaffRecord", "staff", "staff_members", "/v1/staff", "staff", "staffMember", "Staff member"),
    ("PlanRecord", "plan", "treatment_plans", "/v1/plans", "plans", "plan", "Treatment plan"),
    ("PrescriptionRecord", "prescription", "prescriptions", "/v1/prescriptions", "prescriptions", "prescription", "Prescription"),
    ("PaymentRecord", "payment", "payments", "/v1/payments", "payments", "payment", "Payment"),
    ("ExpenseRecord", "expense", "expenses", "/v1/expenses", "expenses", "expense", "Expense"),
    ("VendorRecord", "vendor", "vendors", "/v1/vendors", "vendors", "vendor", "Vendor"),
    ("InventoryRecord", "inventory", "inventory_items", "/v1/inventory", "inventory", "item", "Inventory item"),
    ("ServiceRecord", "service", "service_catalog", "/v1/services", "services", "service", "Service"),
    ("CaseRecord", "casedoc", "case_docs", "/v1/cases", "cases", "case", "Case"),
    ("ReminderRecord", "reminder", "reminders", "/v1/reminders", "reminders", "reminder", "Reminder"),
    ("VoiceNoteRecord", "voicenote", "voice_notes", "/v1/voice-notes", "voiceNotes", "voiceNote", "Voice note"),
    ("AuditEntryRecord", "audit", "audit_entries", "/v1/audit-log", "auditLog", "entry", "Audit entry"),
    ("SettlementRecord", "settlement", "settlements", "/v1/settlements", "settlements", "settlement", "Settlement"),
    ("ArchiveRecord", "archive", "archives", "/v1/archives", "archives", "archive", "Archive"),
    ("ReferralRecord", "referral", "referrals", "/v1/referrals", "referrals", "referral", "Referral"),
]

created = []
for entity, pkg, table, api, coll, item, label in resources:
    d = os.path.join(base, pkg)
    os.makedirs(d, exist_ok=True)
    files = {
        f"{entity}.java": f"""package com.dantal.clinical.{pkg};

import com.dantal.clinical.entity.ClinicalResourceEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "{table}")
public class {entity} extends ClinicalResourceEntity {{
}}
""",
        f"{entity}Repository.java": f"""package com.dantal.clinical.{pkg};

import com.dantal.clinical.repository.ClinicalResourceRepository;

public interface {entity}Repository extends ClinicalResourceRepository<{entity}> {{
}}
""",
        f"{entity}Service.java": f"""package com.dantal.clinical.{pkg};

import com.dantal.clinical.service.ClinicalResourceService;
import org.springframework.stereotype.Service;

@Service
public class {entity}Service extends ClinicalResourceService<{entity}> {{
    public {entity}Service({entity}Repository repository) {{
        super(repository, "{label}");
    }}

    @Override
    protected {entity} newEntity() {{
        return new {entity}();
    }}
}}
""",
        f"{entity}Controller.java": f"""package com.dantal.clinical.{pkg};

import com.dantal.clinical.web.ClinicalResourceController;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("{api}")
@Tag(name = "{label}")
@SecurityRequirement(name = "bearerAuth")
public class {entity}Controller extends ClinicalResourceController {{
    public {entity}Controller({entity}Service service) {{
        super(service, "{coll}", "{item}");
    }}
}}
""",
    }
    for name, content in files.items():
        path = os.path.join(d, name)
        with open(path, "w", encoding="utf-8", newline="\n") as f:
            f.write(content)
        created.append(path)

print(f"Created {len(created)} files")
