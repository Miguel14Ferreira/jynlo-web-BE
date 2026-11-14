#!/usr/bin/env bash
# SQL-only integration test against an isolated ephemeral PostgreSQL 16 database.
# No production connection strings or secrets are used.
set -euo pipefail
export PGOPTIONS="-c search_path=desporto24,public"
psql -X -q -v ON_ERROR_STOP=1 <<'SQL'
CREATE SCHEMA desporto24;
CREATE TABLE desporto24.social_post (
  id bigint PRIMARY KEY, author_id bigint NOT NULL,
  activity_type text, image_storage_key text, video_storage_key text,
  image_url text, video_url text
);
CREATE TABLE desporto24.social_post_extra_media (
  post_id bigint NOT NULL, position integer NOT NULL,
  media_storage_key text, media_url text, media_mime_type text
);
CREATE TABLE desporto24.social_story (
  id bigint PRIMARY KEY, author_id bigint NOT NULL,
  community_id bigint, is_video boolean NOT NULL,
  media_storage_key text, media_url text
);
INSERT INTO desporto24.social_post
(id,author_id,image_storage_key,image_url,video_storage_key,video_url)
VALUES (101,42,'posts/42/image-a.jpg','https://public.example/image-a.jpg',NULL,NULL),
       (102,42,'private-profile-media/posts/42/protected-a.png','/api/profile-media/posts/102/image',NULL,NULL);
INSERT INTO desporto24.social_post_extra_media
(post_id,position,media_storage_key,media_url,media_mime_type)
VALUES (101,0,'posts/42/video-b.mp4','https://public.example/video-b.mp4','video/mp4');
INSERT INTO desporto24.social_story
(id,author_id,community_id,is_video,media_storage_key,media_url)
VALUES (301,42,NULL,false,'stories/42/story-a.png','https://public.example/story-a.png'),
       (302,42,100,false,'stories/42/public-community.png','https://public.example/group.png');
SQL

psql -X -q -v ON_ERROR_STOP=1 -f scripts/audit_profile_media_inventory_gaps.sql > /tmp/jynlo-media-gaps.txt
psql -X -q -v ON_ERROR_STOP=1 -f scripts/export_profile_media_inventory.sql > /tmp/jynlo-media-inventory.csv
python3 scripts/stage_private_profile_media.py --inventory /tmp/jynlo-media-inventory.csv
python3 - <<'PY'
from pathlib import Path
import csv
rows=list(csv.DictReader(Path('/tmp/jynlo-media-inventory.csv').open()))
assert len(rows)==3, rows
assert {(r['kind'],r['item_id'],r['slot']) for r in rows} == {
    ('post','101','image'),('post','101','extra-0'),('story','301','media')
}, rows
gaps=Path('/tmp/jynlo-media-gaps.txt').read_text()
assert 'post_image' in gaps and 'post_album' in gaps and 'personal_story' in gaps
assert 'post_video' not in gaps
print('SQL inventory and gap audit PASS: 3 legacy personal media objects, private/community excluded')
PY
