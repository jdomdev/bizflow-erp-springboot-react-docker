# PR: Limpieza de Secretos del Historial de Git

**Rama:** `fix/clean-git-history` → `dev`  
**Fecha:** 19 de febrero de 2026  
**Autor:** @jdomdev

---

## 📋 Resumen

Esta PR elimina todos los secretos (contraseñas y JWT secrets) expuestos en archivos del repositorio, preparando el código para una limpieza completa del historial de Git con BFG Repo-Cleaner. Los secretos reales se reemplazan por placeholders seguros, y los secretos de test se regeneran con valores nuevos no comprometidos.

---

## 🎯 Objetivos

1. **Eliminar contraseñas en texto plano** de documentación y resúmenes de sesión
2. **Reemplazar JWT secrets expuestos** con placeholders o valores nuevos
3. **Preparar el repositorio** para limpieza de historial con BFG
4. **Cumplir con GitGuardian** eliminando alertas de secretos detectados

---

## 🔍 Contexto del Problema

### El Problema: Secretos en el Historial de Git

GitGuardian detectó múltiples secretos expuestos en el repositorio:

| Tipo | Ejemplo | Ubicación |
|------|---------|-----------|
| Contraseñas admin | `[REDACTED_ADMIN_PASS]` | Session summaries, SQL comments |
| JWT Secrets | `[REDACTED_JWT_64_CHARS]` | Documentation, docker-compose examples |
| Test secrets | `[REDACTED_TEST_SECRET]` | Test files, CI workflows |
| DB passwords | `[REDACTED_DB_PASS]` | Documentation examples |

### Por qué es un problema

1. **GitGuardian escanea TODO el historial**, no solo el código actual
2. Aunque los secretos ya no estén en HEAD, siguen en commits históricos
3. Las alertas persisten hasta que se reescriba el historial completo
4. Riesgo de exposición si alguien clona el repositorio completo

### La Solución: BFG Repo-Cleaner

BFG Repo-Cleaner reescribe el historial de Git, reemplazando strings específicos en **todos** los commits. Pero BFG **protege el HEAD actual** - no modifica el último commit.

**Requisito:** Antes de ejecutar BFG, todos los secretos deben estar eliminados del HEAD actual para que la limpieza sea completa.

---

## ✨ Cambios Implementados

### 1. Documentación - Secretos Reemplazados por Placeholders

**Archivos modificados:**
- `docs/guide/dev/deployment.md`
- `docs/sessions/2024-11/2024-11-26-1-launch-guide.md`
- `docs/sessions/2024-11/2024-11-26-3-docker.md`

**Ejemplo de cambio:**
```diff
# Antes
- JWT_SECRET=[SECRETO_EXPUESTO_ELIMINADO]

# Después
+ JWT_SECRET=<GENERA_CON_openssl_rand_-base64_64>
+ DB_PASSWORD=<TU_DB_PASSWORD>
```

### 2. Resúmenes de Sesión - Contraseñas Redactadas

**Archivo modificado:**
- `docs/sessions/2026-01/2026-01-25-6-summary-2332.md`

**Ejemplo de cambio:**
```diff
# Antes
- [PASSWORD_EXPUESTA] (admin password)

# Después
+ [REDACTED] (admin password)
```

### 3. Test Files - JWT Secret Regenerado

Se generó un **nuevo secreto de test** que reemplaza al anterior comprometido.

> Los valores específicos no se documentan aquí por seguridad. Ver archivos modificados para el nuevo valor.

**Archivos modificados:**
- `.github/workflows/docker-build-test.yml`
- `backend/src/test/java/io/sunbit/app/security/controller/UserControllerIT.java`
- `backend/src/test/java/io/sunbit/app/security/controller/UserControllerTest.java`
- `backend/src/test/resources/application-test.properties`

---

## 📁 Archivos Modificados

