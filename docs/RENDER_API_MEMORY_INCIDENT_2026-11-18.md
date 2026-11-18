# Jynlo API — Render 512 MiB OOM incident, 2026-11-18

Service: `desporto24-api` (`srv-darsqqgjo6nc739a10v0`), Render Frankfurt, plan `0.5c-512mb`, one instance. Current public URL in Render: `https://desporto24-preview-api.onrender.com` (production Spring profile).

## Confirmed incident
- Render event `server_failed` at 18:45:03 UTC; reason `oomKilled`, limit `512Mi`.
- The same instance was `server_available` at 18:46:28 UTC.
- Memory before restart approx. 536.1 MB / 536.9 MB limit; afterwards approx. 500 MB, still very near limit.
- Request metrics show HTTP 502 around restart; clients may show transient feed/session failures. **Do not assume every observed 502 is a unique affected user.**
- CPU during the buildup was generally low. These facts do not establish a specific memory leak.
- Email 2026-11-17 reported Render build-pipeline monthly spend cap `$15` reached; CI redeploys can fail until the cap is adjusted or billing resets (email says 2026-12-01).

## Low-risk remediation staged on develop (not yet in Render live image)
- `spring.datasource.hikari.maximum-pool-size=${JYNLO_DB_POOL_MAX:4}` (previously Hikari default max 10).
- `spring.datasource.hikari.minimum-idle=${JYNLO_DB_POOL_MIN_IDLE:1}` (previously default minimum equals maximum).
- Hikari idle timeout 30s and connection wait timeout 20s.
- `MALLOC_ARENA_MAX=2` to reduce native allocator fragmentation/retained arenas.
- Tomcat max threads 24 -> 16, minimum spare threads 3 -> 2.
- Preserve existing 160 MiB heap and 200 MiB metaspace caps: 144 MiB metaspace previously produced OOM under normal requests.
- Static regression test: `ProductionMemoryBudgetTest`.
- **Expected to help under low/moderate concurrency; not proof that a 512 MiB Spring application will remain under 512 MiB.**

## Release gate (before relying on this fix)
1. Ensure the Render build pipeline can deploy; do not repeatedly trigger builds after spending cap has been reached.
2. Apply on `develop` only, run Maven tests/boot smoke test before deploying; do not modify live env secrets.
3. During rollout, check /actuator/health, auth, feed, notifications and session endpoints without exposing tokens.
4. Observe memory 5, 15, 30, 60 minutes after startup and compare RSS to limit; ensure a meaningful margin. Review HTTP 5xx and runtime logs.
5. If memory still hovers around 500 MB, increase the service plan to `1c-2g` (2 GB RAM) rather than squeezing metaspace further. Scaling is a separate paid decision requiring account-owner authorization.
6. Validate push readiness separately: backend `jynlo.push.enabled` defaults to `false`, Expo access token and FCM/APNs require secret configuration, proper native build, and real-device delivery test. **Do not enable push blindly**.

## Rollback
- Revert the two runtime tuning commits in `Dockerfile` and `application-prod.properties` if smoke tests show regressions or connection exhaustion.
- Do not roll back unrelated mobile changes or reset production data.
- Existing installed mobile builds do not receive source changes until a new signed APK/IPA is distributed.
