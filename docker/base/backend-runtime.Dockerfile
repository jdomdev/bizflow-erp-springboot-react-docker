# syntax=docker/dockerfile:1.5
ARG TEMURIN_VERSION=21
FROM eclipse-temurin:${TEMURIN_VERSION}-jre-alpine

ARG APP_UID=1000
ARG APP_GID=1000

# Install tooling for health checks and create runtime user
RUN apk add --no-cache curl \
  && addgroup -g ${APP_GID} app \
  && adduser -D -u ${APP_UID} -G app app \
  && install -d -o app -g app /app \
  && install -d -o app -g app /app/logs

WORKDIR /app
USER app
