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

## 🌱 Arquitectura Multi-Entorno

### Cambio de Paradigma: De Un Entorno a Tres

**Antes (Single Environment):**
- Una sola base de datos compartida
- Configuración única para todos los casos de uso
- Riesgo de contaminar datos de producción durante desarrollo/testing
- Imposibilidad de ejecutar tests en paralelo con desarrollo

**Ahora (Multi-Environment):**
- **Tres entornos completamente aislados** con bases de datos dedicadas
- **Desarrollo (dev):** Puerto 5433, configuración relajada para desarrollo rápido
- **Testing (test):** Puerto 5434, ideal para tests de integración y CI/CD
- **Producción (prod):** Puerto 5442, optimizado para rendimiento y seguridad

### ¿Por qué Multi-Entorno?

1. **🔒 Aislamiento Total:** Los cambios en desarrollo no afectan producción
2. **🧪 Testing Seguro:** Ejecuta tests destructivos sin miedo a perder datos
3. **⚡ Desarrollo Paralelo:** Múltiples desarrolladores trabajando simultáneamente
4. **🎯 Configuración Específica:** Pools de conexiones, logging, timeouts personalizados por entorno
5. **🚀 Migración de Esquemas:** Prueba cambios de base de datos en test antes de producción
6. **📊 Datos Realistas:** Test con datos similares a producción sin riesgos

### Configuración de Perfiles Spring Boot

Cada entorno usa su propio perfil con configuración dedicada:

| Perfil | Archivo | Base de Datos | Puerto DB | URL Backend |
|--------|---------|---------------|-----------|-------------|
| `dev` | `application-dev.properties` | `erp_dev_db` | 5433 | localhost:8080 |
| `test` | `application-test.properties` | `erp_test_db` | 5434 | localhost:8282 |
| `prod` | `application-prod.properties` | `erp_prod_db` | 5442 | localhost:8181 |

### Activación de Perfiles

#### 1. Variable de Entorno (Recomendado para Desarrollo)
```bash
# Desarrollo
export SPRING_PROFILES_ACTIVE=dev
./mvnw spring-boot:run

# Producción
export SPRING_PROFILES_ACTIVE=prod
java -jar target/bizflowerp-1.1.0.jar
```

#### 2. Argumento JVM (Para Despliegue)
```bash
java -jar app.jar --spring.profiles.active=prod
```

#### 3. En Tests (Automático)
```java
@ActiveProfiles("test")
public class EmployeeTest {
    // Test usa automáticamente erp_test_db
}
```

### Docker Compose con Perfiles

Cada entorno tiene su propio perfil de Docker:

```bash
# Iniciar desarrollo
docker compose --profile dev up -d

# Iniciar testing
docker compose --profile test up -d

# Iniciar producción
docker compose --profile prod up -d

# Múltiples entornos simultáneos
docker compose --profile dev --profile prod up -d
```

### Características Nuevas del Multi-Entorno

#### ✅ Inicialización Automática de Datos
- Scripts SQL ejecutados automáticamente al crear contenedores
- Passwords pre-encriptadas con bcrypt ($2a$ format)
- Datos de ejemplo (empleados, posiciones, nóminas) cargados por defecto
- Servicios efímeros `seed-expense-users-*` en los perfiles dev, test y prod que consumen [scripts/register_users.sh](scripts/register_users.sh) si está disponible el archivo de semillas

#### 📁 Seeds de usuarios de gastos
- Crea la carpeta [scripts/secrets](scripts/secrets) (gitignored) y prepara el archivo [scripts/secrets/register_users_payloads.jsonl](scripts/secrets/register_users_payloads.jsonl) con una línea JSON por usuario. Ejemplo:

```jsonl
{"email":"analista.gastos@example.com","name":"Analista","surname":"Gastos","password":"<CONTRASENA>","employee_id":7}
```
- Define la variable REGISTER_USERS_SEED_FILE en tus archivos .env* apuntando a /workspace/scripts/secrets/register_users_payloads.jsonl. Esa ruta coincide con el volumen montado por Docker; al ejecutar el script directamente en tu máquina puedes omitir la variable y el script tomará [scripts/secrets/register_users_payloads.jsonl](scripts/secrets/register_users_payloads.jsonl) automáticamente.
- Si el archivo no existe, los contenedores seed-expense-users-* continuarán sin bloquear el arranque pero dejarán un aviso. Añade o actualiza el archivo cuando quieras poblar los usuarios de gastos.

#### ✅ Healthchecks Integrados
- Cada base de datos tiene healthcheck (`pg_isready`)
- Backends esperan a que DB esté lista antes de iniciar
- Reinicio automático en caso de fallo

#### ✅ Volúmenes Persistentes por Entorno
- `postgres_dev_data` - Datos de desarrollo
- `postgres_test_data` - Datos de testing (puede limpiarse frecuentemente)
- `postgres_prod_data` - Datos de producción (persistencia crítica)

#### ✅ Red Docker Aislada
- Todos los servicios en `bizflow_erp_network`
- Comunicación interna por nombre de servicio
- Puertos expuestos solo a localhost

### Migración desde Configuración Anterior

Si vienes de la configuración anterior con un solo entorno:

1. **Limpia volúmenes antiguos:**
   ```bash
   docker compose down -v
   ```

2. **Selecciona tu entorno:**
   ```bash
   docker compose --profile prod up -d
   ```

3. **Verifica la conexión:**
   ```bash
   docker compose --profile prod ps
   docker compose --profile prod logs backend-prod
   ```

### Guías Completas

- **📘 Guía de Perfiles Spring Boot:** [`docs/spring/SPRING_PROFILES_GUIDE.md`](./docs/spring/SPRING_PROFILES_GUIDE.md)
- **🔄 Guía de Cambio de Entornos:** [`docs/guia_cambio_entornos.md`](./docs/guia_cambio_entornos.md)
- **🐳 Comandos Docker:** [`docs/docker/docker_commands_session_6.md`](./docs/docker/docker_commands_session_6.md)
- **🧪 Testing con Docker:** [`docs/docker/README_TESTS_DOCKER.md`](./docs/docker/README_TESTS_DOCKER.md)

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
---
