package com.dantal.clinic.service;

import com.dantal.clinical.appointment.AppointmentRecordService;
import com.dantal.clinical.archive.ArchiveRecordService;
import com.dantal.clinical.audit.AuditEntryRecordService;
import com.dantal.clinical.casedoc.CaseRecordService;
import com.dantal.clinical.expense.ExpenseRecordService;
import com.dantal.clinical.inventory.InventoryRecordService;
import com.dantal.clinical.patient.PatientRecordService;
import com.dantal.clinical.payment.PaymentRecordService;
import com.dantal.clinical.plan.PlanRecordService;
import com.dantal.clinical.prescription.PrescriptionRecordService;
import com.dantal.clinical.referral.ReferralRecordService;
import com.dantal.clinical.reminder.ReminderRecordService;
import com.dantal.clinical.service.ServiceRecordService;
import com.dantal.clinical.settlement.SettlementRecordService;
import com.dantal.clinical.staff.StaffRecordService;
import com.dantal.clinical.vendor.VendorRecordService;
import com.dantal.clinical.voicenote.VoiceNoteRecordService;
import com.dantal.clinic.entity.ClinicSettings;
import com.dantal.clinic.repository.ClinicSettingsRepository;
import com.dantal.common.security.ClinicContext;
import com.dantal.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClinicDataService {

    private final ClinicSettingsRepository clinicSettingsRepository;
    private final PatientRecordService patientRecordService;
    private final AppointmentRecordService appointmentRecordService;
    private final StaffRecordService staffRecordService;
    private final PlanRecordService planRecordService;
    private final PrescriptionRecordService prescriptionRecordService;
    private final PaymentRecordService paymentRecordService;
    private final ExpenseRecordService expenseRecordService;
    private final VendorRecordService vendorRecordService;
    private final InventoryRecordService inventoryRecordService;
    private final ServiceRecordService serviceRecordService;
    private final CaseRecordService caseRecordService;
    private final ReminderRecordService reminderRecordService;
    private final VoiceNoteRecordService voiceNoteRecordService;
    private final AuditEntryRecordService auditEntryRecordService;
    private final SettlementRecordService settlementRecordService;
    private final ArchiveRecordService archiveRecordService;
    private final ReferralRecordService referralRecordService;

    @Transactional(readOnly = true)
    public Map<String, Object> getSettings(UserPrincipal principal) {
        UUID clinicId = ClinicContext.requireClinicId(principal);
        return clinicSettingsRepository.findById(clinicId)
                .map(ClinicSettings::getPayload)
                .map(LinkedHashMap::new)
                .orElseGet(LinkedHashMap::new);
    }

    @Transactional
    public Map<String, Object> updateSettings(UserPrincipal principal, Map<String, Object> body) {
        UUID clinicId = ClinicContext.requireClinicId(principal);
        ClinicSettings settings = clinicSettingsRepository.findById(clinicId).orElseGet(() -> {
            ClinicSettings created = new ClinicSettings();
            created.setClinicId(clinicId);
            return created;
        });
        settings.setPayload(new LinkedHashMap<>(body));
        return clinicSettingsRepository.save(settings).getPayload();
    }

    @Transactional(readOnly = true)
    public Map<String, Object> exportAll(UserPrincipal principal) {
        Map<String, Object> db = new LinkedHashMap<>();
        db.put("meta", getSettings(principal));
        db.put("patients", patientRecordService.list(principal));
        db.put("appointments", appointmentRecordService.list(principal));
        db.put("staff", staffRecordService.list(principal));
        db.put("plans", planRecordService.list(principal));
        db.put("prescriptions", prescriptionRecordService.list(principal));
        db.put("payments", paymentRecordService.list(principal));
        db.put("expenses", expenseRecordService.list(principal));
        db.put("vendors", vendorRecordService.list(principal));
        db.put("inventory", inventoryRecordService.list(principal));
        db.put("services", serviceRecordService.list(principal));
        db.put("cases", caseRecordService.list(principal));
        db.put("reminders", reminderRecordService.list(principal));
        db.put("voiceNotes", voiceNoteRecordService.list(principal));
        db.put("auditLog", auditEntryRecordService.list(principal));
        db.put("settlements", settlementRecordService.list(principal));
        db.put("archives", archiveRecordService.list(principal));
        db.put("referrals", referralRecordService.list(principal));
        return db;
    }

    @Transactional
    public void importAll(UserPrincipal principal, Map<String, Object> body) {
        if (body.containsKey("meta")) {
            updateSettings(principal, asMap(body.get("meta")));
        }
        replaceCollection(principal, body, "patients", patientRecordService);
        replaceCollection(principal, body, "appointments", appointmentRecordService);
        replaceCollection(principal, body, "staff", staffRecordService);
        replaceCollection(principal, body, "plans", planRecordService);
        replaceCollection(principal, body, "prescriptions", prescriptionRecordService);
        replaceCollection(principal, body, "payments", paymentRecordService);
        replaceCollection(principal, body, "expenses", expenseRecordService);
        replaceCollection(principal, body, "vendors", vendorRecordService);
        replaceCollection(principal, body, "inventory", inventoryRecordService);
        replaceCollection(principal, body, "services", serviceRecordService);
        replaceCollection(principal, body, "cases", caseRecordService);
        replaceCollection(principal, body, "reminders", reminderRecordService);
        replaceCollection(principal, body, "voiceNotes", voiceNoteRecordService);
        replaceCollection(principal, body, "auditLog", auditEntryRecordService);
        replaceCollection(principal, body, "settlements", settlementRecordService);
        replaceCollection(principal, body, "archives", archiveRecordService);
        replaceCollection(principal, body, "referrals", referralRecordService);
    }

    private void replaceCollection(UserPrincipal principal,
                                   Map<String, Object> body,
                                   String key,
                                   com.dantal.clinical.service.ClinicalResourceService<?> service) {
        if (!body.containsKey(key)) {
            return;
        }
        service.replaceAll(principal, asList(body.get(key)));
    }

    @SuppressWarnings("unchecked")
    private static List<Map<String, Object>> asList(Object value) {
        if (value instanceof List<?> list) {
            return (List<Map<String, Object>>) list;
        }
        return List.of();
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> asMap(Object value) {
        if (value instanceof Map<?, ?> map) {
            return (Map<String, Object>) map;
        }
        return new LinkedHashMap<>();
    }
}
