#!/bin/bash
set -euo pipefail

# Wrapper para registrar usuarios en el entorno de pruebas reutilizando register_users.sh
SCRIPT_DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)
RESOLVED_API_URL=${API_URL:-http://localhost:8083/api/v1/auth/signup}

API_URL="$RESOLVED_API_URL" "$SCRIPT_DIR/register_users.sh"
