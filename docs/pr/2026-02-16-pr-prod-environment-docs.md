# PR: Documentación de Entornos (Dev, Test, Prod)

**Rama:** `feat/prod-environment-docs` → `dev`  
**Fecha:** 16 de febrero de 2026  
**Autor:** @jdomdev

---

## 📋 Resumen

Esta PR completa la documentación de los tres entornos del proyecto (development, test, production) en el README principal. Incluye la corrección de hashes de contraseñas BCrypt para el entorno de producción, una tabla unificada de puertos y comandos, y una sección de comandos útiles ampliada y categorizada.

---

## 🎯 Objetivos

1. **Documentar todos los entornos** (dev, test, prod) de forma clara y concisa
2. **Corregir la autenticación en producción** regenerando los hashes BCrypt
3. **Unificar la información de puertos** en una tabla de referencia rápida
4. **Separar credenciales** de dev/test vs producción por seguridad
5. **Ampliar la sección de comandos** con categorías organizadas

---

## 🔍 Contexto del Problema

### Problema 1: Autenticación fallida en producción

Al intentar iniciar sesión en el entorno de producción con las credenciales de `prod_users.json`, la autenticación fallaba con error 401.

**Causa raíz:** Los hashes BCrypt en `sql/common/05_admin_bootstrap_prod.sql` no coincidían con las contraseñas definidas en `scripts/secrets/users_with_passwords/prod_users.json`.

**Contraseñas de prod vs dev:**
| Usuario | Dev Password | Prod Password |
|---------|--------------|---------------|
| ada.lovelace | `<DEV_PASSWORD>` | `<PROD_PASSWORD>` |
| grace.hopper | `<DEV_PASSWORD>` | `<PROD_PASSWORD>` |
| marie.curie | `<DEV_PASSWORD>` | `<PROD_PASSWORD>` |

> 🔐 Las contraseñas reales están en `scripts/secrets/users_with_passwords/`

### Problema 2: README incompleto

El README solo documentaba el entorno de desarrollo. No había información sobre:
- Puertos del entorno de test (frontend, backend, DB)
- Puertos del entorno de producción
- Comandos para iniciar cada entorno
- Nota sobre credenciales diferentes en producción

### Problema 3: Información incorrecta inicial

En la primera iteración de documentación, el entorno test se documentó incorrectamente:
- Frontend: Se indicó como inexistente (❌)
- DB port: Se indicó 5433 (incorrecto)
- Comando: Se indicó `make test-backend` (incorrecto)

---

## ✨ Cambios Implementados

### 1. Regeneración de Hashes BCrypt

Se ejecutó el script de generación de hashes para actualizar los archivos SQL:

```bash
python3 scripts/utils/generate_password_hashes.py --generate
```

**Archivos modificados:**
- `sql/common/05_admin_bootstrap_prod.sql` - Hashes para prod
- `sql/common/05_admin_bootstrap_dev_test.sql` - Hashes para dev/test

**Ejemplo de hash regenerado (ada.lovelace en prod):**
```sql
-- Antes (incorrecto)
'$2a$10$OLD_HASH_THAT_DIDNT_MATCH...'

-- Después (correcto)
'$2a$10$.EwXyrwRo/KPSJpQHcx2xOwPrpAwznEIt4IpR7z.dt.pwzGNXhbHS'
```

### 2. Tabla Unificada de Entornos

Se creó una nueva sección **🌍 Entornos** en el README con tabla consolidada:

| Entorno | Frontend | Backend API | Base de datos | Comando |
|---------|----------|-------------|---------------|---------|
| **Dev** | http://localhost:8085 | http://localhost:8082/api/v1 | localhost:5433 | `make dev` |
| **Test** | http://localhost:8086 | http://localhost:8083/api/v1 | localhost:5434 | `make up-test` |
| **Prod** | http://localhost:8080 | http://localhost:8181/api/v1 | localhost:5442 | `make prod` |

### 3. Tabla de Credenciales

Se añadió tabla separando credenciales por entorno:

| Entorno | Usuario | Contraseña |
|---------|---------|------------|
| Dev/Test | `ada.lovelace@bizflowerp.com` | Ver `dev_users.json` |
| Prod | `ada.lovelace@bizflowerp.com` | Ver `prod_users.json` |

> 🔐 Archivos de credenciales en `scripts/secrets/users_with_passwords/`

> ⚠️ **Nota de seguridad:** En producción se usan contraseñas diferentes para evitar exposición accidental en documentación pública.

### 4. Sección de Comandos Ampliada

La sección **🔧 Comandos Útiles** se reorganizó en categorías:

```bash
# Entornos
make up-dev           # Iniciar entorno desarrollo
make up-prod          # Iniciar entorno producción
make up-test          # Iniciar entorno testing
make down-dev         # Detener entorno desarrollo (conserva datos)
make down-prod        # Detener entorno producción (conserva datos)
make down-test        # Detener entorno testing (conserva datos)

# Base de datos
make backup-dev       # Crear backup de BD de desarrollo
make backup-prod      # Crear backup de BD de producción
make backup-test      # Crear backup de BD de testing

# Logs y estado
docker compose logs   # Ver logs de servicios
docker compose ps     # Estado de contenedores
```

### 5. Sección pgAdmin

Se documentó el acceso a pgAdmin (solo disponible en dev):

- **URL:** http://localhost:5050
- **Credenciales:** Ver variables en `.env`

---

## 📁 Archivos Modificados

