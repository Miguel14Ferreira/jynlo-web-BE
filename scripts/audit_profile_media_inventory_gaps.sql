-- SAFE: read-only audit of gaps which the media inventory export cannot copy.
-- Run on authorized PostgreSQL connection with schema desporto24.
-- Any positive missing_keys / unsupported_keys count BLOCKS the private-media cutover.
-- Never export URLs or usernames from this report.
WITH media AS (
  SELECT 'post_image'::text kind, p.image_storage_key AS media_key,
         p.image_url AS media_url FROM desporto24.social_post p
  WHERE p.activity_type IS NULL
    AND (p.image_url IS NOT NULL OR p.image_storage_key IS NOT NULL)
  UNION ALL
  SELECT 'post_video',p.video_storage_key,p.video_url
  FROM desporto24.social_post p
  WHERE p.activity_type IS NULL
    AND (p.video_url IS NOT NULL OR p.video_storage_key IS NOT NULL)
  UNION ALL
  SELECT 'post_album',em.media_storage_key,em.media_url
  FROM desporto24.social_post p
  JOIN desporto24.social_post_extra_media em ON em.post_id=p.id
  WHERE p.activity_type IS NULL
  UNION ALL
  SELECT 'personal_story',s.media_storage_key,s.media_url
  FROM desporto24.social_story s
  WHERE s.community_id IS NULL
)
SELECT kind, count(*) AS total_media,
       count(*) FILTER (WHERE media_key IS NULL OR btrim(media_key)='') AS missing_keys,
       count(*) FILTER (
         WHERE media_key IS NOT NULL AND btrim(media_key)<>''
           AND media_key !~ '^(posts|stories|private-profile-media/(posts|stories))/[0-9]+/[A-Za-z0-9_-]+\.(jpg|jpeg|png|webp|mp4|webm)$'
       ) AS unsupported_keys,
       count(*) FILTER (
         WHERE media_key LIKE 'private-profile-media/%'
           AND (media_url IS NULL OR media_url NOT LIKE '/api/profile-media/%')
       ) AS protected_keys_with_stale_urls
FROM media GROUP BY kind ORDER BY kind;
