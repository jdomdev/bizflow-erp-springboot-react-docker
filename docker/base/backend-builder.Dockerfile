# syntax=docker/dockerfile:1.5
ARG MAVEN_VERSION=3.9.5
ARG TEMURIN_VERSION=21
FROM maven:${MAVEN_VERSION}-eclipse-temurin-${TEMURIN_VERSION}-alpine

ARG APP_UID=1000
ARG APP_GID=1000

# Prepare workspace and Maven cache directories with consistent ownership
RUN addgroup -g ${APP_GID} app \
  && adduser -D -u ${APP_UID} -G app app \
  && install -d -o app -g app /workspace \
  && install -d -o app -g app /maven/.m2/repository

ENV MAVEN_CONFIG=/maven/.m2
ENV MAVEN_OPTS="-Dmaven.repo.local=/maven/.m2/repository"

WORKDIR /workspace
