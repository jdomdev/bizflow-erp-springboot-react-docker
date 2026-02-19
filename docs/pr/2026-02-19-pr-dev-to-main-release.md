# Pull Request: dev → main (Release 2026-02-19)

**Fecha:** 2026-02-19  
**Rama Origen:** `dev`  
**Rama Destino:** `main`  
**Pull Requests Incluidas:** 4 principales (#70, #72, #78, #79)  
**Commits Totales:** 52  
**Archivos Modificados:** 26  
**Líneas:** +1,476 / -64

---

## 📋 Resumen Ejecutivo

Esta release consolida mejoras de **documentación y seguridad** realizadas después del merge PR #69 (2026-02-16):

- README mejorado con screenshots y guía de credenciales
- Documentación de entorno de producción
- Soporte para Windows WSL2
- **Limpieza de seguridad**: eliminación de secretos del código para preparar limpieza de historial con BFG

---

## 🎯 Cambios Principales

| Categoría | Cambio | PR |
|-----------|--------|-----|
| **Documentación** | Screenshots de la aplicación | #70 |
| **Documentación** | Guía de credenciales del sistema | #72, #78 |
| **Documentación** | Entorno de producción en README | #72, #78 |
| **Documentación** | Soporte Windows WSL2 | #72, #78 |
| **Seguridad** | Eliminación de secretos de archivos | #79 |
| **Seguridad** | Nuevos JWT secrets para tests | #79 |

---

## 📦 Pull Requests Incluidas

### #70 - feat/readme-docs-screenshots
- Screenshots de la aplicación reorganizados
- Imágenes movidas a estructura de directorios apropiada
- Actualización de referencias en documentación

### #72, #78 - feat/prod-environment-docs
- **README mejorado** con sección de características
- **Tabla de entornos** (dev/test/prod) con comandos correctos
- **Guía de credenciales** para cada entorno
- **Soporte Windows WSL2** documentado
- Correcciones de comandos Makefile en documentación

### #79 - fix/clean-git-history
- **Eliminación de secretos** de archivos en HEAD:
  - Contraseñas de admin reemplazadas con placeholders
  - JWT secrets de test regenerados
  - Documentación con ejemplos [REDACTED]
- **Preparación para BFG Repo-Cleaner**
- PR documentación: [2026-02-19-pr-clean-git-history.md](./2026-02-19-pr-clean-git-history.md)

---

## 🔒 Notas de Seguridad

Esta release incluye cambios importantes de seguridad:

1. **Archivos limpiados** (8 archivos):
   - `.github/workflows/docker-build-test.yml`
   - `backend/src/test/java/.../UserControllerIT.java`
   - `backend/src/test/java/.../UserControllerTest.java`
   - `backend/src/test/resources/application-test.properties`
   - `docs/guide/dev/deployment.md`
   - `docs/sessions/2024-11/2024-11-26-1-launch-guide.md`
   - `docs/sessions/2024-11/2024-11-26-3-docker.md`
   - `docs/sessions/2026-01/2026-01-25-6-summary-2332.md`

2. **Post-merge**: Se ejecutará BFG Repo-Cleaner para limpiar el historial completo

---

## 📊 Estadísticas

| Métrica | Valor |
|---------|-------|
| Pull Requests | 4 principales + sub-PRs |
| Commits | 52 |
| Archivos modificados | 26 |
| Líneas añadidas | +1,476 |
| Líneas eliminadas | -64 |

---

## ✅ Checklist de Validación

- [x] README actualizado con features y screenshots
- [x] Guía de credenciales completa
- [x] Entornos documentados correctamente
- [x] Secretos eliminados de archivos en HEAD
- [x] JWT secrets de test regenerados
- [x] PR #79 mergeado a dev

---

## 📋 Instrucciones de Merge

```bash
# 1. Actualizar main local
git checkout main
git pull origin main

# 2. Merge dev → main
git merge dev

# 3. Push a origin
git push origin main
```

### Post-merge: Limpieza de historial con BFG

```bash
# Clonar mirror del repositorio
cd /tmp
git clone --mirror git@github.com:jdomdev/bizflow-erp-springboot-react-docker.git

# Ejecutar BFG
java -jar ~/bfg-1.14.0.jar --replace-text ~/bfg-passwords.txt bizflow-erp-springboot-react-docker.git

# Limpiar y forzar push
cd bizflow-erp-springboot-react-docker.git
git reflog expire --expire=now --all && git gc --prune=now --aggressive
git push --force
```

⚠️ **IMPORTANTE**: Después del force push, todos los colaboradores deben re-clonar el repositorio.

---

## 👤 Autor

**Desarrollador:** jdomdev  
**Fecha de release:** 2026-02-19
