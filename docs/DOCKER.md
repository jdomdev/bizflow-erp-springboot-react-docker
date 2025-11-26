# Docker & Containerization - ExpenseNoteApp v1.1.0

Documentación completa de Docker, docker-compose y configuración de dev-containers.

## 📋 Tabla de Contenidos

1. [Arquitectura de Containers](#arquitectura-de-containers)
2. [Requisitos](#requisitos)
3. [Quick Start con Docker](#quick-start-con-docker)
4. [Dockerfile del Backend](#dockerfile-del-backend)
5. [Dockerfile del Frontend](#dockerfile-del-frontend)
6. [Docker Compose](#docker-compose)
7. [Dev Containers (VS Code)](#dev-containers-vs-code)
8. [Comandos Útiles](#comandos-útiles)
9. [Troubleshooting](#troubleshooting)

---

## 🏗️ Arquitectura de Containers

```
┌─────────────────────────────────────────────────────┐
│                  Docker Network                      │
│         (expense_network - bridge)                   │
├─────────────────────────────────────────────────────┤
│                                                      │
│  ┌──────────────┐  ┌──────────────┐  ┌────────────┐ │
│  │   Frontend   │  │   Backend    │  │ PostgreSQL │ │
│  │   (nginx)    │  │ (Spring Boot)│  │ (DB)       │ │
│  │              │  │              │  │            │ │
│  │ Port: 80     │  │ Port: 8080   │  │ Port: 5432 │ │
│  │ alpine       │  │ Java 21 JRE  │  │ alpine     │ │
│  └──────────────┘  └──────────────┘  └────────────┘ │
│        ▲                  ▲                  ▲        │
│        │                  │                  │        │
│        └──────────────────┴──────────────────┘        │
│         Docker Compose Orchestration                 │
│                                                      │
└─────────────────────────────────────────────────────┘

Volume Mappings:
  postgres_data → /var/lib/postgresql/data
  logs          → backend logs directory
```

---

## 📦 Requisitos

### Software Requerido

| Software | Versión Mínima |
|----------|---|
| Docker | 20.10+ |
| Docker Compose | 2.0+ |
| Git | Latest |

### Verificar Instalación

```bash
docker --version
# Docker version 24.0.0, build abcdef1

docker-compose --version
# Docker Compose version 2.20.0

docker ps
# Debería mostrar lista vacía de containers (o existentes)
```

---

## 🚀 Quick Start con Docker

### Opción 1: Docker Compose (Recomendado)

```bash
# 1. Clonar repositorio
git clone https://github.com/jdomdev/expense-note-app-springboot.git
cd expense-note-app-springboot

# 2. Crear archivo .env (opcional, con valores por defecto)
cat > .env << EOF
DB_USER=postgres
DB_PASSWORD=postgres123
JWT_SECRET=kN8x9mK2p3L5q6R7sT8uV9wX0yZ1aB2cD3eF4gH5iJ6kL7mN8oP9qR0sTuVwXyZ1
SPRING_PROFILE=prod
EOF

# 3. Iniciar containers
docker-compose up -d

# 4. Verificar que todo está corriendo
docker-compose ps

# 5. Ver logs
docker-compose logs -f

# 6. Acceder a la aplicación
# Frontend: http://localhost
# Backend: http://localhost:8080
# pgAdmin: http://localhost:5050 (usuario admin@example.com, password admin)
```

### Opción 2: Detener Containers

```bash
# Detener sin eliminar
docker-compose stop

# Reiniciar
docker-compose start

# Detener y eliminar
docker-compose down

# Detener, eliminar y limpiar volúmenes
docker-compose down -v
```

---

## 🐳 Dockerfile del Backend

### Ubicación

```
backend-springboot/Dockerfile
```

### Características

```dockerfile
# Multi-stage build

# Stage 1: Build
FROM maven:3.9.5-eclipse-temurin-21-alpine AS builder
- Compila el código Java
- Descarga dependencias Maven
- Genera JAR ejecutable

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine
- Imagen base JRE ligera (alpine)
- Copia JAR del stage builder
- Usuario no-root: spring
- Health check integrado
- Expone puerto 8080
```

### Construcción Manual

```bash
# Compilar imagen
docker build -t expense-backend:1.1.0 ./backend-springboot

# Ejecutar container
docker run -d \
  --name expense_backend \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/expense_note_app \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=postgres123 \
  -e APP_JWT_SECRET=kN8x9mK2p3L5q6R7sT8uV9wX0yZ1aB2cD3eF4gH5iJ6kL7mN8oP9qR0sTuVwXyZ1 \
  expense-backend:1.1.0

# Ver logs
docker logs -f expense_backend

# Verificar health
curl http://localhost:8080/actuator/health
```

### Tamaño de Imagen

```
Multi-stage: ~400MB (builder: ~900MB, final: ~400MB)
Componentes:
  - Base JRE: ~200MB
  - JAR Spring Boot: ~150MB
  - Other: ~50MB
```

---

## 🌐 Dockerfile del Frontend

### Ubicación

```
frontend/Dockerfile
```

### Características

```dockerfile
# Multi-stage build

# Stage 1: Build
FROM node:20-alpine AS builder
- Instala dependencias npm
- Compila React app con Vite
- Genera archivos estáticos

# Stage 2: Runtime
FROM nginx:alpine
- Sirve archivos estáticos con nginx
- Proxy para /api/ → backend
- Compresión gzip
- Security headers
- Usuario no-root: nginx
- Health check integrado
- Expone puerto 80
```

### nginx.conf

```nginx
# Características principales:
- SPA routing (todos los paths → index.html)
- API proxy hacia backend
- Compresión gzip
- Security headers (X-Frame-Options, CSP, etc)
- Cache control para assets estáticos
- Health check endpoint (/health)
- Deny hidden files
- Worker processes automático
```

### Construcción Manual

```bash
# Compilar imagen
docker build -t expense-frontend:1.1.0 ./frontend

# Ejecutar container
docker run -d \
  --name expense_frontend \
  -p 3000:80 \
  -e VITE_API_URL=http://localhost:8080 \
  expense-frontend:1.1.0

# Verificar health
curl http://localhost:3000/health

# Acceder a la app
# http://localhost:3000
```

### Tamaño de Imagen

```
Multi-stage: ~50MB
Componentes:
  - Base nginx: ~40MB
  - Built app: ~5MB
  - Other: ~5MB
```

---

## 🐙 Docker Compose

### Archivo: docker-compose.yml

#### Servicios

**1. PostgreSQL**
```yaml
Imagen: postgres:16-alpine
Puerto: 5432
Volumen: postgres_data
Variables de entorno:
  - POSTGRES_DB: expense_note_app
  - POSTGRES_USER: postgres (configurable)
  - POSTGRES_PASSWORD: postgres (configurable)
Health check: pg_isready
```

**2. pgAdmin (Opcional)**
```yaml
Imagen: dpage/pgadmin4
Puerto: 5050
Perfil: debug (solo si se especifica)
Usuarios:
  - Email: admin@example.com
  - Password: admin
```

**3. Backend Spring Boot**
```yaml
Build: Desde ./backend-springboot/Dockerfile
Puerto: 8080
Depende de: postgres (service_healthy)
Volumen: logs directory
Env vars:
  - SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/...
  - SPRING_DATASOURCE_USERNAME: ${DB_USER:-postgres}
  - SPRING_DATASOURCE_PASSWORD: ${DB_PASSWORD:-postgres}
  - APP_JWT_SECRET: ${JWT_SECRET:-...}
  - SPRING_PROFILES_ACTIVE: ${SPRING_PROFILE:-prod}
Health check: GET /actuator/health
```

**4. Frontend React**
```yaml
Build: Desde ./frontend/Dockerfile
Puerto: 80 (localhost) / 3000 (docker)
Depende de: backend (service_healthy)
Env vars:
  - VITE_API_URL: http://backend:8080
Health check: GET /health
```

#### Red

```yaml
Networks:
  - expense_network (bridge)
Permite comunicación entre servicios:
  - frontend ← backend (via http://backend:8080)
  - backend ← postgres (via postgresql://postgres:5432)
```

#### Volúmenes

```yaml
postgres_data:
  - Persistencia de datos PostgreSQL
  - Driver: local
  - No se elimina con docker-compose down (sin -v)
```

### Archivo .env

```env
# Database
DB_USER=postgres
DB_PASSWORD=postgres123

# JWT Secret (generar con: openssl rand -base64 32)
JWT_SECRET=kN8x9mK2p3L5q6R7sT8uV9wX0yZ1aB2cD3eF4gH5iJ6kL7mN8oP9qR0sTuVwXyZ1

# Spring Profile (dev, test, prod)
SPRING_PROFILE=prod
```

### Comandos Docker Compose

```bash
# Iniciar services
docker-compose up -d

# Ver status
docker-compose ps

# Ver logs
docker-compose logs -f              # All services
docker-compose logs -f backend      # Specific service
docker-compose logs -f --tail 100   # Last 100 lines

# Detener
docker-compose stop

# Iniciar tras stop
docker-compose start

# Reiniciar servicio
docker-compose restart backend

# Ejecutar comando en container
docker-compose exec backend bash

# Eliminar todo
docker-compose down -v

# Build personalizado
docker-compose build --no-cache

# Iniciar con pgAdmin
docker-compose --profile debug up -d
```

---

## 💻 Dev Containers (VS Code)

### Requisitos

1. Instalar VS Code
2. Instalar extensión "Dev Containers"
   ```
   ms-vscode-remote.remote-containers
   ```
3. Docker debe estar corriendo

### Configuración

#### Archivo: .devcontainer/devcontainer.json

```json
{
  "name": "ExpenseNoteApp Development",
  "image": "mcr.microsoft.com/devcontainers/java:1-21-bullseye",
  "features": {
    "ghcr.io/devcontainers/features/docker-outside-of-docker:latest": {},
    "ghcr.io/devcontainers/features/github-cli:latest": {},
    "ghcr.io/devcontainers/features/node:latest": {
      "version": "20"
    }
  },
  "forwardPorts": [3000, 5432, 8080, 5050],
  "customizations": {
    "vscode": {
      "extensions": [
        "ms-vscode.Extension-Pack-for-Java",
        "ms-azuretools.vscode-docker",
        "eamodio.gitlens",
        "sonarsource.sonarlint-vscode"
      ]
    }
  },
  "postCreateCommand": "bash .devcontainer/post-create.sh"
}
```

### Características

- ✅ Java 21 + Maven pre-instalados
- ✅ Node.js 20 + npm pre-instalados
- ✅ Docker-in-Docker para ejecutar containers
- ✅ GitHub CLI pre-instalado
- ✅ VS Code extensions configuradas automáticamente
- ✅ Port forwarding automático
- ✅ Script post-creación para setup inicial

### Uso

#### Opción 1: Command Palette

```
1. Ctrl+Shift+P (Cmd+Shift+P en Mac)
2. Buscar: "Dev Containers: Open Folder in Container"
3. Seleccionar carpeta del proyecto
4. VS Code reinicia y abre dev container
```

#### Opción 2: VS Code Remoto

```
1. Click en ícono remoto (esquina inferior-izquierda)
2. Seleccionar "Reopen in Container"
```

### Script post-creación

```bash
.devcontainer/post-create.sh

Ejecuta automáticamente:
1. Actualizar paquetes del sistema
2. Instalar herramientas (git, curl, etc)
3. Configurar PostgreSQL client
4. Instalar npm packages globales
5. Mostrar instrucciones de inicio
```

### Desarrollo en Dev Container

```bash
# Terminal dentro del container

# Backend
cd backend-springboot
mvn clean compile     # Verificar
mvn spring-boot:run   # Ejecutar

# Frontend (en otra terminal)
cd frontend
npm install
npm run dev

# Base de datos
psql -h postgres -U postgres -d expense_note_app
```

---

## 🛠️ Comandos Útiles

### Docker

```bash
# Información general
docker version
docker info
docker ps                    # Containers corriendo
docker ps -a                 # Todos los containers
docker images                # Imágenes disponibles

# Build y Run
docker build -t myapp:1.0 .
docker run -d -p 8080:8080 myapp:1.0
docker run -it myapp:1.0 bash  # Interactive bash

# Logs y Debug
docker logs <container_id>
docker logs -f <container_id>  # Follow logs
docker logs --tail 100 <container_id>
docker exec -it <container_id> bash
docker inspect <container_id>

# Limpieza
docker stop $(docker ps -q)           # Parar todos
docker rm $(docker ps -aq)            # Eliminar todos
docker rmi <image_id>                 # Eliminar imagen
docker system prune -a                # Limpiar todo (CAREFUL!)
```

### Docker Compose

```bash
# Información
docker-compose ps
docker-compose logs -f
docker-compose config           # Ver configuración parseada

# Control
docker-compose up -d
docker-compose down
docker-compose stop
docker-compose start
docker-compose restart

# Build
docker-compose build
docker-compose build --no-cache

# Ejecución
docker-compose exec backend bash
docker-compose exec postgres psql -U postgres
docker-compose run --rm backend mvn clean verify

# Debugging
docker-compose logs -f backend
docker-compose top postgres
docker-compose stats
```

### PostgreSQL

```bash
# Conectar desde host
psql -h localhost -U postgres -d expense_note_app

# Conectar desde docker-compose
docker-compose exec postgres psql -U postgres

# Comandos útiles
\dt               # Listar tablas
\du               # Listar usuarios
SELECT * FROM expense_user;
\l                # Listar bases de datos
\q                # Salir
```

---

## 🐛 Troubleshooting

### Error: "Port already in use"

```bash
# Linux/Mac: Encontrar qué usa el puerto
lsof -i :8080

# Matar el proceso
kill -9 <PID>

# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

### Error: "Cannot connect to Docker daemon"

```bash
# Verificar que Docker está corriendo
docker ps

# Si no funciona:
# Linux: sudo systemctl start docker
# Mac: open /Applications/Docker.app
# Windows: Abrir Docker Desktop
```

### Error: "Service 'backend' failed to build"

```bash
# Limpiar build cache
docker-compose build --no-cache

# Ver detalles del error
docker-compose build backend --verbose

# Verificar Dockerfile
docker build --rm -f backend-springboot/Dockerfile ./backend-springboot
```

### Error: "Health check failed"

```bash
# Ver logs del container
docker-compose logs -f backend

# Verificar endpoint de salud
docker-compose exec backend curl http://localhost:8080/actuator/health

# Aumentar timeout en docker-compose.yml si es necesario
healthcheck:
  start_period: 60s  # Aumentar si es muy lento
```

### Error: "Database connection refused"

```bash
# Verificar que postgres está corriendo
docker-compose ps

# Ver logs de postgres
docker-compose logs postgres

# Esperar a que postgres esté listo
docker-compose exec backend bash -c "
  until pg_isready -h postgres -p 5432; do
    echo 'Esperando postgres...'
    sleep 1
  done
"
```

### Performance: Container consume mucha memoria

```bash
# Limitar memoria
docker-compose down
docker system prune -a --volumes

# En backend, limitar JVM
JAVA_OPTS="-Xmx512m -Xms256m"
```

### Error: "Module not found" en Frontend

```bash
# Reconstruir sin cache
docker-compose build --no-cache frontend

# Limpiar node_modules
docker-compose exec frontend rm -rf node_modules package-lock.json
docker-compose build --no-cache frontend
```

---

## 📊 Monitoreo

### Ver recursos utilizados

```bash
# Memory, CPU, I/O
docker-compose stats

# Detalles de un container
docker stats <container_id>
```

### Ver actividad de red

```bash
# Entre containers
docker network inspect expense_network

# Tráfico entre servicios
docker-compose exec backend netstat -tuln
```

---

## 🔒 Seguridad en Docker

### Mejores Prácticas

```dockerfile
# ✅ NO HACER ESTO
RUN apt-get install password123

# ✅ HACER ESTO
ENV PASSWORD=${DB_PASSWORD}
RUN apt-get install ${PASSWORD}

# ✅ Usuario no-root
RUN useradd -m appuser
USER appuser

# ✅ Escanear vulnerabilidades
docker run --rm -v /var/run/docker.sock:/var/run/docker.sock \
  aquasec/trivy image myapp:1.0
```

### Credenciales Seguras

```bash
# ✅ Usar .env
DB_PASSWORD=YOUR_STRONG_PASSWORD_HERE

# ❌ NO hardcodear en docker-compose.yml
# ❌ NO commitear .env a Git
```

---

## 📝 Notas Importantes

1. **Volúmenes de Data**: `postgres_data` persiste aunque hagas `docker-compose down`
   - Para eliminar: `docker-compose down -v`

2. **Variables de Entorno**: 
   - Cargar desde `.env` o especificar en línea
   - Varias opciones en el fichero `.env`

3. **Health Checks**:
   - Backend espera ~40s para estar healthy
   - Frontend espera ~10s
   - Esto es normal en primera ejecución

4. **Logs**:
   - Usar `docker-compose logs -f` para debug
   - En producción, dirigir a ELK stack o CloudWatch

5. **Red**:
   - Todos los servicios en `expense_network` se ven entre sí
   - Usar hostnames: `postgres`, `backend`, etc.

---

**Última actualización**: Noviembre 26, 2024
**Versión**: 1.1.0
**Mantenido por**: ExpenseNoteApp Team
