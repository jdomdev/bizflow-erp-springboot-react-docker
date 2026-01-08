# syntax=docker/dockerfile:1.5

ARG FRONTEND_BUILDER_IMAGE=bizflow/frontend-builder:local
ARG FRONTEND_RUNTIME_IMAGE=bizflow/frontend-runtime:local

FROM ${FRONTEND_BUILDER_IMAGE} AS builder

WORKDIR /workspace

ARG VITE_API_URL
ENV VITE_API_URL=$VITE_API_URL

# Instalar dependencias primero para aprovechar el cache de capas
COPY package*.json ./
RUN npm install --legacy-peer-deps --no-audit || npm install

# Copiar el resto del código y generar el build de producción
COPY . .
RUN npm run build

FROM ${FRONTEND_RUNTIME_IMAGE}

USER root

# Configuración de Nginx y artefactos estáticos generados
COPY nginx.conf /etc/nginx/nginx.conf
COPY --from=builder /workspace/dist /usr/share/nginx/html
RUN printf "OK" > /usr/share/nginx/html/health/index.html \
  && chown -R appuser:appuser /usr/share/nginx/html

USER appuser

EXPOSE 80

HEALTHCHECK --interval=30s --timeout=3s --start-period=10s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost/health || exit 1

CMD ["nginx", "-g", "daemon off;"]
