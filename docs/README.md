# 📚 Documentación - BizFlow ERP

**Última actualización:** 06 Enero 2026  
**Estado:** Sesión 6 - Roadmap y pruebas

---

## 🗂️ Directorios Principales
- [sessions/README.md](sessions/README.md) — Cronología completa de sesiones con índices mensuales.
- [guides](guides) — Guías operativas (entornos, inicialización de BD, testing).
- [docker](docker) — Procedimientos Docker y ejecución en contenedores.
- [planning](planning) — Planificación funcional y hojas de ruta.
- [postman](postman) — Colecciones y guías para pruebas de API.
- [json](json) — Datos de ejemplo para cargas masivas o pruebas.
- [process](process) — Historial de procesos y sesiones legacy.
- [spring](spring) — Referencias específicas de Spring Boot y seguridad.

---

## 📅 Sesiones Recientes
- [sessions/2026-01/README.md](sessions/2026-01/README.md) — Avances de enero 2026 (sesión 6 en curso).
- [sessions/2025-12/README.md](sessions/2025-12/README.md) — Cierre de fase 1 y guías de pruebas.
- [sessions/2025-11/README.md](sessions/2025-11/README.md) — Consolidación de la sesión 5 y roadmap inicial.
- [sessions/2024-11/README.md](sessions/2024-11/README.md) — Inicios de la migración y contexto histórico.

**Documento clave:** [sessions/2025-11/2025-11-26-2-migration.md](sessions/2025-11/2025-11-26-2-migration.md) concentra los cambios críticos de la migración inicial.

---

## 🧭 Rutas Recomendadas

### 🚀 Primer día en el proyecto
1. [sessions/2025-11/2025-11-27-5-start-here.md](sessions/2025-11/2025-11-27-5-start-here.md)
2. [sessions/2025-11/2025-11-27-5-summary.md](sessions/2025-11/2025-11-27-5-summary.md)
3. [sessions/2025-11/2025-11-27-5-architecture.md](sessions/2025-11/2025-11-27-5-architecture.md)
4. [sessions/2025-11/2025-11-27-5-debugging-guide.md](sessions/2025-11/2025-11-27-5-debugging-guide.md)

### 🔁 Repasar el roadmap actual
1. [sessions/2025-11/2025-11-27-6-roadmap.md](sessions/2025-11/2025-11-27-6-roadmap.md)
2. [sessions/2025-12/2025-12-06-6-phase-1-work-completed.md](sessions/2025-12/2025-12-06-6-phase-1-work-completed.md)
3. [sessions/2026-01/2026-01-05-6-summary-2233.md](sessions/2026-01/2026-01-05-6-summary-2233.md)

### 🧪 Preparar entorno de pruebas
- [guides/environment-switch-guide.md](guides/environment-switch-guide.md)
- [guides/automated-db-initialization-sequence.md](guides/automated-db-initialization-sequence.md)
- [docker/backend_tests_docker_guide.md](docker/backend_tests_docker_guide.md)
- [guides/testing_strategy_explained_251215.md](guides/testing_strategy_explained_251215.md)

---

## 🔍 Referencias Destacadas
- [INDEX.md](INDEX.md) — Índice maestro (102 documentos catalogados).
- [sessions/2025-11/2025-11-27-5-index.md](sessions/2025-11/2025-11-27-5-index.md) — Navegación por roles y tiempos estimados.
- [postman/postman_collection_guide.md](postman/postman_collection_guide.md) — Uso de la colección Postman oficial.
- [planning/erp_functionality_251206.md](planning/erp_functionality_251206.md) — Plan funcional por módulos.

---

## 🛠️ Ejecutar el Proyecto
```bash
cd /home/bytetech/code/java/bizflow-erp-springboot-react-docker
docker-compose up -d
# Frontend: http://localhost
# Backend: http://localhost:8080
# BD: localhost:5433
```

### Logs y pruebas rápidas
```bash
docker-compose ps
docker-compose logs backend | tail -50
docker-compose exec backend mvn test
```

---

## 👤 Credenciales por Defecto
```
Username: admin
Password: admin123
Email: admin@expenseapp.com
```

### Datos iniciales
```
Roles: ADMIN, USER, MANAGER (cargados por DataLoader)
Usuario admin: creado automáticamente en el arranque
Posiciones: 3 ejemplos incluidos
```

---

## ✅ Checklist Esencial
- [ ] Seguí la ruta "Primer día en el proyecto"
- [ ] Revisé el roadmap vigente
- [ ] Ejecuté docker-compose up -d y verifiqué servicios
- [ ] Probé el login con admin/admin123
- [ ] Revisé la colección Postman oficial
- [ ] Consulté el índice maestro para profundizar

---

¿Necesitas localizar un documento específico? Empieza por [INDEX.md](INDEX.md) o el índice mensual correspondiente en [sessions/README.md](sessions/README.md).
## 🎓 Glosario de Términos


