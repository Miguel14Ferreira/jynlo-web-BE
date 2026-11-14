-- Read-only psql inventory for Jynlo media migration.
-- Writes ONLY inventory rows to STDOUT (redirect to a secure local file).
-- Does not update, delete or change privacy flags.
-- Run: psql "$DATABASE_URL" -X -q -v ON_ERROR_STOP=1 -f scripts/export_profile_media_inventory.sql > inventory.csv
--
-- Keep private media inventory outside public folders and Git repositories.
-- If keys or URLs are missing, review separately before using the R2 staging script.
COPY (
  SELECT 'post'::text AS kind,
         p.id::text AS item_id,
         p.author_id::text AS owner_id,
         'image'::text AS slot,
         p.image_storage_key::text AS storage_key,
         CASE
           WHEN lower(p.image_storage_key) LIKE '%.png' THEN 'image/png'
           WHEN lower(p.image_storage_key) LIKE '%.webp' THEN 'image/webp'
           ELSE 'image/jpeg'
         END AS mime_type
    FROM desporto24.social_post p
   WHERE p.activity_type IS NULL AND p.image_storage_key IS NOT NULL
     AND p.image_storage_key NOT LIKE 'private-profile-media/%'
  UNION ALL
  SELECT 'post',p.id::text,p.author_id::text,'video',p.video_storage_key,
         CASE WHEN lower(p.video_storage_key) LIKE '%.webm' THEN 'video/webm'
              ELSE 'video/mp4' END
    FROM desporto24.social_post p
   WHERE p.activity_type IS NULL AND p.video_storage_key IS NOT NULL
     AND p.video_storage_key NOT LIKE 'private-profile-media/%'
  UNION ALL
  SELECT 'post',p.id::text,p.author_id::text,'extra-' || em.position::text,
         em.media_storage_key,em.media_mime_type
    FROM desporto24.social_post p
    JOIN desporto24.social_post_extra_media em ON em.post_id=p.id
   WHERE p.activity_type IS NULL AND em.media_storage_key IS NOT NULL
     AND em.media_storage_key NOT LIKE 'private-profile-media/%'
  UNION ALL
  SELECT 'story',s.id::text,s.author_id::text,'media',s.media_storage_key,
         CASE
           WHEN s.is_video AND lower(s.media_storage_key) LIKE '%.webm' THEN 'video/webm'
           WHEN s.is_video THEN 'video/mp4'
           WHEN lower(s.media_storage_key) LIKE '%.png' THEN 'image/png'
           WHEN lower(s.media_storage_key) LIKE '%.webp' THEN 'image/webp'
           ELSE 'image/jpeg'
         END
    FROM desporto24.social_story s
   WHERE s.community_id IS NULL AND s.media_storage_key IS NOT NULL
     AND s.media_storage_key NOT LIKE 'private-profile-media/%'
  ORDER BY owner_id,item_id,kind,slot
) TO STDOUT WITH (FORMAT CSV, HEADER TRUE);
