
# BIZFLOW ERP

**Bizflow ERP v1.1.0** - Una aplicación moderna para gestión de gastos empresariales con backend Spring Boot 3.3.4 y frontend React 18.

## 🎯 Descripción General


Bizflow ERP es una solución completa para la administración de gastos corporativos que permite a los empleados gestionar sus reportes de gastos y nómina. Combina un backend robusto en Spring Boot 3 con un frontend moderno en React, proporcionando una experiencia de usuario profesional y segura.


**Nota**: Esta versión (1.1.0) incluye una actualización importante desde Spring Boot 2.7.18 → 3.3.4, con todas las dependencias actualizadas a las versiones más seguras. El backend utiliza **Java 17 LTS** (no Java 21).

## 📚 Documentación

Para una guía completa, consulta los documentos en la carpeta `/docs`:
- **[📖 INDEX.md](./docs/INDEX.md)** - Índice de documentación completo
- **[🚀 QUICK_START.md](./docs/QUICK_START.md)** - Guía rápida de inicio
- **[🔍 ANALISIS_DETALLADO.md](./docs/ANALISIS_DETALLADO.md)** - Problemas encontrados y soluciones
- **[✅ CAMBIOS_V2.md](./docs/CAMBIOS_V2.md)** - Documentación detallada de cambios

## ✨ Características Principales

### Autenticación & Seguridad
- ✅ Autenticación JWT moderna (JJWT 0.12.6)
- ✅ Control de acceso basado en roles (RBAC)
- ✅ Cifrado de contraseñas con Spring Security
- ✅ Todas las dependencias sin vulnerabilidades conocidas

### Gestión de Gastos
- ✅ CRUD completo de gastos
- ✅ Validación en frontend y backend
- ✅ Filtros avanzados
- ✅ Dashboard con estadísticas

### Gestión de Personal
- ✅ Registro de empleados
- ✅ Información de nómina
- ✅ Puestos y departamentos
- ✅ Gestión de roles

### Interfaz Moderna
- ✅ React 18 con Vite (ultra-rápido)
- ✅ Tailwind CSS para diseño responsivo
- ✅ Framer Motion para animaciones suaves
- ✅ Zustand para state management eficiente

## 🛠️ Stack Tecnológico

| Componente | Tecnología | Versión |
|-----------|-----------|---------|
| **Lenguaje** | Java | 17 |
| **Framework Backend** | Spring Boot | 3.3.4 |
| **Framework Frontend** | React | 18.2.0 |
| **Build Frontend** | Vite | 5.0.0 |
| **Estilos** | Tailwind CSS | 3.4.0 |
| **Base de Datos** | PostgreSQL | Latest |
| **Autenticación** | JWT (JJWT) | 0.12.6 |
| **Build Backend** | Maven | 3.6+ |

## 🚀 Inicio Rápido

### Requisitos
- Java 17+
- Node.js 18+
- PostgreSQL 12+
- Maven 3.6+

### Verificar Instalación

```bash
# Verificar versiones instaladas
java -version
javac -version
node --version
npm --version
mvn --version
psql --version
```

### Instalación

```bash
# 1. Clonar repositorio
git clone https://github.com/yourusername/BizflowERP.git
cd BizflowERP

# 2. Configurar base de datos
createdb expense_note_app
# Editar backend/src/main/resources/application.properties

# 3. Iniciar backend
cd backend
mvn spring-boot:run
# Backend disponible en http://localhost:8080

# 4. En otra terminal, iniciar frontend
cd frontend
npm install
npm run dev
# Frontend disponible en http://localhost:3000
```

### 🐳 Ejecución con Docker

Para ejecutar la aplicación completa con Docker (recomendado para desarrollo y producción):

```bash
# Desde la raíz del proyecto
docker-compose up -d

# Ver logs
docker-compose logs -f

# Detener
docker-compose down

# Detener y limpiar (elimina todo excepto datos persistentes)
docker-compose down --volumes
```

**📌 Importante sobre Persistencia de Datos:**
- La base de datos PostgreSQL usa un **volumen persistente** (`postgres_data`)
- Los datos se mantienen incluso si los contenedores se detienen/reinician
- Para eliminar completamente los datos: `docker volume rm postgres_data`
- Las credenciales de prueba (usuarios creados) persisten después de reinicios

### Credenciales de Prueba
- **Email**: admin@example.com
- **Contraseña**: <PASSWORD>

## 📊 Versioning

Este proyecto usa **Semantic Versioning (SemVer)**:
- **v1.1.0** (actual): Frontend React + Security updates
- **v1.0.0**: Backend Spring Boot 3 migration
- **v2.0.0**: Próximas grandes características

Ver [CAMBIOS_V2.md](./docs/CAMBIOS_V2.md) para historial completo.

## 🔒 Seguridad

✅ **Todas las dependencias están actualizadas** a versiones sin vulnerabilidades:
- Spring Boot 3.3.4 con actualizaciones de seguridad más recientes
- Spring Security 6.3.3
- Log4j 2.23.1 con parches CVE
- Jackson 2.17.2
- Y más...

Ver [ANALISIS_DETALLADO.md](./docs/ANALISIS_DETALLADO.md) para detalles completos.

## Authentication and Authorization

The application uses **JWT** for secure authentication. Access is role-based, with the following roles:
- `ROLE_ADMIN`: Full access to system resources and management capabilities.
- `ROLE_USER`: Limited access, with permissions scoped to their own data.

## Future Enhancements

- **Migration to Spring Boot 3**: Planned update to leverage new features and optimizations.
- **Frontend Integration**: Upcoming features include a frontend interface for easier interaction.
- **Extended Testing**: Adding JUnit tests to enhance API stability and reliability.

## Contributing

Feel free to submit issues and pull requests. For major changes, please discuss them via issues or email with the maintainers.

## License

This project is licensed under the GNU General Public License v3.0. See the [LICENSE](./LICENSE) file for more details.
With this update, it is specified that the application is under the **GPLv3** license. This informs contributors and users that they can modify and redistribute the software under the terms of this license.
