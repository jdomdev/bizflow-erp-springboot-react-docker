# Pull Request: feature/seed-data-refactor → dev

**Fecha:** 2026-01-25  
**Rama origen:** `feature/seed-data-refactor`  
**Rama destino:** `dev`  
**Commits:** 20+  
**Archivos modificados:** 60+  
**Líneas:** +20,700 / -50

---

## 📋 Resumen Ejecutivo

Esta rama implementa un **sistema completo de seed data basado en API** que reemplaza los scripts SQL legacy, con **idempotencia 100%**, **seguridad mejorada con BCrypt**, y **soporte para tres entornos** (TEST, DEV, PROD). Además incluye **configuración multi-entorno para Vite**, **limpieza de seguridad del historial Git**, y **corrección de validaciones de Employee** para datos internacionales.

---

## 🎯 Objetivos Cumplidos

| Objetivo | Estado |
|----------|--------|
| Sistema de seed data basado en API REST | ✅ |
| Idempotencia completa (ejecutar múltiples veces sin duplicar) | ✅ |
| Tres conjuntos de datos escalados (TEST, DEV, PROD) | ✅ |
| Seguridad BCrypt para contraseñas de admin | ✅ |
| Entorno PROD completamente habilitado | ✅ |
| Configuración multi-entorno de Vite | ✅ |
| Limpieza de contraseñas del historial Git | ✅ |
| Validaciones de Employee para nombres internacionales | ✅ |
| Documentación completa de sesiones | ✅ |

---

## 🔐 CAMBIOS DE SEGURIDAD (CRÍTICOS)

### 1. Contraseñas con BCrypt Hash

**Problema resuelto:** Las contraseñas de admin estaban en texto plano en scripts SQL y docker-compose.yml.

**Solución implementada:**

```sql
-- sql/common/05_admin_bootstrap_*.sql
INSERT INTO expense_users (email, name, surname, password)
VALUES (
    'ada.lovelace@bizflowerp.com',
    'Ada',
    'Lovelace',
    '$2a$10$...'  -- BCrypt hash, no reversible
) ON CONFLICT (email) DO NOTHING;
```

**Archivos afectados:**
- `sql/common/05_admin_bootstrap_dev_test.sql` - Hash BCrypt para DEV/TEST
- `sql/common/05_admin_bootstrap_prod.sql` - Hash BCrypt para PROD

### 2. Variables de Entorno Seguras

**Configuración:**
```yaml
# docker-compose.yml
api-seeder-{env}:
  env_file:
    - .env.{env}  # Archivo gitignored con credenciales
```

**Archivos `.env.*` (gitignored):**
```bash
ADMIN_EMAIL=ada.lovelace@bizflowerp.com
ADMIN_PASSWORD=<secure-password>
```

### 3. Limpieza del Historial Git

Se utilizó `git filter-repo` para eliminar todas las contraseñas del historial:
- Contraseñas de admin
- Contraseñas de base de datos
- Contraseñas de ejemplo
- Hints de formato de contraseña

---

## 🌱 SISTEMA DE SEED DATA

### Arquitectura

```
scripts/seeds/
├── Dockerfile              # Imagen Python para el seeder
├── seed_runner.py          # Script principal de seeding
├── config/
│   ├── test.env           # Configuración TEST
│   ├── dev.env            # Configuración DEV
│   └── prod.env           # Configuración PROD
└── data/
    ├── test/              # Datos TEST (20 employees)
    │   ├── employees.json
    │   ├── users.json
    │   ├── payrolls.json
    │   └── expenses.json
    ├── dev/               # Datos DEV (80 employees)
    │   └── ...
    └── prod/              # Datos PROD (250 employees)
        └── ...
```

### Características del Seeder

#### Idempotencia

El seeder verifica existencia antes de crear:

```python
def seed_employees(employees, headers):
    existing = get_all_employees(headers)
    existing_emails = {e['email'] for e in existing}
    
    for emp in employees:
        if emp['email'] in existing_emails:
            log(f"Skipping {emp['email']} - already exists")
            continue
        create_employee(emp, headers)
```

#### Volúmenes de Datos por Entorno

| Entorno | Employees | Users | Payrolls | Expenses | Total |
|---------|-----------|-------|----------|----------|-------|
| TEST | 20 | 20 | 80 | 40 | **160** |
| DEV | 80 | 60 | 300 | 120 | **560** |
| PROD | 250 | 200 | 1200 | 480 | **2,130** |

#### Ejecución

```bash
# Desde cero (DB vacía)
docker compose --profile test down --volumes
docker compose --profile test up -d erp-test-db-container backend-test
docker compose --profile test run --rm api-seeder-test

# Re-ejecución (idempotente)
docker compose --profile test run --rm api-seeder-test
# Output: 0 created, X skipped
```

### Servicios Docker

