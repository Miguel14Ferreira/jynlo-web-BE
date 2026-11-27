-- Venues meant for adults (snooker, billiards, darts, bars, pubs…) are hidden
-- from visitors without an adult account. Same rule as AdultVenueAccess.detect.
ALTER TABLE desporto24.venue ADD COLUMN IF NOT EXISTS adults_only BOOLEAN NOT NULL DEFAULT FALSE;

UPDATE desporto24.venue
   SET adults_only = TRUE
 WHERE adults_only = FALSE
   AND (COALESCE(sports, '') ~* '\m(snooker|bilhar|dardos|darts|pool|poker)\M'
        OR COALESCE(venue_type, '') ~* '\m(bar|pub|discoteca|casino|taberna|cervejaria)\M');
