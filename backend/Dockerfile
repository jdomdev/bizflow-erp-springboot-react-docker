# syntax=docker/dockerfile:1.5

ARG BACKEND_BUILDER_IMAGE=bizflow/backend-builder:local
ARG BACKEND_RUNTIME_IMAGE=bizflow/backend-runtime:local

FROM ${BACKEND_BUILDER_IMAGE} AS builder

WORKDIR /workspace

# Preload Maven wrapper and project metadata for dependency resolution
COPY .mvn ./.mvn
COPY mvnw ./mvnw
COPY pom.xml ./
RUN chmod +x mvnw \
  && ./mvnw -B -ntp dependency:go-offline

# Copy source code and build the executable JAR
COPY src ./src
RUN ./mvnw -B -ntp clean package -DskipTests

FROM ${BACKEND_RUNTIME_IMAGE}

USER root

# Copy the application artifact produced in the builder stage
COPY --from=builder /build/backend/bizflowerp-*.jar /app/app.jar
RUN chown app:app /app/app.jar

USER app

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