| Archivo | Tipo | Cambio |
|---------|------|--------|
| `sql/common/05_admin_bootstrap_prod.sql` | SQL | Hashes BCrypt regenerados para prod |
| `sql/common/05_admin_bootstrap_dev_test.sql` | SQL | Hashes BCrypt verificados para dev/test |
| `README.md` | Docs | Nueva sección Entornos, tabla credenciales, comandos ampliados |

---

## 🔗 Commits

### 1. `51f11cf` - fix(auth): regenerate password hashes for prod environment
```
- Prod uses different passwords than dev for security
- BCrypt hashes now match prod_users.json passwords

Files:
 sql/common/05_admin_bootstrap_dev_test.sql | 6 +++---
 sql/common/05_admin_bootstrap_prod.sql     | 6 +++---
```

### 2. `4a10980` - docs: add production environment section to README
```
- Document prod ports (8080 frontend, 8181 backend, 5442 db)
- Add make prod command
- Note about different passwords in prod for security
- Include dev credentials for quick start

Files:
 README.md | 23 +++++++++++++++++++++--
```

### 3. `b06b37a` - docs: consolidate environments table and update commands section
```
- Add unified environments table (dev, test, prod) with ports and commands
- Add credentials table separating dev/test from prod
- Expand useful commands section with categories
- Document test environment (port 8083/5433)

Files:
 README.md | 52 +++++++++++++++++++++++++++-------------------------
```

### 4. `a4100e9` - fix(docs): correct test environment ports and command
```
- Test frontend: port 8086 (not missing)
- Test database: port 5434 (not 5433)
- Command: make up-test (not make test-backend)

Files:
 README.md | 4 ++--
```

---

## 🧪 Verificación Realizada

### Test de autenticación en producción

```bash
# 1. Iniciar entorno prod
make prod

# 2. Verificar servicios
curl -s http://localhost:8080 | head -1  # Frontend: 200 OK
curl -s http://localhost:8181/api/v1     # Backend: 200 OK

# 3. Login con credenciales de prod
curl -X POST http://localhost:8181/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"ada.lovelace@bizflowerp.com","password":"<PROD_PASSWORD>"}'

# Resultado: Token JWT recibido ✅
```

### Verificación de puertos en docker-compose.yml

**Entorno Development:**

| Servicio | Línea | Puerto |
|----------|-------|--------|
| frontend-dev | 348 | `8085:80` |
| backend-dev | 218 | `8082:8080` |
| erp-dev-db-container | 132 | `5433:5432` |

**Entorno Test:**

| Servicio | Línea | Puerto |
|----------|-------|--------|
| frontend-test | 368 | `8086:80` |
| backend-test | 235 | `8083:8080` |
| erp-test-db-container | 157 | `5434:5432` |

---

## 📊 Resumen de Puertos por Entorno

### Entorno Development (profile: dev)
```
┌─────────────────────────────────────────────────┐
│                    DEV                          │
├─────────────┬─────────────┬─────────────────────┤
│  Frontend   │   Backend   │     Database        │
│   :8085     │   :8082     │      :5433          │
├─────────────┴─────────────┴─────────────────────┤
│  pgAdmin: :5050                                 │
└─────────────────────────────────────────────────┘
```

### Entorno Test (profile: test)
```
┌─────────────────────────────────────────────────┐
│                   TEST                          │
├─────────────┬─────────────┬─────────────────────┤
│  Frontend   │   Backend   │     Database        │
│   :8086     │   :8083     │      :5434          │
└─────────────┴─────────────┴─────────────────────┘
```

### Entorno Production (profile: prod)
```
┌─────────────────────────────────────────────────┐
│                   PROD                          │
├─────────────┬─────────────┬─────────────────────┤
│  Frontend   │   Backend   │     Database        │
│   :8080     │   :8181     │      :5442          │
└─────────────────────────────────────────────────┘
```

---

## 🔐 Notas de Seguridad

1. **Contraseñas de producción no se documentan en README**
   - Se referencian a `scripts/secrets/users_with_passwords/prod_users.json`
   - Este archivo está en `.gitignore` (no se sube al repo público)

2. **Hashes BCrypt en SQL son seguros**
   - Incluso si se exponen, no revelan la contraseña original
   - Factor de costo: 10 (estándar de la industria)

3. **Separación de entornos**
   - Cada entorno usa diferentes puertos para evitar conflictos
   - Dev y Test comparten credenciales (uso interno)
   - Prod usa credenciales únicas

---

## ✅ Checklist

- [x] Hashes BCrypt regenerados y verificados
- [x] Login en producción funcional
- [x] Tabla de entornos con puertos correctos
- [x] Tabla de credenciales separada por entorno
- [x] Sección de comandos ampliada
- [x] Puertos de test corregidos (frontend: 8086, db: 5434)
- [x] Comando de test corregido (make up-test)
- [x] Commits con mensajes descriptivos
- [x] Rama pusheada a origin

---

## 🚀 Próximos Pasos

1. **Merge a dev** - Esta PR
2. **Probar los 3 entornos** antes de merge a main
3. **Considerar agregar:**
   - Scripts de health-check por entorno
   - Documentación de variables de entorno (.env.example)
   - Guía de troubleshooting de autenticación

---

## 📝 Lecciones Aprendidas

1. **Verificar hashes después de cambiar contraseñas**
   - Siempre ejecutar `generate_password_hashes.py --generate` después de modificar contraseñas en los JSON

2. **Consultar docker-compose.yml para puertos reales**
   - No asumir puertos basándose en patrones
   - El entorno test tiene frontend completo (puerto 8086)

3. **Documentar credenciales de forma segura**
   - Dev/test: pueden exponerse en README
   - Prod: solo referenciar ubicación del archivo