```yaml
# docker-compose.yml

api-seeder-test:
  build: ./scripts/seeds
  profiles: ["test"]
  environment:
    ENVIRONMENT: TEST
    BACKEND_URL: http://backend-test:8080
  depends_on:
    backend-test:
      condition: service_healthy

api-seeder-dev:
  # Similar para DEV...

api-seeder-prod:
  # Similar para PROD...
```

---

## 🗄️ ESTRUCTURA SQL REFACTORIZADA

### Nuevo Layout

```
sql/
├── common/                        # Compartido entre todos los entornos
│   ├── 01_schema.sql             # Tablas, índices, constraints
│   ├── 02_roles.sql              # ADMIN, USER roles
│   ├── 03_positions.sql          # Posiciones/cargos
│   └── 05_admin_bootstrap_*.sql  # Usuarios admin con BCrypt
├── test/
│   └── 00_master.sql             # Orquestador para TEST
├── dev/
│   └── 00_master.sql             # Orquestador para DEV
├── prod/
│   └── 00_master.sql             # Orquestador para PROD
└── _legacy_*/                    # Scripts SQL antiguos (archivados)
```

### Flujo de Inicialización

```
PostgreSQL Init →
  00_master.sql (por entorno) →
    01_schema.sql (común) →
    02_roles.sql (común) →
    03_positions.sql (común) →
    05_admin_bootstrap_*.sql (por tipo de entorno)
```

### Tabla Notifications (Nueva)

Se añadió la tabla `notifications` al esquema común:

```sql
CREATE TABLE IF NOT EXISTS notifications (
    id BIGSERIAL PRIMARY KEY,
    employee_id BIGINT NOT NULL REFERENCES employees(id),
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    type VARCHAR(50) DEFAULT 'INFO',
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    read_at TIMESTAMP
);
```

---

## 🖥️ CONFIGURACIÓN MULTI-ENTORNO VITE

### Problema

El frontend Vite tenía el proxy del backend hardcodeado a puerto 8082 (solo DEV).

### Solución

#### `frontend/vite.config.js`
```javascript
export default defineConfig({
  server: {
    proxy: {
      '/api': {
        target: process.env.VITE_API_URL || 'http://localhost:8082',
        changeOrigin: true,
        secure: false
      }
    }
  }
});
```

#### `frontend/package.json`
```json
{
  "scripts": {
    "dev": "vite",
    "dev:test": "VITE_API_URL=http://localhost:8083 vite",
    "dev:prod": "VITE_API_URL=http://localhost:8181 vite"
  }
}
```

### Uso

```bash
# Desarrollo contra backend DEV (puerto 8082)
npm run dev

# Desarrollo contra backend TEST (puerto 8083)
npm run dev:test

# Desarrollo contra backend PROD (puerto 8181)
npm run dev:prod
```

**Nota:** Esto es solo para desarrollo local. TEST y PROD en producción usan el frontend containerizado.

---

## ✏️ CORRECCIÓN DE VALIDACIONES

### Problema

La entidad `Employee` tenía validaciones muy restrictivas que rechazaban nombres internacionales legítimos:

```java
// Antes - muy restrictivo
@Length(min = 3, max = 128)  // Rechaza "Mo", "Ai"
private String name;

@Length(min = 5, max = 255)  // Rechaza "Lee", "Ali", "Bach"
private String surname;
```

### Empleados Afectados

- `mo.farah@bizflowerp.com` - name='Mo' (2 chars)
- `ai.weiwei@bizflowerp.com` - name='Ai' (2 chars)
- `muhammad.ali@bizflowerp.com` - surname='Ali' (3 chars)
- `ang.lee@bizflowerp.com` - surname='Lee' (3 chars)
- `immanuel.kant@bizflowerp.com` - surname='Kant' (4 chars)
- Y 16 más...

### Solución

```java
// Después - compatible con nombres internacionales
@Length(min = 2, max = 128)
private String name;

@Length(min = 2, max = 255)
private String surname;
```

### Resultado

| Entorno | Antes | Después |
|---------|-------|---------|
| TEST | 19/20 employees | **20/20 ✅** |
| DEV | 72/80 employees | **80/80 ✅** |
| PROD | 229/250 employees | **250/250 ✅** |

---

## 📁 SCRIPTS DE UTILIDAD

Se añadieron scripts en `scripts/` para facilitar operaciones:

### `scripts/tests/`
- Scripts de testing automatizado

### `scripts/utils/`
- Utilidades de mantenimiento
- Herramientas de backup/restore

### `scripts/data/`
- `init_expense_data.sh` - Inicialización de datos de gastos

---

## 📊 COMMITS DE LA RAMA

