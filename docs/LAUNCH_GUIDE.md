# 🚀 Guía de Lanzamiento - ExpenseNoteApp v1.1.0

Una guía paso a paso para ejecutar la aplicación completa (backend + frontend) en tu ambiente local.

## 📋 Tabla de Contenidos

1. [Requisitos Previos](#requisitos-previos)
2. [Configuración Rápida (5 minutos)](#configuración-rápida)
3. [Configuración Detallada](#configuración-detallada)
4. [Ejecutar la Aplicación](#ejecutar-la-aplicación)
5. [Verificar que Todo Funciona](#verificar-que-todo-funciona)
6. [Credenciales de Prueba](#credenciales-de-prueba)
7. [Troubleshooting](#troubleshooting)
8. [Parar la Aplicación](#parar-la-aplicación)

---

## 📦 Requisitos Previos

### Software Requerido

| Software | Versión Mínima | Descarga |
|----------|---|---|
| Java JDK | 21+ | [oracle.com/java](https://www.oracle.com/java/technologies/downloads/) |
| Node.js | 18+ | [nodejs.org](https://nodejs.org/) |
| PostgreSQL | 12+ | [postgresql.org](https://www.postgresql.org/download/) |
| Maven | 3.6+ | Incluido con Java en muchas distros |
| Git | Latest | [git-scm.com](https://git-scm.com/) |

### Verificar Instalación

```bash
# Verificar Java
java -version
# Esperado: openjdk 21 o superior

# Verificar Node.js
node --version
npm --version
# Esperado: v18+ para Node, 9+ para npm

# Verificar PostgreSQL
psql --version
# Esperado: psql 12 o superior

# Verificar Maven
mvn --version
# Esperado: 3.6 o superior
```

---

## ⚡ Configuración Rápida (5 minutos)

Si ya tienes todo instalado y solo quieres lanzar:

```bash
# 1. Clonar repositorio
git clone https://github.com/jdomdev/expense-note-app-springboot.git
cd expense-note-app-springboot

# 2. Crear base de datos (en terminal de PostgreSQL)
createdb expense_note_app

# 3. Editar configuración (reemplazar valores)
cd backend-springboot
# Editar: src/main/resources/application.properties
# Cambiar: spring.datasource.username y password

# 4. Terminal 1: Backend
mvn clean spring-boot:run

# 5. Terminal 2: Frontend
cd ../frontend
npm install
npm run dev

# ✅ Backend: http://localhost:8080
# ✅ Frontend: http://localhost:3000
```

---

## 🔧 Configuración Detallada

### Paso 1: Clonar el Repositorio

```bash
git clone https://github.com/jdomdev/expense-note-app-springboot.git
cd expense-note-app-springboot
```

### Paso 2: Configurar Base de Datos PostgreSQL

#### En Linux/Mac:

```bash
# Conectar a PostgreSQL
psql -U postgres

# Crear base de datos
CREATE DATABASE expense_note_app;

# Ver que se creó correctamente
\l

# Salir
\q
```

#### En Windows (Command Prompt):

```cmd
REM Conectar a PostgreSQL
psql -U postgres

REM Crear base de datos
CREATE DATABASE expense_note_app;

REM Ver que se creó correctamente
\l

REM Salir
\q
```

#### En pgAdmin (GUI):

1. Abrir pgAdmin
2. Right-click en "Databases"
3. Select "Create → Database"
4. Name: `expense_note_app`
5. Click "Save"

### Paso 3: Configurar Backend Spring Boot

```bash
cd backend-springboot
```

Abrir `src/main/resources/application.properties`:

```properties
# Configuración original (cambiar según tu setup)
spring.datasource.url=jdbc:postgresql://localhost:5432/expense_note_app
spring.datasource.username=postgres
spring.datasource.password=YOUR_PASSWORD_HERE
spring.datasource.driver-class-name=org.postgresql.Driver

# JWT Configuration
app.jwt.secret=YOUR_JWT_SECRET_KEY_MIN_32_CHARS_LONG_HERE
app.jwt.expiration=86400000

# Server Port
server.port=8080

# JPA/Hibernate
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
```

**⚠️ IMPORTANTE**: 
- Cambiar `YOUR_PASSWORD_HERE` con tu contraseña PostgreSQL
- Generar una clave JWT segura (mínimo 32 caracteres)
- No comprometer `application.properties` en Git

#### Generar JWT Secret Seguro

```bash
# En Linux/Mac/WSL:
openssl rand -base64 32

# Ejemplo de salida:
# kN8x9mK2p3L5q6R7sT8uV9wX0yZ1aB2cD3eF4gH5iJ6kL7mN8oP9qR0sTuVwXyZ1
```

### Paso 4: Compilar Backend

```bash
cd backend-springboot
mvn clean compile
```

Esperado: `BUILD SUCCESS`

```
[INFO] --------------------------------[ jar ]--------------
[INFO]
[INFO] --- maven-compiler-plugin:3.13.0:compile (default-compile) @ expensenoteapp ---
[INFO] Compiling 62 source files with javac
[INFO] BUILD SUCCESS
```

### Paso 5: Configurar Frontend React

```bash
cd frontend

# Crear archivo .env desde .env.example
cp .env.example .env
```

Editar `frontend/.env`:

```env
VITE_API_URL=http://localhost:8080
```

Instalar dependencias:

```bash
npm install
```

---

## ▶️ Ejecutar la Aplicación

### Opción A: Terminal Separadas (Recomendado)

#### Terminal 1: Backend Spring Boot

```bash
cd backend-springboot
mvn spring-boot:run
```

Esperado (después de ~30 segundos):

```
Started ExpenseNoteAppApplication in 15.432 seconds
INFO 12345 --- [main] org.springframework.boot.web.embedded.tomcat.TomcatWebServer
Tomcat started on port(s): 8080 (http)
```

#### Terminal 2: Frontend Vite

```bash
cd frontend
npm run dev
```

Esperado:

```
VITE v5.0.0 ready in XXX ms

➜  Local:   http://localhost:3000/
➜  Press h to show help
```

### Opción B: Scripts Automáticos

#### Linux/Mac (crear `run.sh`):

```bash
#!/bin/bash

# Backend en background
cd backend-springboot
mvn spring-boot:run &
BACKEND_PID=$!

# Frontend en background
cd ../frontend
npm run dev &
FRONTEND_PID=$!

echo "Backend PID: $BACKEND_PID"
echo "Frontend PID: $FRONTEND_PID"
echo ""
echo "Backend: http://localhost:8080"
echo "Frontend: http://localhost:3000"
echo ""
echo "Para parar: kill $BACKEND_PID $FRONTEND_PID"

wait
```

#### Windows (crear `run.bat`):

```batch
@echo off

REM Backend en terminal separada
start "ExpenseNoteApp Backend" cmd /k "cd backend-springboot && mvn spring-boot:run"

REM Frontend en terminal separada
start "ExpenseNoteApp Frontend" cmd /k "cd frontend && npm install && npm run dev"

echo Backend: http://localhost:8080
echo Frontend: http://localhost:3000
```

---

## ✅ Verificar que Todo Funciona

### 1. Verificar Backend

```bash
# En otra terminal
curl http://localhost:8080/api/health

# O en navegador: http://localhost:8080/api/health
```

Esperado: `200 OK` (o similar)

### 2. Verificar Frontend

Abrir en navegador: **http://localhost:3000**

Esperado: Página de login con formulario

### 3. Verificar Conexión Base de Datos

```bash
# En terminal PostgreSQL
psql -U postgres -d expense_note_app

# Ver tablas creadas
\dt

# Debería mostrar tablas como:
# expense_user, expense, employee, payroll, position, role, etc.

\q
```

### 4. Verificar JWT Token

```bash
# Request login
curl -X POST http://localhost:8080/api/authentication/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@example.com","password":"admin123"}'

# Esperado: JSON con token JWT
# {"token":"eyJhbGciOiJIUzUxMiJ9...", "user": {...}}
```

---

## 🔐 Credenciales de Prueba

### Usuario Admin (Ya debe existir)

```
Email: admin@example.com
Password: admin123
Rol: ROLE_ADMIN
```

### Crear Nuevo Usuario

1. Ir a http://localhost:3000
2. Click en "Signup"
3. Rellenar formulario:
   - Email: `user@example.com`
   - Password: `password123`
   - Confirm Password: `password123`
4. Click "Register"
5. Login con nuevas credenciales

---

## 🐛 Troubleshooting

### Error: "Port 8080 already in use"

```bash
# Linux/Mac: Encontrar qué proceso usa puerto 8080
lsof -i :8080

# Matar el proceso
kill -9 <PID>

# Windows: Encontrar proceso en puerto 8080
netstat -ano | findstr :8080

# Matar el proceso (reemplazar PID)
taskkill /PID <PID> /F
```

### Error: "Port 3000 already in use"

```bash
# Cambiar puerto en frontend
npm run dev -- --port 3001
```

### Error: "Connection refused" a PostgreSQL

```bash
# Verificar que PostgreSQL está corriendo
# Linux
systemctl status postgresql

# Mac
brew services list | grep postgres

# Windows
# Services → PostgreSQL should be running
```

### Error: "Database does not exist"

```bash
# Recrear base de datos
psql -U postgres

CREATE DATABASE expense_note_app;
\q
```

### Error: "FATAL: Ident authentication failed for user"

En `pg_hba.conf` cambiar `ident` a `md5` o `scram-sha-256`:

```
# En Linux/Mac: /etc/postgresql/14/main/pg_hba.conf
# En Windows: Program Files/PostgreSQL/14/data/pg_hba.conf

# Cambiar de:
local   all             all                                     ident

# A:
local   all             all                                     md5
```

### Error: "Cannot find module" en Frontend

```bash
cd frontend
rm -rf node_modules package-lock.json
npm install
npm run dev
```

### Error: "BUILD FAILURE" en Backend

```bash
cd backend-springboot
mvn clean compile -U

# Si persiste, verificar:
# 1. Java 21+ instalado
# 2. Maven 3.6+ instalado
# 3. Conexión a Internet (descargar dependencias)
```

### Error: "Invalid JWT Token"

1. Verificar que `app.jwt.secret` en `application.properties` es correcto
2. Verificar que token no ha expirado (24 horas)
3. Verificar que token se envía correctamente en header:
   ```
   Authorization: Bearer <token>
   ```

### No se puede acceder a http://localhost:3000

1. Verificar que frontend está corriendo (`npm run dev`)
2. Verificar que no hay error en consola
3. Limpiar caché del navegador (Ctrl+Shift+Delete)
4. Intentar incógnito/privado

---

## 🛑 Parar la Aplicación

### Parar Backend

```bash
# En terminal backend, presionar: Ctrl + C
```

### Parar Frontend

```bash
# En terminal frontend, presionar: Ctrl + C
```

### Script para Parar Todo (Linux/Mac)

```bash
# Encontrar procesos
ps aux | grep "java\|node"

# Matar procesos específicos
kill <PID>
```

### Script para Parar Todo (Windows)

```cmd
REM Matar java (backend)
taskkill /IM java.exe /F

REM Matar node (frontend)
taskkill /IM node.exe /F
```

---

## 📊 Arquitectura de la Aplicación

```
ExpenseNoteApp v1.1.0
├── Backend (Spring Boot 3.3.4)
│   ├── API REST en puerto 8080
│   ├── JWT Authentication
│   ├── PostgreSQL Database
│   └── DTO/DAO patterns
│
├── Frontend (React 18 + Vite)
│   ├── SPA en puerto 3000
│   ├── Tailwind CSS
│   ├── Zustand Store
│   └── Axios HTTP Client
│
└── Database (PostgreSQL)
    ├── Users & Roles
    ├── Employees
    ├── Expenses
    └── Payroll
```

---

## 📚 Recursos Adicionales

- **Backend Docs**: [docs/SECURITY.md](./docs/SECURITY.md)
- **Frontend Docs**: [frontend/README.md](../frontend/README.md)
- **Cambios v1.1.0**: [docs/CAMBIOS_V2.md](./docs/CAMBIOS_V2.md)
- **Análisis Detallado**: [docs/ANALISIS_DETALLADO.md](./docs/ANALISIS_DETALLADO.md)
- **API Documentation**: Ver [docs/INDEX.md](./docs/INDEX.md)

---

## 💡 Tips y Mejores Prácticas

### Desarrollo Local

```bash
# Hot reload automático (backend)
# Spring Boot recompila automáticamente

# Hot reload (frontend)
# Vite reinicia automáticamente al cambiar archivos

# Ver logs en tiempo real
tail -f backend-springboot/logs/*.log
```

### Testing API

```bash
# Usar curl
curl -X GET http://localhost:8080/api/expenses \
  -H "Authorization: Bearer <token>"

# O usar Postman/Insomnia
# Importar colección desde: /docs/postman_collection.json
```

### Performance

```bash
# Verificar uso de memoria
top  # Linux
Activity Monitor  # Mac
Task Manager  # Windows

# Backend consume ~400MB
# Frontend consume ~150MB
```

---

## 🆘 ¿Ayuda?

Si tienes problemas:

1. Revisa [SECURITY.md](./docs/SECURITY.md) para problemas de seguridad
2. Revisa [ANALISIS_DETALLADO.md](./docs/ANALISIS_DETALLADO.md) para problemas técnicos
3. Abre un issue en GitHub con detalles del error
4. Verifica logs:
   - Backend: `backend-springboot/logs/`
   - Frontend: Browser DevTools (F12)

---

**Última actualización**: Noviembre 26, 2024
**Versión**: 1.1.0
**Mantenido por**: ExpenseNoteApp Team

¡Felicidades! Ya tienes ExpenseNoteApp corriendo localmente. 🎉
