#!/bin/bash
# Script para ejecutar los tests del backend en un contenedor Docker
# Uso: ./scripts/tests/run_backend_tests.sh

set -e

SCRIPT_DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)
PROJECT_ROOT=$(cd "$SCRIPT_DIR/../.." && pwd)
cd "$PROJECT_ROOT/backend"

# 1. Levantar el contenedor de la base de datos de test si no está corriendo
docker compose up -d erp-test-db-container

# 2. Construir la imagen de test
docker build -f Dockerfile.test -t bizflow-backend-test .

# 2b. Asegurar carpeta de build compartida
mkdir -p "$PROJECT_ROOT/build/backend"

# 3. Ejecutar los tests en la red de docker-compose
docker run --rm --network=bizflow-erp-springboot-react-docker_bizflow_erp_network \
  -v ~/.m2:/root/.m2 \
  -v $(pwd)/src:/app/src \
  -v $(pwd)/../build/backend:/build/backend \
  bizflow-backend-test
