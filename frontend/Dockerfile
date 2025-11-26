# Frontend Dockerfile
# Multi-stage build for React + Vite

# Stage 1: Build
FROM node:20-alpine AS builder

WORKDIR /app

# Update npm to latest
RUN npm install -g npm@latest

# Copy package files
COPY package*.json ./

# Install dependencies with npm
# Using --legacy-peer-deps to avoid peer dependency conflicts
RUN npm install --legacy-peer-deps --no-audit || npm install

# Copy source code
COPY . .

# Build application
RUN npm run build

# Verify build output
RUN ls -la dist/ || echo "Build output directory"

# Stage 2: Runtime (nginx)
FROM nginx:alpine

# Install wget for health checks
RUN apk add --no-cache wget curl

# Copy nginx configuration
COPY nginx.conf /etc/nginx/nginx.conf

# Copy built application from builder
COPY --from=builder /app/dist /usr/share/nginx/html

# Create health check endpoint handler
RUN mkdir -p /usr/share/nginx/html/health
RUN echo "OK" > /usr/share/nginx/html/health/index.html

# Create cache and temp directories with proper permissions
RUN mkdir -p /var/cache/nginx/client_temp /var/run/nginx \
    && chmod -R 777 /var/cache/nginx /var/run/nginx \
    && touch /var/run/nginx.pid \
    && chmod 666 /var/run/nginx.pid

# Create non-root user (handle if it already exists)
RUN addgroup -g 1000 appuser && adduser -D -u 1000 -G appuser appuser || true

# Change permissions
RUN chown -R appuser:appuser /usr/share/nginx/html
RUN chmod -R 755 /usr/share/nginx/html

# Switch to non-root user
USER appuser

# Expose port
EXPOSE 80

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=10s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost/health || exit 1

# Start nginx
CMD ["nginx", "-g", "daemon off;"]
