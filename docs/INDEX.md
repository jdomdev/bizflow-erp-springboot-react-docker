# Documentación - ExpenseNoteApp v1.1.0

Bienvenido a la documentación completa del proyecto ExpenseNoteApp. Este proyecto es una aplicación moderna para gestión de gastos empresariales con autenticación JWT, backend Spring Boot 3.3.4 y frontend React.

## 📋 Índice de Documentación

### Guías de Usuario

### 1. **[USER_GUIDE.md](./USER_GUIDE.md)** 👤
Guía completa para usuarios finales del sistema.
- Primeros pasos y login
- Gestión de gastos (crear, editar, eliminar)
- Consulta de nómina
- Configuración de perfil
- Roles y permisos
- Preguntas frecuentes
- Ejemplos prácticos

### Guías Técnicas

### 2. **[QUICK_START.md](./QUICK_START.md)** 🚀
Guía rápida para empezar a usar la aplicación.
- Requisitos previos
- Instalación y configuración
- Ejecución de backend y frontend
- Troubleshooting común

### 3. **[API_REFERENCE.md](./API_REFERENCE.md)** 📡
Documentación completa de la REST API.
- Información general y autenticación
- Endpoints de gastos (CRUD completo)
- Endpoints de empleados
- Endpoints de nómina y puestos
- Endpoints de usuarios y roles
- Códigos de respuesta
- Swagger UI y OpenAPI
- Ejemplos con curl

### 4. **[ARCHITECTURE.md](./ARCHITECTURE.md)** 🏗️
Arquitectura técnica del sistema.
- Visión general y componentes
- Arquitectura de alto nivel
- Capas del backend (MVC)
- Arquitectura del frontend
- Modelo de datos y relaciones
- Seguridad y flujos de datos
- Decisiones de diseño
- Escalabilidad

### 5. **[DEPLOYMENT.md](./DEPLOYMENT.md)** 🚀
Guía completa de despliegue.
- Despliegue local y con Docker
- Despliegue en producción
- Cloud providers (AWS, Azure, GCP, Heroku)
- Configuración de entornos
- Monitoreo y mantenimiento
- Backups y disaster recovery
- Checklist de despliegue

### 6. **[TROUBLESHOOTING.md](./TROUBLESHOOTING.md)** 🔧
Solución de problemas comunes.
- Problemas de instalación
- Problemas de backend y frontend
- Problemas de base de datos
- Problemas de autenticación
- Problemas de Docker
- Errores comunes y soluciones
- Herramientas de diagnóstico

### Documentación de Proyecto

### 7. **[ANALISIS_DETALLADO.md](./ANALISIS_DETALLADO.md)** 🔍
Análisis exhaustivo de los problemas encontrados en el código original.
- Dependencias deprecadas
- Códigos vulnerables
- Issues de seguridad
- Recomendaciones de solución

### 8. **[CAMBIOS_V2.md](./CAMBIOS_V2.md)** ✅
Documentación detallada de todos los cambios realizados.
- Migraciones de dependencias
- Refactorización de código JWT
- Eliminación de code smells
- Aplicación de SOLID principles
- Creación del frontend React

### 9. **[SECURITY.md](./SECURITY.md)** 🔒
Análisis completo de seguridad y vulnerabilidades solucionadas.
- 13 vulnerabilidades identificadas y solucionadas
- Detalles técnicos de cada remediación
- Prácticas de seguridad implementadas
- Checklist pre-producción

### 10. **[LAUNCH_GUIDE.md](./LAUNCH_GUIDE.md)** 🚀
Guía paso a paso para lanzar la aplicación.
- Requisitos previos y verificación
- Configuración de base de datos
- Ejecución de backend y frontend
- Troubleshooting y resolución de problemas

### 11. **[FEATURES_ROADMAP.md](./FEATURES_ROADMAP.md)** 🎯
Características empresariales adicionales para futuras versiones.
- Gestión avanzada de gastos
- Workflows de aprobación multi-nivel
- Gestión presupuestaria
- Reportes y analytics
- Integraciones externas
- Roadmap de implementación

### 12. **[DOCKER.md](./DOCKER.md)** 🐳
Guía completa de Docker, docker-compose y dev-containers.
- Arquitectura de containers
- Dockerfile backend (Java 21)
- Dockerfile frontend (Nginx + React)
- Docker Compose orchestration
- VS Code dev-containers setup
- Comandos útiles y troubleshooting

