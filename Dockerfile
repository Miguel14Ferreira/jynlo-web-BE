FROM eclipse-temurin:21-jdk-jammy AS build

WORKDIR /workspace
COPY . .
RUN chmod +x mvnw && ./mvnw -q -DskipTests package

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

# Keep a hard memory budget for the Render 512 MiB instance.
# A 144 MiB metaspace cap produced repeated OutOfMemoryError on normal HTTP requests.
# Rebalance the 512 MiB instance: reserve more class metadata by bounding
# heap, direct buffers, thread stacks and Tomcat worker count.
# Encourage the Serial GC to return unused heap/metaspace sooner after bursts,
# while retaining the already-validated 160 MiB heap and 200 MiB metaspace caps.
# Lower idle heap floor and reduce native memory pressure from Tomcat workers.
# Validate post-deploy RSS and class-loading pressure before reducing hard caps.
ENTRYPOINT ["java", "-Xms32m", "-Xmx160m", "-XX:MinHeapFreeRatio=10", "-XX:MaxHeapFreeRatio=20", "-XX:MaxMetaspaceSize=200m", "-XX:MinMetaspaceFreeRatio=0", "-XX:MaxMetaspaceFreeRatio=10", "-XX:ReservedCodeCacheSize=20m", "-XX:MaxDirectMemorySize=20m", "-Xss512k", "-XX:ActiveProcessorCount=2", "-XX:+UseSerialGC", "-Dserver.tomcat.threads.max=12", "-Dserver.tomcat.threads.min-spare=2", "-jar", "/app/app.jar"]
