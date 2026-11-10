\set ON_ERROR_STOP on
BEGIN;
CREATE SCHEMA season_delete_qa;
SET search_path TO season_delete_qa;
CREATE TABLE community_season (id BIGINT PRIMARY KEY, name TEXT NOT NULL);
CREATE TABLE session (
    id BIGINT PRIMARY KEY,
    season_competition_id BIGINT REFERENCES community_season(id),
    sport TEXT NOT NULL
);
CREATE TABLE session_participant (
    id BIGINT PRIMARY KEY,
    session_id BIGINT NOT NULL REFERENCES session(id),
    username TEXT NOT NULL
);
INSERT INTO community_season VALUES (10, 'Época anterior'), (11, 'Época atual');
INSERT INTO session VALUES
    (100, 10, 'Futebol'),
    (101, 10, 'Futebol'),
    (102, 11, 'Futebol'),
    (103, NULL, 'Futebol');
INSERT INTO session_participant VALUES
    (201, 100, 'organizador'),
    (202, 101, 'jogador'),
    (203, 102, 'participante');
-- Same nullable FK/UPDATE shape used by SessionRepository.detachDeletedSeason.
UPDATE session SET season_competition_id = NULL WHERE season_competition_id = 10;
DELETE FROM community_season WHERE id = 10;
DO $test$
BEGIN
    IF (SELECT count(*) FROM session) <> 4 THEN
        RAISE EXCEPTION 'Deleting a season removed a session';
    END IF;
    IF (SELECT count(*) FROM session_participant) <> 3 THEN
        RAISE EXCEPTION 'Deleting a season removed attendance';
    END IF;
    IF EXISTS (SELECT 1 FROM session WHERE id IN (100, 101) AND season_competition_id IS NOT NULL) THEN
        RAISE EXCEPTION 'Deleted season still referenced by a game';
    END IF;
    IF NOT EXISTS (SELECT 1 FROM session WHERE id = 102 AND season_competition_id = 11) THEN
        RAISE EXCEPTION 'Another season association was changed';
    END IF;
    IF NOT EXISTS (SELECT 1 FROM session WHERE id = 103 AND season_competition_id IS NULL) THEN
        RAISE EXCEPTION 'Ordinary session changed';
    END IF;
    IF (SELECT count(*) FROM community_season) <> 1 THEN
        RAISE EXCEPTION 'Unexpected remaining seasons';
    END IF;
END
$test$;
ROLLBACK;