| Archivo | Tipo | Cambio |
|---------|------|--------|
| `.github/workflows/docker-build-test.yml` | CI | Nuevo JWT secret de test |
| `backend/src/test/java/.../UserControllerIT.java` | Test | Nuevo JWT secret |
| `backend/src/test/java/.../UserControllerTest.java` | Test | Nuevo JWT secret |
| `backend/src/test/resources/application-test.properties` | Config | Nuevo JWT secret |
| `docs/guide/dev/deployment.md` | Docs | Placeholders para secrets |
| `docs/sessions/2024-11/2024-11-26-1-launch-guide.md` | Docs | Placeholder para ejemplo JWT |
| `docs/sessions/2024-11/2024-11-26-3-docker.md` | Docs | Placeholders para JWT y DB password |
| `docs/sessions/2026-01/2026-01-25-6-summary-2332.md` | Docs | Contraseñas redactadas |

---

## 🔗 Commits

### 1. `cb628bd` - security: remove all secrets from HEAD before BFG cleanup
```
- Replace JWT secrets with placeholders in documentation
- Replace admin passwords with [REDACTED] in session summaries
- Update test JWT secret to new non-exposed value
- Prepares codebase for clean BFG history rewrite

Files:
 .github/workflows/docker-build-test.yml                        | 2 +-
 backend/src/test/java/.../UserControllerIT.java                | 2 +-
 backend/src/test/java/.../UserControllerTest.java              | 2 +-
 backend/src/test/resources/application-test.properties         | 2 +-
 docs/guide/dev/deployment.md                                   | 2 +-
 docs/sessions/2024-11/2024-11-26-1-launch-guide.md            | 2 +-
 docs/sessions/2024-11/2024-11-26-3-docker.md                  | 6 +++---
 docs/sessions/2026-01/2026-01-25-6-summary-2332.md            | 8 ++++----
```

---

## 🔜 Siguientes Pasos (Post-Merge)

Una vez mergeada esta PR a `dev` y luego a `main`, se ejecutará BFG Repo-Cleaner para limpiar el historial completo:

```bash
# 1. Clonar mirror del repositorio
git clone --mirror git@github.com:jdomdev/bizflow-erp-springboot-react-docker.git

# 2. Ejecutar BFG con lista de secretos a eliminar
java -jar bfg.jar --replace-text passwords.txt bizflow-erp-springboot-react-docker.git

# 3. Limpiar y push
cd bizflow-erp-springboot-react-docker.git
git reflog expire --expire=now --all
git gc --prune=now --aggressive
git push --force

# 4. Todos los colaboradores deben re-clonar el repositorio
```

> ⚠️ **IMPORTANTE:** Después de ejecutar BFG, todos los colaboradores deben:
> 1. Eliminar su clon local
> 2. Clonar de nuevo el repositorio
> 3. No hacer push de ramas locales antiguas (reintroducirían secretos)

---

## 🔐 Secretos Eliminados

### Lista completa de secretos detectados y eliminados:

| Categoría | Cantidad | Estado |
|-----------|----------|--------|
| Contraseñas de admin | 2 | ✅ Redactadas |
| JWT secrets (producción) | 3 | ✅ Placeholders |
| JWT secrets (dev/test) | 5 | ✅ En archivo BFG |
| Contraseñas de ejemplo | 2 | ✅ Eliminadas |
| Contraseñas de DB | 2 | ✅ Placeholders |

> **Nota:** Los valores específicos no se listan por seguridad. El archivo `bfg-passwords.txt` contiene la lista completa para la limpieza.

---

## ✅ Checklist de Validación

- [x] Tests pasan con nuevo JWT secret
- [x] CI workflow usa nuevo JWT secret
- [x] No hay secretos en texto plano en archivos del repositorio
- [x] Placeholders son claramente identificables (no parecen reales)
- [x] Archivo `bfg-passwords.txt` preparado para limpieza de historial

---

## 📝 Notas Adicionales

### Rotación de Credenciales

Aunque los secretos se eliminarán del historial, las credenciales **ya fueron expuestas públicamente**. Se recomienda:

1. **Contraseñas de admin**: Ya fueron cambiadas en producción (usando `prod_users.json`)
2. **JWT secrets**: Los de producción deben regenerarse en el servidor
3. **Base de datos**: Cambiar contraseña de PostgreSQL en producción

### GitGuardian

Después de completar la limpieza con BFG y el force push:
1. Las alertas existentes deben cerrarse automáticamente
2. Si persisten, se pueden marcar como "resueltas" manualmente
3. El escaneo de nuevos commits no debería generar alertas

---

## 🏷️ Labels Sugeridos

- `security`
- `documentation`
- `maintenance`
