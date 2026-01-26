#!/bin/bash
# =============================================================================
# docker-compose-safe.sh - Wrapper seguro para docker compose
# =============================================================================
# Previene la eliminación accidental de volúmenes en producción.
# 
# Uso: ./scripts/utils/docker-compose-safe.sh [argumentos de docker compose]
#
# En lugar de: docker compose --profile prod down -v
# Usar:        ./scripts/utils/docker-compose-safe.sh --profile prod down -v
#              (El script bloqueará la operación peligrosa)
# =============================================================================

set -e

# Colores
RED='\033[0;31m'
YELLOW='\033[1;33m'
GREEN='\033[0;32m'
NC='\033[0m' # Sin color

# Detectar si es producción
IS_PROD=false
for arg in "$@"; do
    if [[ "$arg" == "prod" ]] || [[ "$arg" == "--profile=prod" ]]; then
        IS_PROD=true
        break
    fi
done

# Detectar si se intenta eliminar volúmenes
HAS_VOLUME_FLAG=false
HAS_DOWN=false
for arg in "$@"; do
    if [[ "$arg" == "-v" ]] || [[ "$arg" == "--volumes" ]] || [[ "$arg" == "-v," ]]; then
        HAS_VOLUME_FLAG=true
    fi
    if [[ "$arg" == "down" ]]; then
        HAS_DOWN=true
    fi
done

# Bloquear operación peligrosa en producción
if $IS_PROD && $HAS_DOWN && $HAS_VOLUME_FLAG; then
    echo -e "${RED}╔═══════════════════════════════════════════════════════════════╗${NC}"
    echo -e "${RED}║  ⛔ OPERACIÓN BLOQUEADA: No se permite 'down -v' en PROD      ║${NC}"
    echo -e "${RED}╠═══════════════════════════════════════════════════════════════╣${NC}"
    echo -e "${RED}║  Eliminar volúmenes en producción causaría PÉRDIDA DE DATOS.  ║${NC}"
    echo -e "${RED}║                                                               ║${NC}"
    echo -e "${RED}║  Si realmente necesitas hacer esto:                           ║${NC}"
    echo -e "${RED}║  1. Crea un backup primero:                                   ║${NC}"
    echo -e "${RED}║     make backup-prod                                          ║${NC}"
    echo -e "${RED}║  2. Usa docker compose directamente (bajo tu responsabilidad):║${NC}"
    echo -e "${RED}║     docker compose --profile prod down -v                     ║${NC}"
    echo -e "${RED}╚═══════════════════════════════════════════════════════════════╝${NC}"
    exit 1
fi

# Advertencia para down en producción (sin -v)
if $IS_PROD && $HAS_DOWN && ! $HAS_VOLUME_FLAG; then
    echo -e "${YELLOW}⚠️  Deteniendo contenedores de PRODUCCIÓN (los datos están seguros)${NC}"
fi

# Ejecutar docker compose normalmente
docker compose "$@"
