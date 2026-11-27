CREATE TABLE IF NOT EXISTS desporto24.venue_stripe_account (
    venue_id BIGINT PRIMARY KEY,
    stripe_account_id VARCHAR(64) NOT NULL UNIQUE,
    charges_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    created_by VARCHAR(80),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);
CREATE TABLE IF NOT EXISTS desporto24.venue_online_payment (
    checkout_session_id VARCHAR(128) PRIMARY KEY,
    booking_id BIGINT NOT NULL,
    stripe_account_id VARCHAR(64) NOT NULL,
    amount_cents INTEGER NOT NULL,
    status VARCHAR(16) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    paid_at TIMESTAMP WITH TIME ZONE
);
CREATE INDEX IF NOT EXISTS idx_venue_online_payment_booking ON desporto24.venue_online_payment (booking_id);
