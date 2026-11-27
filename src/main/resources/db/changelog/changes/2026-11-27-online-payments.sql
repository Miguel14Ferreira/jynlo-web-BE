CREATE TABLE IF NOT EXISTS desporto24.stripe_connected_account (
    username VARCHAR(80) PRIMARY KEY,
    stripe_account_id VARCHAR(64) NOT NULL UNIQUE,
    charges_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);
CREATE TABLE IF NOT EXISTS desporto24.session_online_payment (
    checkout_session_id VARCHAR(128) PRIMARY KEY,
    session_id BIGINT NOT NULL,
    participant_id BIGINT NOT NULL,
    stripe_account_id VARCHAR(64) NOT NULL,
    amount_cents INTEGER NOT NULL,
    status VARCHAR(16) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    paid_at TIMESTAMP WITH TIME ZONE
);
CREATE INDEX IF NOT EXISTS idx_session_online_payment_participant ON desporto24.session_online_payment (participant_id);
