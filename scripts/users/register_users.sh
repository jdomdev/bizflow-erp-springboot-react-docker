#!/bin/bash
set -euo pipefail

# Registra usuarios de gastos vía /api/v1/auth/signup
# Por defecto apunta al backend local; sobreescribe API_URL para otros entornos.
: "${API_URL:=http://localhost:8081/api/v1/auth/signup}"

if ! command -v curl >/dev/null 2>&1; then
	echo "curl no está disponible en el entorno." >&2
	exit 1
fi


SCRIPT_DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)
DEFAULT_SEED_FILE="$SCRIPT_DIR/../secrets/register_users_payloads.jsonl"
SEED_FILE="${REGISTER_USERS_SEED_FILE:-}"

# Busca el archivo de payloads en una ubicación externa o en scripts/secrets (gitignored).
if [[ -z "$SEED_FILE" ]]; then
	if [[ -f "$DEFAULT_SEED_FILE" ]]; then
		SEED_FILE="$DEFAULT_SEED_FILE"
	else
		echo "REGISTER_USERS_SEED_FILE no está definido ni existe el archivo por defecto." >&2
		echo "Crea un archivo gitignored (por ejemplo scripts/secrets/register_users_payloads.jsonl) o exporta REGISTER_USERS_SEED_FILE." >&2
		exit 1
	fi
fi

if [[ ! -f "$SEED_FILE" ]]; then
	echo "No se encontró el archivo de payloads en: $SEED_FILE" >&2
	echo "Crea un archivo fuera del control de versiones con líneas JSON, una por usuario, por ejemplo:" >&2
	cat >&2 <<'EOF'
{"email":"usuario@example.com","name":"Nombre","surname":"Apellido","password":"<CONTRASENA>","employee_id":7}
EOF
	exit 1
fi

mapfile -t PAYLOADS < <(grep -v '^[[:space:]]*#' "$SEED_FILE" | grep -v '^[[:space:]]*$')

if [[ ${#PAYLOADS[@]} -eq 0 ]]; then
	echo "El archivo $SEED_FILE no contiene payloads válidos." >&2
	exit 1
fi

tmp_response=$(mktemp)
trap 'rm -f "$tmp_response"' EXIT

for payload in "${PAYLOADS[@]}"; do
	email=$(printf '%s\n' "$payload" | sed -n 's/.*"email":"\([^"\]*\)".*/\1/p')
	http_code=$(curl --silent --show-error \
		--header "Content-Type: application/json" \
		--output "$tmp_response" \
		--write-out "%{http_code}" \
		--request POST "$API_URL" \
		--data "$payload")

	if [[ "$http_code" == "409" ]]; then
		printf "Omitido %s (ya existía, HTTP %s)\n" "$email" "$http_code"
		continue
	fi

	if [[ "$http_code" == "400" ]] && grep -qi "Email already registered" "$tmp_response"; then
		printf "Omitido %s (ya existía, HTTP %s)\n" "$email" "$http_code"
		continue
	fi

	if [[ "$http_code" != "201" && "$http_code" != "200" ]]; then
		printf "Error %s al registrar %s\n" "$http_code" "$email" >&2
		cat "$tmp_response" >&2
		exit 1
	fi

	printf "Registrado %s (HTTP %s)\n" "$email" "$http_code"
done

printf "Usuarios de gastos registrados con API_URL=%s usando SEED_FILE=%s\n" "$API_URL" "$SEED_FILE"
