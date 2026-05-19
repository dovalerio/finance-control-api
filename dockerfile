# ---------- build stage ----------
FROM gradle:9.3.1-jdk21 AS builder

WORKDIR /workspace

COPY gradle gradle
COPY gradlew settings.gradle.kts build.gradle.kts ./
RUN chmod +x gradlew

COPY src src

# -x test: testes são executados na pipeline CI, não no build Docker
RUN ./gradlew clean bootJar -x test --no-daemon

# ---------- runtime stage ----------
FROM eclipse-temurin:21-jre-ubi9-minimal

WORKDIR /app

# create non-root user
RUN useradd spring

COPY --from=builder /workspace/build/libs/*.jar app.jar

RUN mkdir /app/logs && chown spring /app/logs

USER spring

EXPOSE 8080

ENV SPRING_PROFILES_ACTIVE=prod

HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -sf http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java","-XX:MaxRAMPercentage=75","-jar","/app/app.jar"]