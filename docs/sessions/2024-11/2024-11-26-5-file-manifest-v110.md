**Fecha:** 2024-11-26

# 📁 File Manifest v1.1.0

**Actualizado:** 2026-01-07 09:38 UTC

## 🔖 Resumen de versión
- Generado: 26 de noviembre de 2024
- Versión entregada: 1.1.0
- Volumen: 9 commits y más de 4 300 líneas nuevas
- Objetivo: habilitar contenedores productivos, migración a Jakarta EE y base documental para la Sesión 6.

## 🆕 Altas principales
### Infraestructura de contenedores
- backend/Dockerfile — build multi-stage Maven → JRE 21 con usuario sin privilegios y health check a `/actuator/health`.
- frontend/Dockerfile — build multi-stage Node 20/Nginx, expone `/health` y sirve la SPA.
- frontend/nginx.conf — enruta toda la SPA, proxy `/api/*` al backend, añade cabeceras de seguridad y compresión.
- docker-compose.yml — orquesta postgres, pgadmin (perfil debug), backend y frontend sobre la red `expense_network` con volúmenes persistentes.
- .devcontainer/devcontainer.json — imagen Java 21 con extensiones de productividad; expone 3000/5432/8080/5050.
- .devcontainer/post-create.sh — aprovisiona Maven, cliente psql y utilidades NPM dentro del contenedor de desarrollo.

### Documentación
- docs/sessions/2025-11/2025-11-27-5-start-here.md — onboarding de 5 minutos con comandos esenciales.
- docs/sessions/2025-11/2025-11-27-5-readme-improved.md — guía operativa completa y troubleshooting.
- docs/sessions/2025-11/2025-11-27-5-architecture.md — arquitectura de cinco capas y flujos JWT documentados.
- docs/sessions/2025-11/2025-11-27-5-debugging-guide.md — catálogo de siete incidencias frecuentes y scripts curl.
- docs/sessions/2025-11/2025-11-27-5-summary.md — resumen narrativo con métricas y checklist de la sesión.
- docs/sessions/2024-11/2024-11-26-5-release-notes-v110.md — detalle de commits, métricas y riesgos del release.
- docs/sessions/2024-11/2024-11-26-5-file-manifest-v110.md — inventario de rutas (este documento).
- docs/sessions/2025-11/2025-11-27-6-roadmap.md — roadmap para la siguiente iteración.

## 🔄 Cambios en backend
- backend/pom.xml actualizado a Spring Boot 3.3.4, Spring Security 6.3.3, Jakarta EE 10 y driver PostgreSQL 42.7.3.
- Código Java migrado de `javax.*` a `jakarta.*` en controllers, DAOs, DTOs, entidades y capa de seguridad.
- Autenticación JWT adaptada a JJWT 0.12.6 con logging reforzado y manejo de excepciones alineado.

## ⚛️ Cambios en frontend
- Nueva aplicación React 18 + Vite con estructura modular (componentes Auth, Dashboard, Layout, Common).
- Servicios `api.js`, `authService.js`, `employeeService.js`, `expenseService.js` y `payrollService.js` centralizan llamadas HTTP.
- Estado gestionado con Zustand y TailwindCSS; configuración en package.json, vite.config.js y tailwind.config.js.
- Scripts de NPM listos para ejecución local o dentro del Dev Container.

## 📈 Métricas y commits
| Categoría | Líneas añadidas | Archivos |
|-----------|----------------|----------|
| Contenedores y configuración | ~180 | 5 |
| Backend (Java) | ~200 | 8+ |
| Frontend (React) | ~1 480 | 22 |
| Documentación | ~2 400 | 6 |
| **Total** | **4 300+** | **42+** |

| Commit | Tipo | Nota rápida |
|--------|------|-------------|
| 1 | feat | Alta de infraestructura Docker |
| 2 | build | Ajustes de compilación backend |
| 3 | fix | Correcciones críticas de seguridad |
| 4 | feat | Implementación completa del frontend |
| 5-7 | docs | Publicación de documentación de sesión |
| 8 | chore | Limpieza y ajustes de configuración |
| 9 | docs | Publicación de release notes |

## 🛡️ Mejoras de seguridad
- 13 vulnerabilidades abordadas (3 críticas, 4 altas, 5 medias, 1 baja) mediante actualización de dependencias clave.
- Cabeceras de seguridad y compresión Gzip habilitadas en frontend/nginx.conf.
- Variables sensibles gestionadas vía `.env`; guía de uso en 2025-11-27-5-readme-improved.md.

## 🔍 Cómo revisar los cambios
```bash
# Revisar commits y diffs
git log --oneline -9
git show <commit>

# Validar cambios agregados
git diff HEAD~9 HEAD --stat
```

```bash
# Construir y ejecutar contenedores
docker-compose build
docker-compose up -d
```

```bash
# Abrir el Dev Container
code .
# Luego: Cmd/Ctrl+Shift+P → "Dev Containers: Open Folder in Container"
```

## ✅ Checklist de verificación
- [x] Contenedores backend y frontend construyen y pasan health checks.
- [x] Migración a Jakarta completada sin errores de compilación.
- [x] Documentación de Sesión 5 organizada por roles (start_here, architecture, debugging, summary).
- [x] Credenciales de prueba validadas y documentadas en 2025-11-27-5-summary.md.
- [x] Roadmap de Sesión 6 enlazado para continuidad.

---
Actualizado: 2026-01-07 09:38 UTC
