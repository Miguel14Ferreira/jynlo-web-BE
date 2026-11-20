FROM eclipse-temurin:21-jdk-jammy AS build

WORKDIR /workspace
COPY . .
# maven.test.skip also skips compiling tests: an out-of-date test must not
# block a production image. Tests run in CI (develop-verify workflow).
RUN chmod +x mvnw && ./mvnw -q -Dmaven.test.skip=true package

FROM eclipse-temurin:21-jre-jammy

ENV CONTAINER_PORT=8080
# Reduce glibc allocator arena retention in this thread-heavy JVM container.
ENV MALLOC_ARENA_MAX=2

RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/* \
    && groupadd --system desporto24 \
    && useradd --system --gid desporto24 --home /app --shell /usr/sbin/nologin desporto24 \
    && mkdir -p /app/data/media \
    && chown -R desporto24:desporto24 /app

WORKDIR /app
COPY --from=build --chown=desporto24:desporto24 /workspace/target/*.jar app.jar

USER desporto24

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=5 \
  CMD curl --fail --silent "http://127.0.0.1:${PORT:-${CONTAINER_PORT:-8080}}/actuator/health" >/dev/null || exit 1

# Render plan 1c-2g (2 GiB, 1 CPU) since the 2026-11-18 OOM incident.
# Size the JVM from the container limit instead of the old 512 MiB caps:
# heap up to 55% of RAM (~1.1 GiB), bounded metaspace/direct memory, and
# enough Tomcat workers for normal concurrency (DB pool: JYNLO_DB_POOL_MAX).
# If the plan is ever lowered back to 512 MiB, restore the previous flags
# (see git history) - 55% heap plus 256 MiB metaspace would not fit there.
ENTRYPOINT ["java", "-XX:InitialRAMPercentage=10", "-XX:MaxRAMPercentage=55", "-XX:MaxMetaspaceSize=256m", "-XX:ReservedCodeCacheSize=64m", "-XX:MaxDirectMemorySize=64m", "-Xss512k", "-XX:+ExitOnOutOfMemoryError", "-Dserver.tomcat.threads.max=40", "-Dserver.tomcat.threads.min-spare=4", "-jar", "/app/app.jar"]
