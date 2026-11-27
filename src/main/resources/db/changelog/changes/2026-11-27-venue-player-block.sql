CREATE TABLE IF NOT EXISTS desporto24.venue_player_block (
    id BIGSERIAL PRIMARY KEY,
    venue_id BIGINT NOT NULL,
    username VARCHAR(120) NOT NULL,
    reason VARCHAR(16) NOT NULL,
    booking_id BIGINT,
    created_by VARCHAR(120) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);
CREATE UNIQUE INDEX IF NOT EXISTS uk_venue_player_block ON desporto24.venue_player_block (venue_id, LOWER(username));
