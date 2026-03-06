# ---------- build stage ----------
FROM gradle:9.3.1-jdk21 AS builder

WORKDIR /workspace

COPY gradle gradle
COPY gradlew settings.gradle.kts build.gradle.kts ./
RUN chmod +x gradlew

COPY src src

RUN ./gradlew clean bootJar --no-daemon

# ---------- runtime stage ----------
FROM eclipse-temurin:21-jre-ubi9-minimal

WORKDIR /app

# create non-root user
RUN useradd spring

COPY --from=builder /workspace/build/libs/*.jar app.jar

USER spring

EXPOSE 8080

ENV SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["java","-XX:MaxRAMPercentage=75","-jar","/app/app.jar"]