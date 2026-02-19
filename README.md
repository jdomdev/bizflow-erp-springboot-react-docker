# Bizflow ERP

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.4-brightgreen?logo=springboot)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-18.2-61DAFB?logo=react)](https://reactjs.org/)
[![Java](https://img.shields.io/badge/Java-17-ED8B00?logo=openjdk)](https://openjdk.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-336791?logo=postgresql)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?logo=docker)](https://docs.docker.com/compose/)
[![License](https://img.shields.io/badge/License-GPLv3-blue.svg)](LICENSE.txt)

Sistema ERP moderno para gestión de gastos empresariales, empleados y nóminas.

## 📸 Screenshots

<table>
  <tr>
    <td align="center"><strong>Dashboard</strong></td>
    <td align="center"><strong>Gastos</strong></td>
  </tr>
  <tr>
    <td><img src="docs/images/app/dashboard.png" alt="Dashboard" width="400"/></td>
    <td><img src="docs/images/app/expenses.png" alt="Gastos" width="400"/></td>
  </tr>
  <tr>
    <td align="center"><strong>Empleados</strong></td>
    <td align="center"><strong>Modo Oscuro</strong></td>
  </tr>
  <tr>
    <td><img src="docs/images/app/employees.png" alt="Empleados" width="400"/></td>
    <td><img src="docs/images/app/dashboard-dark-mode.png" alt="Modo Oscuro" width="400"/></td>
  </tr>
</table>

## ✨ Características

### Seguridad
- 🔐 **Autenticación JWT** con refresh tokens y control de acceso basado en roles (ADMIN, MANAGER, USER)
- 🛡️ **Spring Security** configurado con protección CSRF y CORS
- 🔒 **Contraseñas encriptadas** con BCrypt

### Funcionalidades
- 📊 **Dashboard interactivo** con estadísticas en tiempo real y gráficos (Chart.js)
- 💰 **Gestión de Gastos** con paginación del lado del servidor, filtros y exportación
- 👥 **Gestión de Empleados** con vinculación automática a usuarios del sistema
- 💵 **Gestión de Nóminas** con cálculos automáticos y historial
- 🏢 **Gestión de Cargos/Posiciones** con jerarquía organizacional

### Experiencia de Usuario
- 🌙 **Modo Oscuro** con persistencia de preferencias en localStorage
- 🔔 **Notificaciones en tiempo real** via WebSocket
- 📱 **Diseño 100% responsive** para móvil, tablet y escritorio
- ⚡ **Rendimiento optimizado** con lazy loading y code splitting

### DevOps y Desarrollo
- 🐳 **Docker Compose** con perfiles para dev, test y producción
- 🔄 **Hot reload** en desarrollo con Vite
- 🧪 **Tests automatizados** con JUnit 5 (backend) y Vitest (frontend)
- 📝 **API REST documentada** con endpoints versionados (/api/v1)

## 🚀 Quick Start

### Prerrequisitos

#### Linux / macOS
- Docker y Docker Compose v2+
- Git
- Node.js 18+ (solo para desarrollo local con Vite)
- Make (generalmente preinstalado)

#### Windows
> ⚠️ **Importante**: Este proyecto usa Makefile y scripts bash. En Windows necesitas **WSL2** (Windows Subsystem for Linux).

1. Instalar [WSL2](https://docs.microsoft.com/es-es/windows/wsl/install) con Ubuntu
2. Instalar [Docker Desktop](https://www.docker.com/products/docker-desktop/) con integración WSL2
3. Ejecutar todos los comandos desde la terminal WSL2

```powershell
# Desde PowerShell (como administrador)
wsl --install -d Ubuntu
```

Una vez en WSL2, los comandos son idénticos a Linux.

### Paso 1: Clonar el repositorio

```bash
git clone https://github.com/jdomdev/bizflow-erp-springboot-react-docker.git
cd bizflow-erp-springboot-react-docker
```

### Paso 2: Configurar credenciales

Antes de ejecutar, debes crear los archivos de credenciales (están ignorados en .gitignore por seguridad):

```bash
# Crear directorio de secrets
mkdir -p scripts/secrets/users_with_passwords

# Copiar plantillas y editar contraseñas
cp scripts/seeds/data/dev/users.json.example scripts/secrets/users_with_passwords/dev_users.json
cp scripts/seeds/data/test/users.json.example scripts/secrets/users_with_passwords/test_users.json

# Editar y reemplazar <SEED_PASSWORD_PLACEHOLDER> con contraseñas reales
# nano scripts/secrets/users_with_passwords/dev_users.json
```

> 📖 Ver [Guía completa de configuración de credenciales](./docs/guide/SETUP_CREDENTIALS.md)

### Paso 3: Ejecutar

```bash
# Construir imágenes base e iniciar entorno desarrollo
make up-dev

# O con docker compose directamente
docker compose --profile dev up -d --build
```

## 🌍 Entornos

### Modo Híbrido: Vite Local + Docker (Recomendado para desarrollo)

Este es el modo **recomendado para desarrolladores** porque ofrece:
- ⚡ **Hot reload instantáneo** en el frontend (< 100ms)
- 🔄 **HMR (Hot Module Replacement)** para CSS y JS
- 🐳 Backend y BD aislados en contenedores

```bash
# Terminal 1: Levantar backend y base de datos en Docker
make up-dev

# Terminal 2: Iniciar servidor Vite para el frontend
cd frontend
npm install      # Solo la primera vez
npm run dev
```

| Servicio | URL | Descripción |
|----------|-----|-------------|
| Frontend (Vite) | http://localhost:3000 | Servidor de desarrollo con HMR |
| Backend API | http://localhost:8082/api/v1 | Spring Boot en Docker |
| Base de datos | localhost:5433 | PostgreSQL en Docker |

> 💡 **Nota**: Vite tiene configurado un proxy para `/api` que redirige automáticamente al backend en el puerto 8082. Ver [vite.config.js](./frontend/vite.config.js).

> ⚠️ **Importante**: Aunque existe un contenedor `frontend-dev` en Docker (puerto 8085), para desarrollo activo se recomienda usar Vite local por la velocidad de recarga.

### Entornos Docker (Todo containerizado)

Para testing, CI/CD o cuando no necesitas hot reload:

| Entorno | Frontend | Backend API | Base de datos | Comando |
|---------|----------|-------------|---------------|--------|
| **Dev** | http://localhost:8085 | http://localhost:8082/api/v1 | localhost:5433 | `make up-dev` |
| **Test** | http://localhost:8086 | http://localhost:8083/api/v1 | localhost:5434 | `make up-test` |
| **Prod** | http://localhost:8080 | http://localhost:8181/api/v1 | localhost:5442 | `make up-prod` |

### Credenciales de acceso

Los archivos de credenciales deben crearse manualmente (están en `.gitignore`):

| Entorno | Usuario admin | Archivo de contraseñas |
|---------|---------------|------------------------|
| Dev | `ada.lovelace@bizflowerp.com` | `scripts/secrets/users_with_passwords/dev_users.json` |
| Test | `ada.lovelace@bizflowerp.com` | `scripts/secrets/users_with_passwords/test_users.json` |
| Prod | `ada.lovelace@bizflowerp.com` | `scripts/secrets/users_with_passwords/prod_users.json` |

> 📖 **Primer uso**: Sigue la [Guía de configuración de credenciales](./docs/guide/SETUP_CREDENTIALS.md) para crear estos archivos.

> ⚠️ **Seguridad**: Las contraseñas de producción **DEBEN** ser diferentes a las de desarrollo.

### pgAdmin (Solo Dev)

- Disponible solo cuando se levanta el perfil `debug` de Docker Compose
- Para usarlo: `docker compose --profile debug up -d`
- URL: http://localhost:5050
- Credenciales: Ver variables en `.env`

## 📖 Documentación

### 🌐 Documentación Online (Recomendada)

<p align="center">
  <a href="https://bizflowerp.netlify.app">
    <img src="https://img.shields.io/badge/📚_Documentaci%C3%B3n_Completa-Netlify-00C7B7?style=for-the-badge&logo=netlify&logoColor=white" alt="Documentación en Netlify"/>
  </a>
</p>

**➡️ [https://bizflowerp.netlify.app](https://bizflowerp.netlify.app)**

La documentación online incluye:
- 🏗️ **Arquitectura del sistema** - Diagramas y explicaciones detalladas
- 🐳 **Guías de Docker** - Comandos, perfiles y troubleshooting
- 🔗 **API Reference** - Todos los endpoints documentados
- 🧪 **Guías de testing** - Estrategias y configuración
- 🚀 **Guías de despliegue** - Configuración de producción

<table>
  <tr>
    <td align="center"><strong>Home</strong></td>
    <td align="center"><strong>Arquitectura</strong></td>
  </tr>
  <tr>
    <td><img src="docs/images/docs/home.png" alt="Docs Home" width="400"/></td>
    <td><img src="docs/images/docs/architecture.png" alt="Arquitectura" width="400"/></td>
  </tr>
  <tr>
    <td align="center"><strong>Guía Docker</strong></td>
    <td align="center"><strong>API Reference</strong></td>
  </tr>
  <tr>
    <td><img src="docs/images/docs/docker-guide.png" alt="Docker Guide" width="400"/></td>
    <td><img src="docs/images/docs/api-reference.png" alt="API Reference" width="400"/></td>
  </tr>
</table>

### 📂 Documentación Local

También disponible en la carpeta [`/docs`](./docs/):
- [Índice de documentación](./docs/INDEX.md)
- [Guía de desarrollo](./docs/guides/DEVELOPMENT_GUIDELINES.md)
- [Comandos Makefile](./docs/makefile/makefile_commands_reference.md)

## 🛠️ Stack Tecnológico

| Backend | Frontend | Infraestructura |
|---------|----------|-----------------|
| Java 17 (OpenJDK) | React 18 | Docker Compose |
| Spring Boot 3.3.4 | Vite 5 | PostgreSQL 16 |
| Spring Security + JWT | Tailwind CSS | Nginx |
| JPA/Hibernate | Zustand | pgAdmin |
| Maven | Vitest | |

## 🎯 Guía para Nuevos Desarrolladores

Si eres nuevo en el proyecto, sigue este orden de lectura:

### 1️⃣ Configuración inicial
1. **Este README** - Visión general y quick start
2. [Configuración de credenciales](./docs/guide/SETUP_CREDENTIALS.md) - **OBLIGATORIO** antes de ejecutar

### 2️⃣ Entender la arquitectura
3. [Arquitectura del sistema](https://bizflowerp.netlify.app/guide/architecture) 🌐 - Estructura general
4. [Guía de desarrollo](./docs/guides/DEVELOPMENT_GUIDELINES.md) - Convenciones y flujos

### 3️⃣ Sistema de datos
5. [Sistema de Seeds](./scripts/seeds/README.md) - Cómo se cargan datos iniciales
6. [Inicialización de BD](./docs/guides/automated-db-initialization-sequence.md) - Secuencia de arranque

### 4️⃣ Docker y comandos
7. [Comandos Makefile](./docs/makefile/makefile_commands_reference.md) - Todos los comandos disponibles
8. [Guía de entornos](./docs/guides/environment-switch-guide.md) - Cambiar entre dev/test/prod

## 📁 Estructura del Proyecto

```
├── backend/          # Spring Boot API
├── frontend/         # React + Vite
├── docker/           # Dockerfiles base
├── scripts/          # Scripts de utilidad
├── sql/              # Migraciones y seeds
├── docs/             # Documentación
└── docker-compose.yml
```

## 🔧 Comandos Útiles

```bash
# Entornos
make up-dev           # Iniciar entorno desarrollo
make up-prod          # Iniciar entorno producción
make up-test          # Iniciar entorno testing
make down-dev         # Detener entorno desarrollo
make down-prod        # Detener entorno producción
make down-test        # Detener entorno testing

# Base de datos
make backup-dev       # Crear backup de BD (desarrollo)
make backup-prod      # Crear backup de BD (producción)
make backup-test      # Crear backup de BD (testing)
make backup-all       # Crear backup de todas las bases de datos

# Ayuda
make help             # Ver todos los comandos disponibles
```

## 🤝 Contribuir

1. Fork del repositorio
2. Crear rama feature (`git checkout -b feat/nueva-funcionalidad`)
3. Commit cambios (`git commit -m 'feat: añadir nueva funcionalidad'`)
4. Push a la rama (`git push origin feat/nueva-funcionalidad`)
5. Abrir Pull Request

## 📄 Licencia

Este proyecto está bajo la Licencia GNU General Public License v3.0 - ver [LICENSE.txt](LICENSE.txt) para más detalles.

---

<p align="center">
  Desarrollado con ☕ y 💻
</p>