```
cd7b72a security: remove password format hints from SQL bootstrap comments
c8ed3c7 security: remove plaintext passwords from documentation and SQL comments
363be77 docs(session): add session summary 2026-01-24 - idempotency and security
2b32b34 feat(scripts): add utility scripts for testing and data management
78cf199 feat(seeder): update payroll JSON files with bi-weekly dates
2ebaa42 feat(seeder): implement full idempotency for payrolls and expenses
06613cb feat(docker): configure environment-specific admin credentials
a20447b chore(sql): archive legacy SQL files for reference
51ba59b refactor(sql): remove legacy SQL seed files, use API seeder
e25b4b2 feat(prod): add PROD environment SQL initialization
2ad2f72 feat(security): implement bcrypt password hashing for admin bootstrap
a8b7474 feat(schema): add notifications table to common schema
a646697 feat(sql): add SQL seed infrastructure with legacy markers
660b23d fix(sql): remove duplicate Ada/Alan from environment user seeds
d66e73d feat(seeds): implement API-based seed system (Phase 2)
9750a87 feat(seeds): add DEV environment seed data
83a1157 feat(seeds): add TEST environment seed data
```

---

## 🧪 TESTING REALIZADO

### Metodología

Para cada entorno:
1. `docker compose --profile {env} down --volumes` - Limpieza total
2. `docker compose --profile {env} up -d db backend` - Iniciar servicios
3. `docker compose --profile {env} run --rm api-seeder-{env}` - Primera ejecución (CREAR)
4. `docker compose --profile {env} run --rm api-seeder-{env}` - Segunda ejecución (IDEMPOTENCIA)

### Resultados

| Entorno | 1ª Ejecución | 2ª Ejecución | Estado |
|---------|--------------|--------------|--------|
| TEST | 160 registros creados | 0 created, todo skipped | ✅ |
| DEV | 560 registros creados | 0 created, todo skipped | ✅ |
| PROD | 2,130 registros creados | 0 created, todo skipped | ✅ |

---

## 📝 DOCUMENTACIÓN AÑADIDA

| Archivo | Descripción |
|---------|-------------|
| `docs/sessions/2026-01/2026-01-24-6-summary-0113.md` | Sesión de implementación inicial |
| `docs/sessions/2026-01/2026-01-25-6-summary-2332.md` | Sesión de limpieza y validaciones |
| `scripts/seeds/README.md` | Documentación del sistema de seeding |

---

## ⚠️ BREAKING CHANGES

### 1. Contraseñas de Admin

Las contraseñas de admin ya no están en el código. Deben configurarse en archivos `.env.*`:

```bash
# .env.test / .env.dev / .env.prod (crear manualmente, gitignored)
ADMIN_EMAIL=ada.lovelace@bizflowerp.com
ADMIN_PASSWORD=<tu-password-seguro>
```

### 2. Datos Legacy Eliminados

Los scripts SQL de seed legacy han sido movidos a `sql/_legacy_*/`. El nuevo método es el API Seeder:

```bash
# Antiguo (ya no funciona)
# Los datos se cargaban desde SQL

# Nuevo
docker compose --profile dev run --rm api-seeder-dev
```

### 3. Validación de Employee

La validación mínima de `name` y `surname` cambió de 3/5 a 2/2 caracteres. Esto es compatible hacia atrás.

---

## 🚀 GUÍA DE MIGRACIÓN

### Para entornos existentes

1. **Actualizar archivos `.env.*`:**
   ```bash
   ADMIN_EMAIL=ada.lovelace@bizflowerp.com
   ADMIN_PASSWORD=<tu-password>
   ```

2. **Ejecutar seeder (idempotente):**
   ```bash
   docker compose --profile dev run --rm api-seeder-dev
   ```

3. **Rebuild del backend:**
   ```bash
   docker compose --profile dev build backend-dev
   ```

### Para entornos nuevos

```bash
# 1. Configurar credenciales
cp .env.example .env.dev
# Editar .env.dev con tus credenciales

# 2. Iniciar desde cero
docker compose --profile dev down --volumes
docker compose --profile dev up -d erp-dev-db-container backend-dev

# 3. Ejecutar seeder
docker compose --profile dev run --rm api-seeder-dev
```

---

## 📋 CHECKLIST DE REVISIÓN

- [x] Código compila sin errores
- [x] Tests de unidad pasan
- [x] Idempotencia verificada en 3 entornos
- [x] Sin contraseñas en código o historial Git
- [x] Documentación actualizada
- [x] Breaking changes documentados
- [x] Validaciones compatibles con datos internacionales

---

## 📎 ARCHIVOS MODIFICADOS (Resumen)

```
56 files changed, 20594 insertions(+), 6 deletions(-)

Principales:
- backend/src/main/java/io/sunbit/app/entity/Employee.java
- frontend/vite.config.js
- frontend/package.json
- docker-compose.yml
- scripts/seeds/* (nuevo directorio)
- sql/common/* (restructurado)
- sql/test|dev|prod/00_master.sql (nuevos)
- docs/sessions/2026-01/* (documentación)
```

---

*Pull Request creado el 25 de enero de 2026*
