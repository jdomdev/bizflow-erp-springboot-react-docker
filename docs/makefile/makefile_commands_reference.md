# Makefile Commands Reference - Bizflow ERP

> Referencia completa de comandos disponibles en el Makefile del proyecto.
> Ejecuta `make help` para ver la ayuda interactiva.

---

## Construcción de imágenes

| Comando | Descripción |
|---------|-------------|
| `make all` | Construye todo: imágenes base + aplicaciones |
| `make build-base-images` | Construye las 4 imágenes base (builder y runtime para backend y frontend) |
| `make build-backend-builder` | Imagen base con Maven + JDK para compilar backend |
| `make build-backend-runtime` | Imagen base con JRE para ejecutar backend |
| `make build-frontend-builder` | Imagen base con Node + npm para compilar frontend |
| `make build-frontend-runtime` | Imagen base con nginx para servir frontend |
| `make build-apps` | Construye aplicaciones backend y frontend (requiere imágenes base) |
| `make build-apps-clean` | Igual pero sin cache (build limpio) |
| `make build-backend` | Construye solo backend (base + app) |
| `make build-frontend` | Construye solo frontend (base + app) |

---

## Gestión de entornos

| Comando | Descripción |
|---------|-------------|
| `make up-dev` | Levanta entorno de desarrollo (construye imágenes base primero) |
| `make up-prod` | Levanta entorno de producción |
| `make up-test` | Levanta entorno de testing |
| `make down-dev` | Detiene DEV (conserva datos) |
| `make down-prod` | Detiene PROD (conserva datos) - seguro |
| `make down-test` | Detiene TEST (conserva datos) |
| `make down-prod-with-volumes` | ⛔ Elimina PROD incluyendo datos (hace backup antes) |

---

## Recreación completa (reinicia BD desde cero)

| Comando | Descripción |
|---------|-------------|
| `make recreate-dev` | Elimina volumen DEV y levanta desde cero |
| `make recreate-test` | Elimina volumen TEST y levanta desde cero |
| `make recreate-prod` | ⛔ Elimina PROD (hace backup), levanta desde cero |

> **Nota:** Si cambiaste passwords, ejecuta `make generate-sql-hashes` antes de recrear.

---

## Backups

| Comando | Descripción |
|---------|-------------|
| `make backup-dev` | Crea dump de la BD de desarrollo → `backups/dev/` |
| `make backup-prod` | Crea dump de la BD de producción → `backups/prod/` |
| `make backup-test` | Crea dump de la BD de testing → `backups/test/` |
| `make backup-all` | Hace backup de las 3 BDs |

---

## Credenciales y seguridad

| Comando | Descripción |
|---------|-------------|
| `make generate-sql-hashes` | Regenera SQL bootstrap desde secrets |
| `make verify-sql-hashes` | Verifica que los hashes SQL coinciden |
| `make generate-credentials` | Genera passwords determinísticos |
| `make show-password-formula` | Muestra la fórmula de generación |
| `make regenerate-all-credentials` | Regenera credenciales + hashes SQL |

---

## Utilidad

| Comando | Descripción |
|---------|-------------|
| `make help` | Muestra la ayuda con todos los targets |
| `make list-images` | Lista imágenes Docker del proyecto |
| `make check-base-images` | Verifica si existen las imágenes base |
| `make clean-base-images` | Elimina imágenes base locales |
| `make clean-all` | Elimina todas las imágenes del proyecto |
| `make rebuild` | Limpia todo y reconstruye desde cero |

---

## Uso recomendado por escenario

### Desarrollo diario
```bash
# Tras cambios en código Java
make build-backend

# Tras cambios en código React
make build-frontend
```

### Nuevo setup del proyecto
```bash
# Primera vez o tras clonar
make all
make up-dev
```

### Problemas con la BD
```bash
# Backup + reinicio limpio
make backup-dev
make recreate-dev
```

### Antes de operaciones destructivas
```bash
make backup-all
```

### Verificar estado
```bash
make check-base-images
make list-images
```

---

## Arquitectura de imágenes

```
┌─────────────────────────────────────────────────────────────┐
│                     IMÁGENES BASE                           │
├─────────────────────────────────────────────────────────────┤
│  bizflow/backend-builder:local   → Maven + JDK (compilar)   │
│  bizflow/backend-runtime:local   → JRE (ejecutar)           │
│  bizflow/frontend-builder:local  → Node + npm (compilar)    │
│  bizflow/frontend-runtime:local  → nginx (servir)           │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│                  IMÁGENES DE APLICACIÓN                     │
├─────────────────────────────────────────────────────────────┤
│  bizflow-erp-...-backend:latest                             │
│  bizflow-erp-...-frontend-dev:latest                        │
│  bizflow-erp-...-frontend-prod:latest                       │
│  bizflow-erp-...-frontend-test:latest                       │
│  bizflow-erp-...-api-seeder-dev:latest                      │
│  bizflow-erp-...-api-seeder-prod:latest                     │
│  bizflow-erp-...-api-seeder-test:latest                     │
└─────────────────────────────────────────────────────────────┘
```

---

*Documento generado: 2026-02-07*
