# Makefile para Bizflow ERP - Construcción automática de imágenes Docker
# ===========================================================================
# Este Makefile automatiza la construcción de imágenes base y de aplicación,
# eliminando la necesidad de construir manualmente las base images.

# Variables de configuración
DOCKER := docker
DOCKER_BUILD := $(DOCKER) build
DOCKER_COMPOSE := docker compose

# Nombres de las imágenes base
BACKEND_BUILDER_IMAGE := bizflow/backend-builder:local
BACKEND_RUNTIME_IMAGE := bizflow/backend-runtime:local
FRONTEND_BUILDER_IMAGE := bizflow/frontend-builder:local
FRONTEND_RUNTIME_IMAGE := bizflow/frontend-runtime:local

# Directorios
BASE_DOCKER_DIR := docker/base
BACKEND_DIR := backend
FRONTEND_DIR := frontend

# Colores para output
COLOR_RESET := \033[0m
COLOR_BLUE := \033[0;34m
COLOR_GREEN := \033[0;32m
COLOR_YELLOW := \033[0;33m

# Target por defecto
.DEFAULT_GOAL := help

# ===========================================================================
# Targets principales
# ===========================================================================

.PHONY: help
help: ## Muestra esta ayuda
	@echo "$(COLOR_BLUE)Bizflow ERP - Docker Build Automation$(COLOR_RESET)"
	@echo ""
	@echo "$(COLOR_GREEN)Targets disponibles:$(COLOR_RESET)"
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | \
		awk 'BEGIN {FS = ":.*?## "}; {printf "  $(COLOR_YELLOW)%-20s$(COLOR_RESET) %s\n", $$1, $$2}'
	@echo ""

.PHONY: all
all: build-base-images build-apps ## Construye todo: imágenes base + aplicaciones

.PHONY: build-base-images
build-base-images: ## Construye todas las imágenes base
	@echo "$(COLOR_BLUE)==> Construyendo todas las imágenes base...$(COLOR_RESET)"
	@$(MAKE) build-backend-builder
	@$(MAKE) build-backend-runtime
	@$(MAKE) build-frontend-builder
	@$(MAKE) build-frontend-runtime
	@echo "$(COLOR_GREEN)✓ Todas las imágenes base construidas exitosamente$(COLOR_RESET)"

# ===========================================================================
# Targets para imágenes base individuales
# ===========================================================================

.PHONY: build-backend-builder
build-backend-builder: ## Construye la imagen base para compilar el backend (Maven + JDK)
	@echo "$(COLOR_BLUE)==> Construyendo $(BACKEND_BUILDER_IMAGE)...$(COLOR_RESET)"
	@$(DOCKER_BUILD) -f $(BASE_DOCKER_DIR)/backend-builder.Dockerfile \
		-t $(BACKEND_BUILDER_IMAGE) \
		$(BASE_DOCKER_DIR)
	@echo "$(COLOR_GREEN)✓ $(BACKEND_BUILDER_IMAGE) construida$(COLOR_RESET)"

.PHONY: build-backend-runtime
build-backend-runtime: ## Construye la imagen base de runtime del backend (JRE)
	@echo "$(COLOR_BLUE)==> Construyendo $(BACKEND_RUNTIME_IMAGE)...$(COLOR_RESET)"
	@$(DOCKER_BUILD) -f $(BASE_DOCKER_DIR)/backend-runtime.Dockerfile \
		-t $(BACKEND_RUNTIME_IMAGE) \
		$(BASE_DOCKER_DIR)
	@echo "$(COLOR_GREEN)✓ $(BACKEND_RUNTIME_IMAGE) construida$(COLOR_RESET)"

.PHONY: build-frontend-builder
build-frontend-builder: ## Construye la imagen base para compilar el frontend (Node + npm)
	@echo "$(COLOR_BLUE)==> Construyendo $(FRONTEND_BUILDER_IMAGE)...$(COLOR_RESET)"
	@$(DOCKER_BUILD) -f $(BASE_DOCKER_DIR)/frontend-builder.Dockerfile \
		-t $(FRONTEND_BUILDER_IMAGE) \
		$(BASE_DOCKER_DIR)
	@echo "$(COLOR_GREEN)✓ $(FRONTEND_BUILDER_IMAGE) construida$(COLOR_RESET)"

.PHONY: build-frontend-runtime
build-frontend-runtime: ## Construye la imagen base de runtime del frontend (nginx)
	@echo "$(COLOR_BLUE)==> Construyendo $(FRONTEND_RUNTIME_IMAGE)...$(COLOR_RESET)"
	@$(DOCKER_BUILD) -f $(BASE_DOCKER_DIR)/frontend-runtime.Dockerfile \
		-t $(FRONTEND_RUNTIME_IMAGE) \
		$(BASE_DOCKER_DIR)
	@echo "$(COLOR_GREEN)✓ $(FRONTEND_RUNTIME_IMAGE) construida$(COLOR_RESET)"

# ===========================================================================
# Targets para construir aplicaciones
# ===========================================================================

.PHONY: build-apps
build-apps: ## Construye las aplicaciones backend y frontend (requiere imágenes base)
	@echo "$(COLOR_BLUE)==> Construyendo aplicaciones...$(COLOR_RESET)"
	@$(DOCKER_COMPOSE) build
	@echo "$(COLOR_GREEN)✓ Aplicaciones construidas exitosamente$(COLOR_RESET)"

.PHONY: build-apps-clean
build-apps-clean: ## Construye las aplicaciones sin cache (para builds limpios)
	@echo "$(COLOR_BLUE)==> Construyendo aplicaciones (sin cache)...$(COLOR_RESET)"
	@$(DOCKER_COMPOSE) build --no-cache
	@echo "$(COLOR_GREEN)✓ Aplicaciones construidas exitosamente$(COLOR_RESET)"

.PHONY: build-backend
build-backend: build-backend-builder build-backend-runtime ## Construye solo el backend (base + app)
	@echo "$(COLOR_BLUE)==> Construyendo aplicación backend...$(COLOR_RESET)"
	@$(DOCKER_COMPOSE) build backend-dev backend-test backend-prod
	@echo "$(COLOR_GREEN)✓ Backend construido$(COLOR_RESET)"

.PHONY: build-frontend
build-frontend: build-frontend-builder build-frontend-runtime ## Construye solo el frontend (base + app)
	@echo "$(COLOR_BLUE)==> Construyendo aplicación frontend...$(COLOR_RESET)"
	@$(DOCKER_COMPOSE) build frontend-dev frontend-test frontend-prod
	@echo "$(COLOR_GREEN)✓ Frontend construido$(COLOR_RESET)"

# ===========================================================================
# Targets de limpieza
# ===========================================================================

.PHONY: clean-base-images
clean-base-images: ## Elimina las imágenes base locales
	@echo "$(COLOR_YELLOW)==> Eliminando imágenes base...$(COLOR_RESET)"
	-@$(DOCKER) rmi $(BACKEND_BUILDER_IMAGE) 2>/dev/null || true
	-@$(DOCKER) rmi $(BACKEND_RUNTIME_IMAGE) 2>/dev/null || true
	-@$(DOCKER) rmi $(FRONTEND_BUILDER_IMAGE) 2>/dev/null || true
	-@$(DOCKER) rmi $(FRONTEND_RUNTIME_IMAGE) 2>/dev/null || true
	@echo "$(COLOR_GREEN)✓ Imágenes base eliminadas$(COLOR_RESET)"

.PHONY: clean-all
clean-all: ## Elimina todas las imágenes Docker del proyecto
	@echo "$(COLOR_YELLOW)==> Eliminando todas las imágenes...$(COLOR_RESET)"
	@$(DOCKER_COMPOSE) down --rmi all -v
	@$(MAKE) clean-base-images
	@echo "$(COLOR_GREEN)✓ Todas las imágenes eliminadas$(COLOR_RESET)"

# ===========================================================================
# Targets de utilidad
# ===========================================================================

.PHONY: list-images
list-images: ## Lista las imágenes Docker del proyecto
	@echo "$(COLOR_BLUE)Imágenes base:$(COLOR_RESET)"
	@$(DOCKER) images | grep "bizflow/" || echo "  (ninguna encontrada)"
	@echo ""
	@echo "$(COLOR_BLUE)Imágenes de aplicación:$(COLOR_RESET)"
	@$(DOCKER) images | grep "bizflow-erp" || echo "  (ninguna encontrada)"

.PHONY: check-base-images
check-base-images: ## Verifica si las imágenes base existen
	@echo "$(COLOR_BLUE)Verificando imágenes base...$(COLOR_RESET)"
	@if $(DOCKER) image inspect $(BACKEND_BUILDER_IMAGE) >/dev/null 2>&1; then \
		echo "$(COLOR_GREEN)✓ $(BACKEND_BUILDER_IMAGE)$(COLOR_RESET)"; \
	else \
		echo "$(COLOR_YELLOW)✗ $(BACKEND_BUILDER_IMAGE) no encontrada$(COLOR_RESET)"; \
	fi
	@if $(DOCKER) image inspect $(BACKEND_RUNTIME_IMAGE) >/dev/null 2>&1; then \
		echo "$(COLOR_GREEN)✓ $(BACKEND_RUNTIME_IMAGE)$(COLOR_RESET)"; \
	else \
		echo "$(COLOR_YELLOW)✗ $(BACKEND_RUNTIME_IMAGE) no encontrada$(COLOR_RESET)"; \
	fi
	@if $(DOCKER) image inspect $(FRONTEND_BUILDER_IMAGE) >/dev/null 2>&1; then \
		echo "$(COLOR_GREEN)✓ $(FRONTEND_BUILDER_IMAGE)$(COLOR_RESET)"; \
	else \
		echo "$(COLOR_YELLOW)✗ $(FRONTEND_BUILDER_IMAGE) no encontrada$(COLOR_RESET)"; \
	fi
	@if $(DOCKER) image inspect $(FRONTEND_RUNTIME_IMAGE) >/dev/null 2>&1; then \
		echo "$(COLOR_GREEN)✓ $(FRONTEND_RUNTIME_IMAGE)$(COLOR_RESET)"; \
	else \
		echo "$(COLOR_YELLOW)✗ $(FRONTEND_RUNTIME_IMAGE) no encontrada$(COLOR_RESET)"; \
	fi

# ===========================================================================
# Targets de desarrollo rápido
# ===========================================================================

.PHONY: generate-sql-hashes
generate-sql-hashes: ## Genera SQL bootstrap desde secrets (sincroniza hashes)
	@echo "$(COLOR_BLUE)==> Regenerando SQL bootstrap desde secrets...$(COLOR_RESET)"
	@python3 scripts/utils/generate_password_hashes.py --generate
	@echo "$(COLOR_GREEN)✓ SQL bootstrap regenerado$(COLOR_RESET)"

.PHONY: verify-sql-hashes
verify-sql-hashes: ## Verifica que los hashes SQL coinciden con secrets
	@python3 scripts/utils/generate_password_hashes.py --verify

.PHONY: generate-credentials
generate-credentials: ## Genera passwords determinísticos para todos los entornos
	@echo "$(COLOR_BLUE)==> Generando credenciales...$(COLOR_RESET)"
	@python3 scripts/secrets/generate_user_credentials.py --generate
	@echo "$(COLOR_GREEN)✓ Credenciales generadas$(COLOR_RESET)"

.PHONY: show-password-formula
show-password-formula: ## Muestra la fórmula de generación de passwords
	@python3 scripts/secrets/generate_user_credentials.py --show-formula

.PHONY: regenerate-all-credentials
regenerate-all-credentials: generate-credentials generate-sql-hashes ## Regenera credenciales + hashes SQL
	@echo "$(COLOR_GREEN)✓ Credenciales y hashes SQL regenerados$(COLOR_RESET)"
	@echo "$(COLOR_YELLOW)   Ejecuta 'make recreate-dev' para aplicar cambios$(COLOR_RESET)"

.PHONY: up-dev
up-dev: build-base-images ## Construye imágenes base y levanta el entorno de desarrollo
	@echo "$(COLOR_BLUE)==> Iniciando entorno de desarrollo...$(COLOR_RESET)"
	@$(DOCKER_COMPOSE) --profile dev up --build -d
	@echo "$(COLOR_GREEN)✓ Entorno de desarrollo iniciado$(COLOR_RESET)"

.PHONY: up-prod
up-prod: build-base-images ## Construye imágenes base y levanta el entorno de producción
	@echo "$(COLOR_BLUE)==> Iniciando entorno de producción...$(COLOR_RESET)"
	@$(DOCKER_COMPOSE) --profile prod up --build -d
	@echo "$(COLOR_GREEN)✓ Entorno de producción iniciado$(COLOR_RESET)"

.PHONY: up-test
up-test: build-base-images ## Construye imágenes base y levanta el entorno de testing
	@echo "$(COLOR_BLUE)==> Iniciando entorno de testing...$(COLOR_RESET)"
	@$(DOCKER_COMPOSE) --profile test up --build -d
	@echo "$(COLOR_GREEN)✓ Entorno de testing iniciado$(COLOR_RESET)"

.PHONY: rebuild
rebuild: clean-base-images all ## Limpia y reconstruye todo desde cero
	@echo "$(COLOR_GREEN)✓ Reconstrucción completa finalizada$(COLOR_RESET)"

# ===========================================================================
# Targets de backup y seguridad
# ===========================================================================

.PHONY: backup-dev
backup-dev: ## Crea backup de la base de datos de desarrollo
	@FECHA=$$(date +%Y%m%d_%H%M%S) && \
	echo "$(COLOR_BLUE)==> Creando backup DEV: $${FECHA}_erp_dev_db.dump$(COLOR_RESET)" && \
	docker exec erp-dev-db-container pg_dump -U erp_dev_user -Fc erp_dev_db > backups/dev/$${FECHA}_erp_dev_db.dump && \
	echo "$(COLOR_GREEN)✓ Backup DEV creado: backups/dev/$${FECHA}_erp_dev_db.dump$(COLOR_RESET)"

.PHONY: backup-prod
backup-prod: ## Crea backup de la base de datos de producción
	@FECHA=$$(date +%Y%m%d_%H%M%S) && \
	echo "$(COLOR_BLUE)==> Creando backup PROD: $${FECHA}_erp_prod_db.dump$(COLOR_RESET)" && \
	docker exec erp-prod-db-container pg_dump -U erp_prod_user -Fc erp_prod_db > backups/prod/$${FECHA}_erp_prod_db.dump && \
	echo "$(COLOR_GREEN)✓ Backup PROD creado: backups/prod/$${FECHA}_erp_prod_db.dump$(COLOR_RESET)"

.PHONY: backup-test
backup-test: ## Crea backup de la base de datos de testing
	@FECHA=$$(date +%Y%m%d_%H%M%S) && \
	echo "$(COLOR_BLUE)==> Creando backup TEST: $${FECHA}_erp_test_db.dump$(COLOR_RESET)" && \
	docker exec erp-test-db-container pg_dump -U erp_test_user -Fc erp_test_db > backups/test/$${FECHA}_erp_test_db.dump && \
	echo "$(COLOR_GREEN)✓ Backup TEST creado: backups/test/$${FECHA}_erp_test_db.dump$(COLOR_RESET)"

.PHONY: backup-all
backup-all: backup-dev backup-prod backup-test ## Crea backup de todas las bases de datos

.PHONY: down-dev
down-dev: ## Detiene el entorno de desarrollo (conserva datos)
	@echo "$(COLOR_BLUE)==> Deteniendo entorno DEV...$(COLOR_RESET)"
	@$(DOCKER_COMPOSE) --profile dev down
	@echo "$(COLOR_GREEN)✓ Entorno DEV detenido (datos conservados)$(COLOR_RESET)"

.PHONY: down-prod
down-prod: ## Detiene el entorno de producción (conserva datos) - SEGURO
	@echo "$(COLOR_BLUE)==> Deteniendo entorno PROD...$(COLOR_RESET)"
	@$(DOCKER_COMPOSE) --profile prod down
	@echo "$(COLOR_GREEN)✓ Entorno PROD detenido (datos conservados)$(COLOR_RESET)"

.PHONY: down-test
down-test: ## Detiene el entorno de testing (conserva datos)
	@echo "$(COLOR_BLUE)==> Deteniendo entorno TEST...$(COLOR_RESET)"
	@$(DOCKER_COMPOSE) --profile test down
	@echo "$(COLOR_GREEN)✓ Entorno TEST detenido (datos conservados)$(COLOR_RESET)"

.PHONY: down-prod-with-volumes
down-prod-with-volumes: ## ⛔ PELIGROSO: Elimina contenedores Y datos de producción
	@echo "$(COLOR_YELLOW)⚠️  ADVERTENCIA: Esto eliminará TODOS los datos de producción$(COLOR_RESET)"
	@echo "$(COLOR_YELLOW)   Tienes 5 segundos para cancelar con Ctrl+C...$(COLOR_RESET)"
	@sleep 5
	@$(MAKE) backup-prod
	@echo "$(COLOR_BLUE)==> Eliminando entorno PROD con volúmenes...$(COLOR_RESET)"
	@$(DOCKER_COMPOSE) --profile prod down -v
	@echo "$(COLOR_GREEN)✓ Entorno PROD eliminado (backup creado previamente)$(COLOR_RESET)"

# ===========================================================================
# Targets de recreación completa (reinicia BD desde cero)
# ===========================================================================

.PHONY: recreate-dev
recreate-dev: ## Recrea DEV desde cero (elimina volumen, levanta). Ejecuta 'make generate-sql-hashes' si cambiaste passwords.
	@echo "$(COLOR_BLUE)==> Recreando entorno DEV desde cero...$(COLOR_RESET)"
	@$(DOCKER_COMPOSE) --profile dev down -v
	@$(MAKE) up-dev
	@echo "$(COLOR_GREEN)✓ Entorno DEV recreado$(COLOR_RESET)"
	@echo "$(COLOR_YELLOW)💡 Si cambiaste passwords, ejecuta: make generate-sql-hashes$(COLOR_RESET)"

.PHONY: recreate-test
recreate-test: ## Recrea TEST desde cero (elimina volumen, levanta). Ejecuta 'make generate-sql-hashes' si cambiaste passwords.
	@echo "$(COLOR_BLUE)==> Recreando entorno TEST desde cero...$(COLOR_RESET)"
	@$(DOCKER_COMPOSE) --profile test down -v
	@$(MAKE) up-test
	@echo "$(COLOR_GREEN)✓ Entorno TEST recreado$(COLOR_RESET)"
	@echo "$(COLOR_YELLOW)💡 Si cambiaste passwords, ejecuta: make generate-sql-hashes$(COLOR_RESET)"

.PHONY: recreate-prod
recreate-prod: ## ⛔ PELIGROSO: Recrea PROD desde cero (backup + reinicia). Ejecuta 'make generate-sql-hashes' si cambiaste passwords.
	@echo "$(COLOR_YELLOW)⚠️  ADVERTENCIA: Esto eliminará TODOS los datos de producción$(COLOR_RESET)"
	@echo "$(COLOR_YELLOW)   Tienes 5 segundos para cancelar con Ctrl+C...$(COLOR_RESET)"
	@sleep 5
	@$(MAKE) backup-prod
	@echo "$(COLOR_BLUE)==> Recreando entorno PROD desde cero...$(COLOR_RESET)"
	@$(DOCKER_COMPOSE) --profile prod down -v
	@$(MAKE) up-prod
	@echo "$(COLOR_GREEN)✓ Entorno PROD recreado (backup disponible en backups/prod/)$(COLOR_RESET)"
	@echo "$(COLOR_YELLOW)💡 Si cambiaste passwords, ejecuta: make generate-sql-hashes$(COLOR_RESET)"