### 13. **[RELEASE_NOTES_v1.1.0.md](../RELEASE_NOTES_v1.1.0.md)** 📋
Resumen completo del release v1.1.0.
- 8 commits granulares (Angular convention)
- Estadísticas del proyecto
- 13 vulnerabilidades solucionadas
- Cambios principales por commit
- Cómo iniciar con Docker
- Beneficios de v1.1.0

## 🏗️ Estructura del Proyecto

```
ExpenseNoteApp/
├── backend-springboot/         # Backend Spring Boot 3.3.4
│   ├── src/main/java/         # Código fuente Java
│   ├── src/main/resources/    # Configuración
│   └── pom.xml                # Dependencias Maven
├── frontend/                   # Frontend React + Vite
│   ├── src/                   # Componentes y páginas
│   ├── public/                # Assets estáticos
│   └── package.json           # Dependencias NPM
├── docs/                      # Documentación (este archivo)
└── README.md                  # Descripción general del proyecto
```

## 🛠️ Tecnologías Principales

### Backend
- **Java 21** con Spring Boot 3.3.4
- **Spring Framework 6.1.13**
- **Spring Security 6.3.3** con JWT (JJWT 0.12.6)
- **PostgreSQL** para persistencia
- **Maven 3.6+** para build
- **Springdoc OpenAPI 2.3.0** para documentación de API (Swagger)

### Frontend
- **React 18.2.0** con Vite 5.0.0
- **Tailwind CSS 3.4.0** para estilos
- **Framer Motion 10.16.4** para animaciones
- **Zustand 4.4.7** para state management
- **Axios 1.6.0** para API calls

## 🌐 Acceso a la Aplicación

Una vez iniciada la aplicación, puedes acceder a:

### Interfaces de Usuario
- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8080/api/v1

### Documentación API Interactiva
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI Spec**: http://localhost:8080/v3/api-docs

### Credenciales por Defecto
- **Email**: admin@example.com
- **Password**: admin123

## 📊 Versioning

Este proyecto usa **Semantic Versioning (SemVer)**:
- **1.1.0**: Versión actual con nuevas características (frontend React agregado)
- Mayor (v2.0.0): Cambios incompatibles
- Menor (v1.1.0): Nuevas características compatibles
- Patch (v1.0.1): Correcciones de bugs

## 🔒 Seguridad

Todas las dependencias han sido actualizadas a las versiones más seguras:
- ✅ Spring Boot 3.3.4 (últimas actualizaciones de seguridad)
- ✅ Spring Security 6.3.3 (sin vulnerabilidades conocidas)
- ✅ Log4j 2.23.1 (parches CVE aplicados)
- ✅ Jackson 2.17.2 (sin vulnerabilidades conocidas)
- ✅ PostgreSQL 42.7.3 (driver más reciente)
- ✅ SnakeYAML 2.2 (mitigación de deserialización)
- ✅ Commons Lang3 3.14.0
- ✅ Commons IO 2.16.1

## 🚀 Próximos Pasos

1. **[Lee QUICK_START.md](./QUICK_START.md)** para configurar el ambiente
2. **[Revisa CAMBIOS_V2.md](./CAMBIOS_V2.md)** para entender la arquitectura
3. **Inicia el backend**: `cd backend-springboot && mvn spring-boot:run`
4. **Inicia el frontend**: `cd frontend && npm run dev`
5. Accede a http://localhost:3000 en tu navegador

## 📱 Funcionalidades Principales

### Autenticación
- Login y registro de usuarios
- JWT token basado en seguridad
- Roles y permisos (Admin, User)

### Gestión de Gastos
- Crear, leer, actualizar, eliminar gastos
- Filtrar por fecha, empleado, cantidad
- Validación de datos en frontend y backend

### Gestión de Nómina
- Registro de salarios
- Detalles de empleados
- Información de puestos

### Dashboard
- Estadísticas de gastos
- Gastos recientes
- Indicadores clave

## 🐛 Reportar Issues

Si encuentras problemas:
1. Revisa la sección de [troubleshooting en QUICK_START.md](./QUICK_START.md#troubleshooting)
2. Verifica los logs en `backend-springboot/target/logs/`
3. Abre un issue en GitHub con detalles del error

## 📝 Licencia

Este proyecto está bajo la licencia indicada en LICENSE.txt

---

**Última actualización**: Noviembre 2024
**Versión**: 1.1.0
**Mantenido por**: ExpenseNoteApp Team
