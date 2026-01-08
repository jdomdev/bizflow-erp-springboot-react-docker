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
