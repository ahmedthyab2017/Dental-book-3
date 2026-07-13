-- Clinical domain — all resources scoped by clinic_id, payload stored as JSON

CREATE TABLE clinic_settings (
    clinic_id  UUID      PRIMARY KEY REFERENCES clinics (id) ON DELETE CASCADE,
    payload    JSON      NOT NULL,
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE patients (
    id         VARCHAR(36) PRIMARY KEY,
    clinic_id  UUID        NOT NULL REFERENCES clinics (id) ON DELETE CASCADE,
    payload    JSON        NOT NULL,
    sort_key   BIGINT      NOT NULL DEFAULT 0,
    created_at TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP   NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_patients_clinic ON patients (clinic_id, sort_key DESC);

CREATE TABLE appointments (
    id         VARCHAR(36) PRIMARY KEY,
    clinic_id  UUID        NOT NULL REFERENCES clinics (id) ON DELETE CASCADE,
    payload    JSON        NOT NULL,
    sort_key   BIGINT      NOT NULL DEFAULT 0,
    created_at TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP   NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_appointments_clinic ON appointments (clinic_id, sort_key DESC);

CREATE TABLE staff_members (
    id         VARCHAR(36) PRIMARY KEY,
    clinic_id  UUID        NOT NULL REFERENCES clinics (id) ON DELETE CASCADE,
    payload    JSON        NOT NULL,
    sort_key   BIGINT      NOT NULL DEFAULT 0,
    created_at TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP   NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_staff_members_clinic ON staff_members (clinic_id, sort_key DESC);

CREATE TABLE treatment_plans (
    id         VARCHAR(36) PRIMARY KEY,
    clinic_id  UUID        NOT NULL REFERENCES clinics (id) ON DELETE CASCADE,
    payload    JSON        NOT NULL,
    sort_key   BIGINT      NOT NULL DEFAULT 0,
    created_at TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP   NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_treatment_plans_clinic ON treatment_plans (clinic_id, sort_key DESC);

CREATE TABLE prescriptions (
    id         VARCHAR(36) PRIMARY KEY,
    clinic_id  UUID        NOT NULL REFERENCES clinics (id) ON DELETE CASCADE,
    payload    JSON        NOT NULL,
    sort_key   BIGINT      NOT NULL DEFAULT 0,
    created_at TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP   NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_prescriptions_clinic ON prescriptions (clinic_id, sort_key DESC);

CREATE TABLE payments (
    id         VARCHAR(36) PRIMARY KEY,
    clinic_id  UUID        NOT NULL REFERENCES clinics (id) ON DELETE CASCADE,
    payload    JSON        NOT NULL,
    sort_key   BIGINT      NOT NULL DEFAULT 0,
    created_at TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP   NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_payments_clinic ON payments (clinic_id, sort_key DESC);

CREATE TABLE expenses (
    id         VARCHAR(36) PRIMARY KEY,
    clinic_id  UUID        NOT NULL REFERENCES clinics (id) ON DELETE CASCADE,
    payload    JSON        NOT NULL,
    sort_key   BIGINT      NOT NULL DEFAULT 0,
    created_at TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP   NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_expenses_clinic ON expenses (clinic_id, sort_key DESC);

CREATE TABLE vendors (
    id         VARCHAR(36) PRIMARY KEY,
    clinic_id  UUID        NOT NULL REFERENCES clinics (id) ON DELETE CASCADE,
    payload    JSON        NOT NULL,
    sort_key   BIGINT      NOT NULL DEFAULT 0,
    created_at TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP   NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_vendors_clinic ON vendors (clinic_id, sort_key DESC);

CREATE TABLE inventory_items (
    id         VARCHAR(36) PRIMARY KEY,
    clinic_id  UUID        NOT NULL REFERENCES clinics (id) ON DELETE CASCADE,
    payload    JSON        NOT NULL,
    sort_key   BIGINT      NOT NULL DEFAULT 0,
    created_at TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP   NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_inventory_items_clinic ON inventory_items (clinic_id, sort_key DESC);

CREATE TABLE service_catalog (
    id         VARCHAR(36) PRIMARY KEY,
    clinic_id  UUID        NOT NULL REFERENCES clinics (id) ON DELETE CASCADE,
    payload    JSON        NOT NULL,
    sort_key   BIGINT      NOT NULL DEFAULT 0,
    created_at TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP   NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_service_catalog_clinic ON service_catalog (clinic_id, sort_key DESC);

CREATE TABLE case_docs (
    id         VARCHAR(36) PRIMARY KEY,
    clinic_id  UUID        NOT NULL REFERENCES clinics (id) ON DELETE CASCADE,
    payload    JSON        NOT NULL,
    sort_key   BIGINT      NOT NULL DEFAULT 0,
    created_at TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP   NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_case_docs_clinic ON case_docs (clinic_id, sort_key DESC);

CREATE TABLE reminders (
    id         VARCHAR(36) PRIMARY KEY,
    clinic_id  UUID        NOT NULL REFERENCES clinics (id) ON DELETE CASCADE,
    payload    JSON        NOT NULL,
    sort_key   BIGINT      NOT NULL DEFAULT 0,
    created_at TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP   NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_reminders_clinic ON reminders (clinic_id, sort_key DESC);

CREATE TABLE voice_notes (
    id         VARCHAR(36) PRIMARY KEY,
    clinic_id  UUID        NOT NULL REFERENCES clinics (id) ON DELETE CASCADE,
    payload    JSON        NOT NULL,
    sort_key   BIGINT      NOT NULL DEFAULT 0,
    created_at TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP   NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_voice_notes_clinic ON voice_notes (clinic_id, sort_key DESC);

CREATE TABLE audit_entries (
    id         VARCHAR(36) PRIMARY KEY,
    clinic_id  UUID        NOT NULL REFERENCES clinics (id) ON DELETE CASCADE,
    payload    JSON        NOT NULL,
    sort_key   BIGINT      NOT NULL DEFAULT 0,
    created_at TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP   NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_audit_entries_clinic ON audit_entries (clinic_id, sort_key DESC);

CREATE TABLE settlements (
    id         VARCHAR(36) PRIMARY KEY,
    clinic_id  UUID        NOT NULL REFERENCES clinics (id) ON DELETE CASCADE,
    payload    JSON        NOT NULL,
    sort_key   BIGINT      NOT NULL DEFAULT 0,
    created_at TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP   NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_settlements_clinic ON settlements (clinic_id, sort_key DESC);

CREATE TABLE archives (
    id         VARCHAR(36) PRIMARY KEY,
    clinic_id  UUID        NOT NULL REFERENCES clinics (id) ON DELETE CASCADE,
    payload    JSON        NOT NULL,
    sort_key   BIGINT      NOT NULL DEFAULT 0,
    created_at TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP   NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_archives_clinic ON archives (clinic_id, sort_key DESC);

CREATE TABLE referrals (
    id         VARCHAR(36) PRIMARY KEY,
    clinic_id  UUID        NOT NULL REFERENCES clinics (id) ON DELETE CASCADE,
    payload    JSON        NOT NULL,
    sort_key   BIGINT      NOT NULL DEFAULT 0,
    created_at TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP   NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_referrals_clinic ON referrals (clinic_id, sort_key DESC);
