# PR: Mejoras en README con Capturas de Documentación

**Rama:** `feat/readme-docs-screenshots` → `dev`  
**Fecha:** 16 de febrero de 2026  
**Autor:** @jdomdev

---

## 📋 Resumen

Esta PR mejora significativamente la presentación visual del proyecto en el README principal, añadiendo capturas de pantalla de la documentación desplegada en Netlify y reorganizando la estructura de imágenes del repositorio para una mejor mantenibilidad.

---

## 🎯 Objetivos

1. **Mostrar la documentación** desplegada en Netlify directamente en el README
2. **Reorganizar las imágenes** del proyecto en una estructura más clara y escalable
3. **Completar el stack tecnológico** con la información de Java/OpenJDK
4. **Mejorar la experiencia** del visitante al repositorio

---

## ✨ Cambios Implementados

### 1. Reorganización de la Estructura de Imágenes

**Antes:**
```
docs/images/
├── dashboard.png
├── dashboard-dark-mode.png
├── employees.png
├── expenses.png
└── README.md
```

**Después:**
```
docs/images/
├── app/                    # Capturas de la aplicación web
│   ├── dashboard.png
│   ├── dashboard-dark-mode.png
│   ├── employees.png
│   └── expenses.png
├── docs/                   # Capturas de la documentación
│   ├── home.png
│   ├── architecture.png
│   ├── docker-guide.png
│   └── api-reference.png
└── README.md
```

**Beneficios:**
- Separación clara entre imágenes de la app y de la documentación
- Nombre genérico `docs/` (no atado a Netlify) para flexibilidad futura
- Fácil de mantener y escalar

### 2. Nueva Galería de Documentación en el README

Se añadió una galería visual de 4 capturas de la documentación desplegada en Netlify, ubicada estratégicamente debajo del título de **📖 Documentación**:

| Captura | Descripción |
|---------|-------------|
| **Home** | Página principal de la documentación con navegación |
| **Arquitectura** | Diagrama y explicación de la arquitectura del sistema |
| **Guía Docker** | Guía de uso de Docker y comandos disponibles |
| **API Reference** | Documentación de la API REST con endpoints |

Esta ubicación permite al visitante ver inmediatamente cómo luce la documentación antes de hacer clic en el enlace a Netlify.

### 3. Actualización del Stack Tecnológico

Se completó la tabla de stack tecnológico añadiendo **Java 17 (OpenJDK)** como primera entrada del backend:

| Backend | Frontend | Infraestructura |
|---------|----------|-----------------|
| **Java 17 (OpenJDK)** | React 18 | Docker Compose |
| Spring Boot 3.3.4 | Vite 5 | PostgreSQL 16 |
| Spring Security + JWT | Tailwind CSS | Nginx |
| JPA/Hibernate | Zustand | pgAdmin |
| Maven | | |

### 4. Simplificación de la Sección Screenshots

La sección de capturas de la aplicación web se mantuvo limpia con 4 imágenes representativas:
- Dashboard (vista principal)
- Gastos (gestión de expenses)
- Empleados (listado de employees)
- Modo Oscuro (dark mode del dashboard)

Se eliminó el subtítulo "Aplicación Web" ya que ahora la distinción está clara con la galería de docs en su propia sección.

---

## 📁 Archivos Modificados

| Archivo | Cambio |
|---------|--------|
| `README.md` | Actualización de rutas de imágenes, nueva galería de docs, stack actualizado |
| `docs/images/app/` | Nueva carpeta con imágenes de la app (movidas) |
| `docs/images/docs/` | Nueva carpeta con 4 capturas de documentación |

### Imágenes Añadidas
- `docs/images/docs/home.png`
- `docs/images/docs/architecture.png`
- `docs/images/docs/docker-guide.png`
- `docs/images/docs/api-reference.png`

### Imágenes Movidas
- `docs/images/*.png` → `docs/images/app/*.png`

### Imágenes Eliminadas
- `docs/images/docs/api-reference-2.png` (redundante)
- `docs/images/docs/api-reference-3.png` (redundante)
- `docs/images/docs/api-reference-4.png` (redundante)

---

## 🔗 Commits

1. **`bfb8173`** - `docs: reorganize images and add documentation screenshots`
   - Creación de carpetas `app/` y `docs/`
   - Movimiento de imágenes existentes
   - Añadido de 7 capturas de documentación
   - Actualización inicial del README

2. **`34671bc`** - `docs: simplify docs screenshots and move to Documentation section`
   - Reducción a 4 capturas de docs (eliminadas 3 redundantes)
   - Reubicación de galería de docs bajo sección Documentación
   - Limpieza de la sección Screenshots

3. **`0c848d9`** - `docs: add Java 17 (OpenJDK) to tech stack`
   - Añadido Java 17 (OpenJDK) como primera fila del backend
   - Reorganización de la tabla para mejor legibilidad

---

## 📸 Vista Previa

### Sección Screenshots (App)
```
📸 Screenshots
┌─────────────┬─────────────┐
│  Dashboard  │   Gastos    │
├─────────────┼─────────────┤
│  Empleados  │ Modo Oscuro │
└─────────────┴─────────────┘
```

### Sección Documentación
```
📖 Documentación
[Badge Netlify] Documentación completa desplegada en Netlify →

┌─────────────┬──────────────┐
│    Home     │ Arquitectura │
├─────────────┼──────────────┤
│ Guía Docker │ API Reference│
└─────────────┴──────────────┘

O localmente en /docs/:
- Índice de documentación
- Guía de desarrollo
- Comandos Makefile
```

---

## ✅ Checklist

- [x] Imágenes organizadas en carpetas temáticas
- [x] Rutas actualizadas en README.md
- [x] Galería de docs ubicada en sección correcta
- [x] Stack tecnológico completo con Java 17
- [x] Imágenes redundantes eliminadas
- [x] Commits con mensajes descriptivos
- [x] Rama pusheada a origin

---

## 🚀 Próximos Pasos

1. Merge de esta PR a `dev`
2. Crear PR de `dev` a `main` cuando esté lista la próxima release
3. Considerar añadir más capturas si se implementan nuevas features

---

## 📝 Notas

- Las carpetas se nombraron de forma genérica (`app/`, `docs/`) para no depender de nombres de plataformas específicas (Netlify, Vercel, etc.)
- Se mantuvieron solo 4 capturas de API Reference para no sobrecargar el README
- El orden del stack tecnológico ahora refleja mejor la jerarquía: lenguaje → framework → librerías
