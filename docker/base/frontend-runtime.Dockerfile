# syntax=docker/dockerfile:1.5
ARG NGINX_VARIANT=alpine
FROM nginx:${NGINX_VARIANT}

ARG APP_UID=1000
ARG APP_GID=1000

# Install health-check tooling and prepare runtime directories
RUN apk add --no-cache wget curl \
  && addgroup -g ${APP_GID} appuser \
  && adduser -D -u ${APP_UID} -G appuser appuser \
  && install -d -o appuser -g appuser /usr/share/nginx/html \
  && install -d -o appuser -g appuser /usr/share/nginx/html/health \
  && install -d -o appuser -g appuser /var/cache/nginx \
  && install -d -o appuser -g appuser /var/run/nginx \
  && touch /var/run/nginx.pid \
  && chown appuser:appuser /var/run/nginx.pid

WORKDIR /usr/share/nginx/html
USER appuser
