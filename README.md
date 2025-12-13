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

## 🌱 Configuración multi-entorno y perfiles Spring Boot

La aplicación soporta perfiles para múltiples entornos (`dev`, `test`, `prod`) usando archivos de configuración dedicados:

- `application-dev.properties`
- `application-test.properties`
- `application-prod.properties`

Puedes activar un perfil de entorno de las siguientes formas:

1. **Variable de entorno** (recomendado):
  ```bash
  export SPRING_PROFILES_ACTIVE=dev
  export SPRING_PROFILES_ACTIVE=prod
  export SPRING_PROFILES_ACTIVE=test
  ```
2. **Argumento JVM**:
  ```bash
  java -jar app.jar --spring.profiles.active=dev
  ```
3. **En los tests**: Usa `@ActiveProfiles("test")` en las clases de test para forzar el uso de la configuración de test.

Consulta la guía completa en [`docs/SPRING_PROFILES_GUIDE.md`](./docs/SPRING_PROFILES_GUIDE.md).

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
- **Contraseña**: admin123

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

## 🔒 Seguridad avanzada: uso de spring-security-crypto

La dependencia `spring-security-crypto` se utiliza **exclusivamente para el hash seguro de contraseñas de usuario** mediante `BCryptPasswordEncoder`. No se emplean los componentes de cifrado simétrico (como `AesBytesEncryptor` o `TextEncryptor`) que pueden estar afectados por vulnerabilidades reportadas en la librería.

### Auditoría de uso
- Se ha revisado todo el código fuente y **solo se utiliza `BCryptPasswordEncoder`** para el registro y autenticación de usuarios.
- No se usan cifradores simétricos ni funciones de encriptación de datos sensibles de la librería.

### Mitigación de vulnerabilidad
- Las vulnerabilidades reportadas para `spring-security-crypto` 6.x afectan principalmente a los cifradores simétricos, no a `BCryptPasswordEncoder`.
- El hash de contraseñas con BCrypt sigue siendo seguro y recomendado por la comunidad de seguridad.
- Se recomienda mantener la dependencia actualizada y monitorizar los avisos de seguridad oficiales.

### Recomendaciones
- **No almacenar datos sensibles usando cifrado simétrico de esta librería.**
- **Usar únicamente `BCryptPasswordEncoder` para contraseñas.**
- Documentar este uso en el README y en auditorías de seguridad.

Más detalles en [SECURITY_SPRING_CRYPTO.md](./SECURITY_SPRING_CRYPTO.md).

---

## Future Enhancements

- **Migration to Spring Boot 3**: Planned update to leverage new features and optimizations.
- **Frontend Integration**: Upcoming features include a frontend interface for easier interaction.
- **Extended Testing**: Adding JUnit tests to enhance API stability and reliability.

## Contributing

Feel free to submit issues and pull requests. For major changes, please discuss them via issues or email with the maintainers.

## License

This project is licensed under the GNU General Public License v3.0. See the [LICENSE](./LICENSE) file for more details.

---

## Revisión de nombres de contenedores y referencias en docker-compose.yml y archivos clave

- **erp-db-container** (PostgreSQL)
- **erp-backend-container** (Spring Boot backend)
- **erp-frontend-container** (React frontend)
- **bizflowerp_pgadmin** (pgAdmin)

Variables de entorno y dependencias también usan estos nombres:

- Backend conecta a DB con `jdbc:postgresql://erp-db-container:5432/erp_db`
- Frontend usa `VITE_API_URL: http://erp-backend-container:8080`

## 🗄️ Backups y Restauración de Base de Datos

- **No se versionan backups en el repositorio.** Los archivos de backup deben almacenarse fuera de Git, en sistemas seguros y externos.
- Antes de operaciones críticas (limpieza de volúmenes, migraciones, actualizaciones), realiza un backup manual:
  ```bash
  docker exec erp-db-container pg_dump -U postgres -d erp_db > backups/erpdb_backup_YYYYMMDD_HHMMSS.sql
  ```
- Para restaurar la base de datos:
  ```bash
  cat backups/erpdb_backup_YYYYMMDD_HHMMSS.sql | docker exec -i erp-db-container psql -U postgres -d erp_db
  ```
- Consulta el resumen de backups y estado de la base de datos en [`docs/DB_BACKUP_SUMMARY_20251209.md`](./docs/DB_BACKUP_SUMMARY_20251209.md).

**Importante:** Mantén los backups fuera del repositorio y realiza copias frecuentes antes de cualquier cambio mayor.

---

## 📝 Session 6 Highlights (2025-12-10)

- Deduplicated payroll table in PostgreSQL, ensuring only one record per employee and payroll date.
- Created and verified database backups with datetime stamps using Docker and pg_dump.
- Verified record counts for all main tables to ensure data integrity.
- Committed granular changes to expenses_sample.sql, adding 100 new invented expenses and switching to expense_user_id references.
- All commands and guides for Docker and database management are available in `docs/docker_commands_session_6.md`.

### Key Docker Commands Used

```bash
# List all tables
$ docker exec -it erp-db-container psql -U postgres -d erp_db -c "\dt"

# List all payroll records
$ docker exec -it erp-db-container psql -U postgres -d erp_db -c "SELECT * FROM payroll ORDER BY employee_id, payroll_date;"

# Remove duplicates from payroll
$ docker exec -it erp-db-container psql -U postgres -d erp_db -c "DELETE FROM payroll WHERE id IN (SELECT id FROM (SELECT id, ROW_NUMBER() OVER (PARTITION BY employee_id, payroll_date ORDER BY id DESC) AS rn FROM payroll) t WHERE t.rn > 1);"

# Create a backup with datetime stamp
$ docker exec -t erp-db-container pg_dump -U postgres -d erp_db > backups/erpdb_backup_$(date +%Y_%m_%d_%H%M%S).sql
```

For a full summary and all commands, see:
- `docs/session_6/session_6_summary_2025_12_10.md`
- `docs/docker_commands_session_6.md`

---
