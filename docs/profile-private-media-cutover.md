# Jynlo private profile media cutover — production gate

Status: **not cut over**. `jynlo.profile.private-mode-enabled` and
`jynlo.profile.private-media-cutover-verified` must remain disabled until
all checks below are satisfied.

## Policy

- Public profile: personal posts and stories can be read by any signed-in Jynlo user.
- Private profile: the author and approved followers can read posts and personal stories.
- Existing followers remain approved on a public-to-private change.
- Blocked profiles must not access protected media, even with an old permalink.
- Community and session visibility rules are independent.
- Profile picture and basic biography remain discoverable.

## Implemented foundation

- `PrivateProfileMediaStorage` accepts only server-generated keys in a
  separate private storage root/bucket; never falls back to the public bucket.
- `PrivateProfileMediaController` serves authenticated
  `/api/profile-media/posts/{id}/{image|video|extra-N}` and
  `/api/profile-media/stories/{id}`, checking authorization each request.
- New media uploads for an already-private author use the protected bucket.
- API feed/story responses map protected storage keys to authenticated paths.
- `/api/profile/privacy/me/media-readiness` reports the remaining legacy
  public references (including expired stories) for the signed-in account.
- Both web and mobile have authenticated media download helpers.

## Required operational migration — NOT automatic

1. Verify the private R2 bucket is **not publicly accessible** via custom
   domain, r2.dev or public bucket grants. Configure dedicated private
   read/write credentials and secure Render environment variables.
2. Back up the PostgreSQL schema and take an inventory of public post/story
   keys and URLs. Run a copy-only migration to private R2 objects with
   validated content type, size and checksums. Do not delete originals yet.
3. Update the referenced objects and media keys atomically in the database,
   preserving post IDs, album ordering, story metadata and ownership.
4. Verify authorized media downloads on web and mobile, including images,
   albums and video playback. Test blocked, unauthenticated, pending-follow
   and newly-unfollowed viewers; all must get 401/404, not media bytes.
5. Delete the legacy public objects only after verified private copies exist
   and DB commits are durable. Track and retry failed deletions.
6. Purge the exact deleted URLs from the public CDN/cache and check that
   anonymous direct requests to every old URL fail. R2 custom-domain cache
   can continue to serve a deleted object until purged.
7. Audit `/api/profile/privacy/me/media-readiness` for affected accounts:
   publicReferences must be zero. Repeat after uploads and media edits.
8. Validate both private-to-public and public-to-private transitions,
   revoked followers, existing followers, session/community content isolation
   and app sign-out cache cleanup.
9. Only after completing these checks should operators set both
   `jynlo.profile.private-mode-enabled=true` and
   `jynlo.profile.private-media-cutover-verified=true`.

**Never enable the private switch based on frontend visibility alone.**
The current code does NOT perform the copy, deletion or CDN purge in steps
2–6. These require an explicit audited migration and infrastructure access.
