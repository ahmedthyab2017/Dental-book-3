-- Cloud sync, backups, licensing, and clinic tasks

CREATE TABLE clinic_sync_state (
    clinic_id         UUID         PRIMARY KEY REFERENCES clinics (id) ON DELETE CASCADE,
    sync_version      BIGINT       NOT NULL DEFAULT 0,
    ciphertext        TEXT,
    iv                VARCHAR(64),
    salt              VARCHAR(64),
    alg               VARCHAR(32)  DEFAULT 'AES-256-GCM',
    kdf_name          VARCHAR(32),
    kdf_iterations    INTEGER,
    kdf_hash          VARCHAR(32),
    client_updated_at TIMESTAMP,
    updated_by        UUID         REFERENCES users (id) ON DELETE SET NULL,
    updated_at        TIMESTAMP  NOT NULL DEFAULT NOW()
);

CREATE TABLE clinic_backups (
    id                UUID         PRIMARY KEY,
    clinic_id         UUID         NOT NULL REFERENCES clinics (id) ON DELETE CASCADE,
    label             VARCHAR(100),
    ciphertext        TEXT         NOT NULL,
    iv                VARCHAR(64)  NOT NULL,
    salt              VARCHAR(64)  NOT NULL,
    alg               VARCHAR(32)  NOT NULL DEFAULT 'AES-256-GCM',
    kdf_name          VARCHAR(32),
    kdf_iterations    INTEGER,
    kdf_hash          VARCHAR(32),
    client_updated_at TIMESTAMP,
    created_by        UUID         REFERENCES users (id) ON DELETE SET NULL,
    created_at        TIMESTAMP  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_clinic_backups_clinic ON clinic_backups (clinic_id, created_at DESC);

CREATE TABLE clinic_licenses (
    clinic_id     UUID         PRIMARY KEY REFERENCES clinics (id) ON DELETE CASCADE,
    license_key   VARCHAR(255) NOT NULL,
    tier          VARCHAR(32)  NOT NULL DEFAULT 'clinic',
    activated_at  TIMESTAMP  NOT NULL DEFAULT NOW(),
    activated_by  UUID         REFERENCES users (id) ON DELETE SET NULL
);

CREATE TABLE license_keys (
    id                UUID         PRIMARY KEY,
    key_hash          VARCHAR(255) NOT NULL,
    tier              VARCHAR(32)  NOT NULL DEFAULT 'clinic',
    max_activations   INTEGER      NOT NULL DEFAULT 1,
    activations_count INTEGER      NOT NULL DEFAULT 0,
    expires_at        TIMESTAMP,
    active            BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at        TIMESTAMP  NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_license_keys_hash UNIQUE (key_hash)
);

CREATE TABLE tasks (
    id                UUID         PRIMARY KEY,
    clinic_id         UUID         NOT NULL REFERENCES clinics (id) ON DELETE CASCADE,
    title             VARCHAR(500) NOT NULL,
    note              TEXT,
    assigner_name     VARCHAR(255),
    assignee_staff_id VARCHAR(100),
    assignee_name     VARCHAR(255),
    due_at            BIGINT,
    status            VARCHAR(20)  NOT NULL DEFAULT 'open',
    created_by        UUID         REFERENCES users (id) ON DELETE SET NULL,
    created_at        TIMESTAMP  NOT NULL DEFAULT NOW(),
    updated_at        TIMESTAMP  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_tasks_clinic_status ON tasks (clinic_id, status);
