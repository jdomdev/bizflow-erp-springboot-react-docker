**Fecha:** 2025-11-27

# 📑 Documentation Index - Session 3

**Fecha:** 27 noviembre 2025  
**Estado:** ✅ Lista para trabajo de Session 3

---

## 🗂️ Ruta rápida de lectura

```
1. Empezar hoy
   → 2024-11-26-3-index.md (10 min)

2. Contexto técnico clave
   → 2025-11-27-3-architecture.md (30 min)

3. Operativa con contenedores
   → 2024-11-26-3-docker.md (25 min)
```

Si necesitas seguridad o roadmap:
```
4. Seguridad
   → 2024-11-26-3-security.md (20 min)

5. Roadmap funcional
   → 2024-11-26-3-features-roadmap.md (30 min)
```

---

## 📚 Documentos disponibles

| Archivo | Tiempo | Tema principal |
|---------|--------|----------------|
| 2024-11-26-3-index.md | 10 min | Guía general del proyecto y stack actual |
| 2025-11-27-3-architecture.md | 30 min | Arquitectura detallada de frontend, backend y base de datos |
| 2024-11-26-3-docker.md | 25 min | Uso de Docker, docker-compose y dev containers |
| 2024-11-26-3-security.md | 20 min | Vulnerabilidades resueltas y hardening aplicado |
| 2024-11-26-3-features-roadmap.md | 30 min | Plan de funcionalidades empresariales futuras |

**Tiempo total:** ~115 min  
**Lectura mínima:** 35 min (primer bloque)

---

## 🎯 Qué leer según tu rol

### Entregable rápido (35 min)
```
1. 2024-11-26-3-index.md
2. 2025-11-27-3-architecture.md (secciones Resumen ejecutivo + System Architecture)
```

### Necesito ejecutar infraestructura (60 min)
```
1. 2024-11-26-3-docker.md → Quick Start + Troubleshooting
2. 2025-11-27-3-architecture.md → Backend Stack + Docker Orchestration
3. 2024-11-26-3-security.md → Endpoints expuestos y políticas
```

### Estoy planificando roadmap (45 min)
```
1. 2024-11-26-3-index.md → Tecnologías y contexto
2. 2024-11-26-3-features-roadmap.md → Secciones 1-4
```

---

## 📄 Resumen por documento

### 2024-11-26-3-index.md
- Estado del proyecto tras la migración a Spring Boot 3 y React
- Desglose de tecnologías por capa (frontend, backend, DevOps)
- Pasos para iniciar ambos servicios y realizar pruebas manuales
- Enlaces a componentes clave y organización de código

### 2025-11-27-3-architecture.md
- Diagrama completo de flujo usuario → API → base de datos
- Stack actualizado (Java 21, React 18, PostgreSQL 16)
- Componentes críticos corregidos en seguridad y CORS
- Resumen de endpoints REST y entidades JPA
- Detalles de configuración docker-compose y health checks

### 2024-11-26-3-docker.md
- Arquitectura de contenedores y red interna expense_network
- Pasos para levantar, detener y depurar servicios con docker-compose
- Explicación de multi-stage builds para backend y frontend
- Variables de entorno necesarias y ejemplos de health checks
- Troubleshooting de montajes, volúmenes y puertos ocupados

### 2024-11-26-3-security.md
- Inventario de 13 vulnerabilidades detectadas en 2024 y su resolución
- Dependencias actualizadas (Spring, Log4j, Jackson, SnakeYAML, PostgreSQL)
- Endpoints sensibles protegidos y tratamiento de errores HTTP
- Checklist de hardening previo a producción

### 2024-11-26-3-features-roadmap.md
- Roadmap temático (gastos, aprobaciones, presupuestos, analytics)
- Estimaciones por iniciativa y valor empresarial
- Modelos de datos propuestos (Trip, Budget, workflows de aprobación)
- Recomendaciones tecnológicas para OCR, BI y notificaciones

---

## ✅ Checklist de preparación

- [ ] Revisión rápida: 2024-11-26-3-index.md
- [ ] Arquitectura confirmada: 2025-11-27-3-architecture.md
- [ ] Docker operativo: 2024-11-26-3-docker.md
- [ ] Seguridad verificada: 2024-11-26-3-security.md
- [ ] Roadmap alineado: 2024-11-26-3-features-roadmap.md

---

## 🔍 Búsqueda por temas

- **Arquitectura y flujos** → 2025-11-27-3-architecture.md (System Architecture, Data Flow)
- **Montaje local y comandos** → 2024-11-26-3-docker.md (Quick Start con Docker, Comandos útiles)
- **Seguridad y dependencias** → 2024-11-26-3-security.md (Vulnerabilidades identificadas)
- **Visión de producto** → 2024-11-26-3-features-roadmap.md (Roadmap por iniciativa)

---

## 🧭 Plan operativo sugerido

```
Hoy (35 min):
├─ 2024-11-26-3-index.md
└─ 2025-11-27-3-architecture.md (hasta Configuración de Docker)

Previo a ejecutar docker-compose (25 min):
├─ 2024-11-26-3-docker.md → Quick Start + Troubleshooting
└─ 2024-11-26-3-security.md → Checklist de dependencias

Planificación a medio plazo (30 min):
└─ 2024-11-26-3-features-roadmap.md → Priorizar iniciativas 1-4
```

---

## ℹ️ Datos prácticos

- Frontend local: http://localhost  
- Backend API: http://localhost:8080  
- Base de datos PostgreSQL: localhost:5433  
- Logs backend: docker-compose logs backend  
- Logs frontend: docker-compose logs frontend

**Branch activa:** fix/api-endpoint-authorization (base dev)

---

**Documento:** 2025-11-27-3-documentacion-index.md  
**Creado:** 27 noviembre 2025  
**Última revisión:** 27 noviembre 2025  
**Próximo paso recomendado:** Leer 2024-11-26-3-index.md
