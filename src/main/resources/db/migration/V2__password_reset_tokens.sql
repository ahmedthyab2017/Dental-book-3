-- Password reset tokens — supports POST /v1/auth/forgot-password and /v1/auth/reset-password
CREATE TABLE password_reset_tokens (
    id         UUID         PRIMARY KEY,
    user_id    UUID         NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    token_hash VARCHAR(255) NOT NULL,
    expires_at TIMESTAMP  NOT NULL,
    used_at    TIMESTAMP,
    created_at TIMESTAMP  NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_password_reset_tokens_hash UNIQUE (token_hash)
);

CREATE INDEX idx_password_reset_tokens_user_id ON password_reset_tokens (user_id);
CREATE INDEX idx_password_reset_tokens_active  ON password_reset_tokens (user_id, used_at);
