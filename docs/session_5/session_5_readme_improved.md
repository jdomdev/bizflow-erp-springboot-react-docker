# 🏦 ExpenseNoteApp - Sistema de Gestión de Gastos

[![Status](https://img.shields.io/badge/status-active--development-blue)]()
[![Version](https://img.shields.io/badge/version-1.0.0--alpha-orange)]()
[![License](https://img.shields.io/badge/license-MIT-green)](LICENSE.txt)

---

## 📌 Descripción del Proyecto

**ExpenseNoteApp** es una aplicación web full-stack para gestión de gastos corporativos con soporte para múltiples roles de usuario (ADMIN, MANAGER, USER). Permite a los empleados registrar, categorizar y reportar gastos, mientras que los managers pueden revisar y aprobar gastos de su equipo.

**Características principales:**
- ✅ Autenticación segura con JWT
- ✅ Control de acceso basado en roles (RBAC)
- ✅ Gestión de gastos (CRUD)
- ✅ Dashboard personalizado según rol
- ✅ Reportes y estadísticas
- ✅ Persistencia en PostgreSQL
- ✅ Dockerizado y listo para producción

---

## 🚀 Stack Tecnológico

### Frontend
- **Framework:** React 18.x + Vite
- **State Management:** Zustand
- **HTTP Client:** Axios
- **Routing:** React Router 6.x
- **Styling:** CSS3 + Responsive Design

### Backend
- **Framework:** Spring Boot 3.3.5
- **Seguridad:** Spring Security 6.1.x
- **ORM:** Spring Data JPA + Hibernate
- **JWT:** io.jsonwebtoken
- **Base de Datos:** PostgreSQL 15
- **Build:** Maven 3.8.x

### DevOps
- **Containerización:** Docker + Docker Compose
- **Web Server:** Nginx
- **Database:** PostgreSQL (persistent volume)

---

## 📋 Requisitos Previos

### Sistema
- Docker y Docker Compose instalados
- Git configurado
- Terminal/Shell disponible

### Verificar instalación
```bash
# Verificar Docker
docker --version
docker-compose --version

# Verificar Git
git --version
```

---

## ⚙️ Instalación y Configuración

### 1. Clonar el Repositorio

```bash
git clone https://github.com/yourusername/ExpenseNoteApp.git
cd ExpenseNoteApp
```

### 2. Configurar Variables de Entorno

```bash
# Backend
cd backend
cp .env.example .env
# Editar .env con valores necesarios

# Frontend
cd ../frontend
cp .env.example .env
# Editar .env con valores necesarios
```

### 3. Levantar la Aplicación con Docker

```bash
# Desde la raíz del proyecto
docker-compose up -d

# Verificar que los contenedores estén sanos
docker-compose ps

# Ver logs en tiempo real
docker-compose logs -f
```

**Esperar a que todos los servicios estén "healthy" (~30 segundos)**

### 4. Verificar Acceso

```bash
# Frontend
open http://localhost

# Backend Health Check
curl http://localhost:8080/actuator/health

# Database Connection
curl http://localhost:8080/api/v1/auth/check-email?email=test@example.com
```

---

## 👥 Usuarios de Prueba

### Creados en Session 5

| Email | Rol | Contraseña | Creado por |
|-------|-----|-----------|-----------|
| testuser1@example.com | USER | TestPass123456 | API Signup |
| testuser2@example.com | USER | TestPass123456 | API Signup |
| frontendtest1@example.com | USER | FrontEnd123456 | API Signup |
| admin1@example.com | ADMIN | AdminPass123456 | DB INSERT |
| admin2@example.com | ADMIN | AdminPass123456 | DB INSERT |
| manager1@example.com | MANAGER | ManagerPass123456 | DB INSERT |
| manager2@example.com | MANAGER | ManagerPass123456 | DB INSERT |

### Crear Nuevo Usuario vía API

```bash
curl -X POST http://localhost:8080/api/v1/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Nuevo",
    "surname": "Usuario",
    "email": "newuser@example.com",
    "password": "SecurePassword123"
  }'
```

---

## 🔐 Flujo de Autenticación

### 1. Signup (Registro)

```bash
POST /api/v1/auth/signup
Content-Type: application/json

{
  "username": "usuario123",
  "email": "usuario@example.com",
  "password": "SecurePass123"
}

Respuesta: 201 Created
{
  "id": 1,
  "username": "usuario123",
  "email": "usuario@example.com",
  "message": "User registered successfully"
}
```

### 2. Login (Autenticación)

```bash
POST /api/v1/auth/login
Content-Type: application/json

{
  "email": "usuario@example.com",
  "password": "SecurePass123"
}

Respuesta: 200 OK
{
  "email": "usuario@example.com",
  "accessToken": "<JWT_TOKEN_PLACEHOLDER>"
}
```

### 3. Usar Token en Requests Autenticados

```bash
GET /api/v1/users/profile
Authorization: Bearer <JWT_TOKEN_PLACEHOLDER>

Respuesta: 200 OK
{
  "id": 1,
  "email": "usuario@example.com",
  "roles": ["USER"],
  "username": "usuario123"
}
```

---

## 📁 Estructura del Proyecto

```
ExpenseNoteApp/
├── backend/          # Spring Boot backend
│   ├── src/main/java/...       # Código fuente Java
│   ├── src/main/resources/     # Configuración, BD migrations
│   ├── pom.xml                  # Dependencias Maven
│   └── Dockerfile               # Docker image backend
│
├── frontend/                    # React frontend
│   ├── src/
│   │   ├── pages/              # Componentes de páginas
│   │   ├── components/         # Componentes reutilizables
│   │   ├── api/                # Cliente HTTP
│   │   ├── store/              # Estado global (Zustand)
│   │   └── styles/             # Estilos CSS
│   ├── package.json
│   └── Dockerfile               # Docker image frontend
│
├── docs/                        # Documentación
│   ├── SESSION_5_SUMMARY.md    # Resumen Session 5
│   ├── SESSION_6_ROADMAP.md    # Roadmap Session 6
│   ├── ARCHITECTURE.md          # Arquitectura detallada
│   └── DEBUGGING_GUIDE.md       # Guía de debugging
│
├── docker-compose.yml           # Configuración Docker Compose
├── .gitignore                   # Git ignore rules
├── LICENSE.txt                  # MIT License
└── README.md                    # Este archivo
```

---

## 🧪 Testing

### Test Manual de Endpoints

#### 1. Health Check
```bash
curl http://localhost:8080/actuator/health
```

#### 2. Signup con Usuario Válido
```bash
curl -X POST http://localhost:8080/api/v1/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "testuser@example.com",
    "password": "Password123"
  }'
```

#### 3. Intentar Signup con Email Duplicado
```bash
curl -X POST http://localhost:8080/api/v1/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "username": "other",
    "email": "testuser@example.com",
    "password": "Password123"
  }'
# Esperar: 400 Bad Request - "Email already registered"
```

#### 4. Login Exitoso
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "testuser@example.com",
    "password": "Password123"
  }'
```

### Tests Unitarios

```bash
# Backend
cd backend
mvn test

# Frontend
cd frontend
npm test
```

---

## 🐳 Comandos Docker

### Ver Estado
```bash
# Ver contenedores
docker-compose ps

# Ver logs en tiempo real
docker-compose logs -f

# Ver logs de servicio específico
docker-compose logs -f backend
docker-compose logs -f frontend
docker-compose logs -f postgres
```

### Reiniciar Servicios
```bash
# Reiniciar todo
docker-compose restart

# Reiniciar servicio específico
docker-compose restart backend
docker-compose restart frontend
```

### Reconstruir y Levantar
```bash
# Reconstruir sin cache
docker-compose build --no-cache

# Levantar con reconstrucción
docker-compose up -d --build

# Limpieza completa
docker-compose down -v
docker system prune -a -f --volumes
docker-compose up -d --build
```

### Conectarse a la Base de Datos
```bash
docker-compose exec postgres psql -U postgres -d expense_note_app
```

---

## 📊 Documentación Adicional

### Arquitectura
Ver [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md) para diagrama completo, capas, patrones de diseño y flujos de datos.

### Session 5 - Autenticación Implementada
Ver [`docs/SESSION_5_SUMMARY.md`](docs/SESSION_5_SUMMARY.md) para resumen detallado, bugs resueltos, testing realizado.

### Session 6 - Roadmap (Próximas Funcionalidades)
Ver [`docs/SESSION_6_ROADMAP.md`](docs/SESSION_6_ROADMAP.md) para especificaciones de dashboard, gestión de gastos, y testing.

### Debugging y Troubleshooting
Ver [`docs/DEBUGGING_GUIDE.md`](docs/DEBUGGING_GUIDE.md) para errores comunes, logs, y soluciones.

---

## 🔄 Gestión de Versiones

### Versión Actual
- **Versión:** 1.0.0-alpha
- **Estado:** Development
- **Última actualización:** Session 5 (27 de Noviembre de 2025)

### Cambios Session 5
- ✅ Autenticación completa (Signup + Login)
- ✅ JWT token generation
- ✅ Role-based access control setup
- ✅ 7 usuarios de test creados
- ✅ Bug fixes en backend
- ✅ Frontend integrado

### Roadmap
- 📋 Session 6: Dashboard + Gestión de Gastos
- 📋 Session 7: Reportes y Estadísticas
- 📋 Session 8: Gestión de Equipo (Managers)
- 📋 Session 9: Panel de Administración
- 📋 Session 10: Deployment y Producción

---

## 🛠️ Troubleshooting

### Problema: Contenedores no inician
```bash
# Limpiar y reintentar
docker-compose down -v
docker system prune -a -f --volumes
docker-compose up -d --build
```

### Problema: Errores de conexión a BD
```bash
# Verificar que postgres está healthy
docker-compose logs postgres

# Conectarse directamente
docker-compose exec postgres psql -U postgres
```

### Problema: Frontend muestra error CORS
```bash
# Verificar que backend tiene CORS configurado
# Reiniciar backend
docker-compose restart backend
```

### Problema: JWT inválido / 401 Unauthorized
```bash
# Verificar token en https://jwt.io
# Hacer login nuevamente para obtener token fresco
# Verificar que Authorization header está correcto
```

Ver [`docs/DEBUGGING_GUIDE.md`](docs/DEBUGGING_GUIDE.md) para más soluciones.

---

## 📞 Soporte y Contribución

### Reportar Issues
1. Abrir GitHub issue con detalles:
   - Descripción del problema
   - Pasos para reproducir
   - Stack trace si aplica
   - Logs relevantes

### Contribuciones
1. Fork del repositorio
2. Crear rama feature (`git checkout -b feature/amazing-feature`)
3. Commit cambios (`git commit -m 'Add amazing feature'`)
4. Push a rama (`git push origin feature/amazing-feature`)
5. Abrir Pull Request

---

## 📄 Licencia

Este proyecto está bajo licencia MIT. Ver [`LICENSE.txt`](LICENSE.txt) para detalles.

---

## 👨‍💻 Autor

**Dev Team**  
Session 5 - Autenticación y Login  
Actualizado: 27 de Noviembre de 2025

---

## 🎯 Próximos Pasos

1. **Leer documentación:**
   - [`ARCHITECTURE.md`](docs/ARCHITECTURE.md) - Entender la arquitectura
   - [`SESSION_5_SUMMARY.md`](docs/SESSION_5_SUMMARY.md) - Saber qué se implementó

2. **Testing:**
   - Probar signup y login
   - Explorar endpoints con postman/curl

3. **Desarrollo Session 6:**
   - Implementar dashboard
   - Crear endpoints CRUD para gastos
   - Ver [`SESSION_6_ROADMAP.md`](docs/SESSION_6_ROADMAP.md)

---

## 📞 Contacto

Para preguntas o soporte, crear issue en el repositorio.

```
┌─────────────────────────────────────────┐
│  ExpenseNoteApp - Versión 1.0.0-alpha   │
│  Status: 🟢 Desarrollo Activo            │
│  Last Updated: Session 5 ✅              │
└─────────────────────────────────────────┘
```

