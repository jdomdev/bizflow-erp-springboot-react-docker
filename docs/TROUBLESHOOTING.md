# Guía de Solución de Problemas - ExpenseNoteApp v1.1.0

Guía completa para resolver problemas comunes en el sistema de gestión de gastos empresariales.

## 📋 Tabla de Contenidos

1. [Problemas de Instalación](#problemas-de-instalación)
2. [Problemas de Backend](#problemas-de-backend)
3. [Problemas de Frontend](#problemas-de-frontend)
4. [Problemas de Base de Datos](#problemas-de-base-de-datos)
5. [Problemas de Autenticación](#problemas-de-autenticación)
6. [Problemas de Docker](#problemas-de-docker)
7. [Problemas de Rendimiento](#problemas-de-rendimiento)
8. [Errores Comunes](#errores-comunes)

---

## 🔧 Problemas de Instalación

### Maven no encuentra dependencias

**Síntoma**: Error al ejecutar `mvn clean install`

**Solución**:
```bash
# Limpiar cache de Maven
mvn clean
rm -rf ~/.m2/repository

# Volver a descargar dependencias
mvn clean install -U

# Si persiste, verificar configuración de proxy en settings.xml
```

**Ubicación de settings.xml**: `~/.m2/settings.xml`

---

### Node modules no se instalan

**Síntoma**: Error al ejecutar `npm install`

**Solución**:
```bash
# Limpiar cache de npm
npm cache clean --force

# Eliminar node_modules y package-lock.json
rm -rf node_modules package-lock.json

# Reinstalar
npm install

# Si persiste, usar yarn
npm install -g yarn
yarn install
```

---

### Java versión incorrecta

**Síntoma**: Error "java.lang.UnsupportedClassVersionError"

**Solución**:
```bash
# Verificar versión de Java (debe ser 21+)
java -version

# Si es incorrecta, instalar Java 21
# Ubuntu/Debian:
sudo apt-get install openjdk-21-jdk

# macOS (con Homebrew):
brew install openjdk@21

# Configurar JAVA_HOME
export JAVA_HOME=/path/to/java21
export PATH=$JAVA_HOME/bin:$PATH
```

---

## 🔴 Problemas de Backend

### El backend no inicia

**Síntoma**: Error al ejecutar `mvn spring-boot:run`

**Verificaciones**:

1. **Puerto 8080 ocupado**:
```bash
# Ver qué proceso usa el puerto 8080
lsof -i :8080
# o en Windows:
netstat -ano | findstr :8080

# Matar el proceso
kill -9 <PID>
```

2. **Base de datos no conecta**:
```bash
# Verificar que PostgreSQL esté corriendo
sudo systemctl status postgresql
# o en macOS:
brew services list

# Verificar conexión
psql -U postgres -d expense_note_app
```

3. **Variables de entorno faltantes**:
```properties
# Verificar application.properties
spring.datasource.url=jdbc:postgresql://localhost:5432/expense_note_app
spring.datasource.username=postgres
spring.datasource.password=tu_password
app.jwt.secret=tu-clave-secreta-de-al-menos-32-caracteres
```

---

### Error "Table not found"

**Síntoma**: `org.h2.jdbc.JdbcSQLException: Table "EXPENSE" not found`

**Solución**:
```properties
# En application.properties, verificar:
spring.jpa.hibernate.ddl-auto=update
# o para desarrollo:
spring.jpa.hibernate.ddl-auto=create-drop

# Reiniciar aplicación para recrear tablas
```

**Alternativa manual**:
```sql
-- ⚠️ ADVERTENCIA: Esto eliminará TODOS los datos. Solo para desarrollo.
-- ⚠️ En producción, SIEMPRE haz backup primero.

-- Conectar a la base de datos
psql -U postgres -d expense_note_app

-- Verificar tablas
\dt

-- Si no existen, salir de psql
\q

-- SOLO EN DESARROLLO: Eliminar y recrear BD
-- ⚠️ ESTO BORRARÁ TODOS LOS DATOS
psql -U postgres
DROP DATABASE expense_note_app;
CREATE DATABASE expense_note_app;
\q
```

---

### Error "JWT parsing failed"

**Síntoma**: `io.jsonwebtoken.SignatureException: JWT signature does not match`

**Causa**: La clave secreta JWT cambió o es diferente entre instancias

**Solución**:
```properties
# application.properties
# Asegurarse que la clave sea la misma en todos los ambientes
app.jwt.secret=la-misma-clave-en-todos-lados-minimo-32-caracteres
app.jwt.expiration=86400000

# Reiniciar backend
```

**Nota**: Si cambias la clave, todos los tokens existentes se invalidarán.

---

### Error 401 Unauthorized en endpoints protegidos

**Síntoma**: Endpoints retornan 401 aunque estés autenticado

**Solución**:
```bash
# 1. Verificar que el token no haya expirado
# 2. Verificar que el header Authorization esté correcto:
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...

# 3. Verificar que el usuario tenga el rol correcto
# En el endpoint, ver @PreAuthorize("hasRole('ROLE_ADMIN')")

# 4. Probar con curl:
curl -X GET http://localhost:8080/api/v1/expense/ \
  -H "Authorization: Bearer TU_TOKEN_AQUI"
```

---

## 🎨 Problemas de Frontend

### Frontend no se conecta con backend

**Síntoma**: Error "Network Error" o "ERR_CONNECTION_REFUSED"

**Solución**:

1. **Verificar que backend esté corriendo**:
```bash
# Verificar puerto 8080
curl http://localhost:8080/api/v1/auth/login
```

2. **Verificar variable de entorno**:
```bash
# En frontend/.env
VITE_API_URL=http://localhost:8080/api/v1
```

3. **Verificar CORS en backend**:
```java
// En controllers, debe tener:
@CrossOrigin(origins = "*")
// o configurar globalmente en SecurityConfig
```

4. **Verificar Axios config**:
```javascript
// src/services/api.js
const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api/v1';
```

---

### Página en blanco al cargar

**Síntoma**: Frontend carga pero muestra página en blanco

**Solución**:

1. **Abrir consola del navegador** (F12):
```
Buscar errores en la consola de JavaScript
```

2. **Verificar build**:
```bash
cd frontend
rm -rf node_modules dist
npm install
npm run dev
```

3. **Verificar rutas**:
```javascript
// En App.jsx, verificar que las rutas estén correctas
<Route path="/" element={<LoginPage />} />
<Route path="/dashboard" element={<DashboardPage />} />
```

---

### Estilos no se aplican

**Síntoma**: La UI se ve sin estilos o rota

**Solución**:
```bash
# Limpiar cache de Vite
rm -rf node_modules/.vite

# Verificar que Tailwind esté configurado
# tailwind.config.js debe tener:
content: [
  "./index.html",
  "./src/**/*.{js,ts,jsx,tsx}",
],

# Reiniciar servidor de desarrollo
npm run dev
```

---

### Error "Cannot read property of undefined"

**Síntoma**: Error en consola al navegar

**Solución**:
```javascript
// Usar optional chaining y valores por defecto
const userName = user?.name ?? 'Unknown';

// Verificar que los datos del API sean válidos
useEffect(() => {
  if (!data) return; // Guard clause
  // usar data
}, [data]);
```

---

## 💾 Problemas de Base de Datos

### PostgreSQL no inicia

**Síntoma**: "could not connect to server"

**Solución**:

**Ubuntu/Debian**:
```bash
sudo systemctl start postgresql
sudo systemctl enable postgresql
sudo systemctl status postgresql
```

**macOS**:
```bash
brew services start postgresql
brew services list
```

**Windows**:
```cmd
# Desde Servicios (services.msc)
# Buscar "postgresql" e iniciar el servicio
```

---

### Base de datos no existe

**Síntoma**: `FATAL: database "expense_note_app" does not exist`

**Solución**:
```bash
# Conectar como postgres
psql -U postgres

# Crear base de datos
CREATE DATABASE expense_note_app;

# Verificar
\l

# Salir
\q

# Conectar a la nueva BD
psql -U postgres -d expense_note_app
```

---

### Error de permisos en PostgreSQL

**Síntoma**: `FATAL: role "tu_usuario" does not exist`

**Solución**:
```sql
-- Conectar como superusuario
psql -U postgres

-- Crear usuario
CREATE USER tu_usuario WITH PASSWORD 'tu_password';

-- Dar permisos
GRANT ALL PRIVILEGES ON DATABASE expense_note_app TO tu_usuario;

-- Verificar
\du
```

---

### Migración de datos falló

**Síntoma**: Datos inconsistentes o tablas corruptas

**Solución**:
```bash
# Backup de datos existentes
pg_dump -U postgres expense_note_app > backup.sql

# Eliminar y recrear BD
psql -U postgres
DROP DATABASE expense_note_app;
CREATE DATABASE expense_note_app;
\q

# Reiniciar backend con ddl-auto=create
# Luego restaurar datos si es necesario
psql -U postgres expense_note_app < backup.sql
```

---

## 🔐 Problemas de Autenticación

### No puedo iniciar sesión

**Síntoma**: Login siempre retorna error

**Verificaciones**:

1. **Credenciales correctas**:
```bash
# Usuario por defecto:
Email: admin@example.com
Password: admin123
```

2. **Backend procesa login**:
```bash
# Verificar logs del backend
tail -f backend-springboot/logs/application.log

# Buscar errores en:
# - Conexión a BD
# - Generación de JWT
# - Encriptación de contraseña
```

3. **Probar endpoint directamente**:
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@example.com","password":"admin123"}'
```

---

### Token expiró muy rápido

**Síntoma**: Session termina después de pocos minutos

**Solución**:
```properties
# application.properties
# Aumentar tiempo de expiración (en milisegundos)
app.jwt.expiration=86400000  # 24 horas
# o
app.jwt.expiration=3600000   # 1 hora
```

---

### "Access Denied" con rol correcto

**Síntoma**: Usuario tiene el rol pero no puede acceder

**Solución**:
```java
// Verificar anotación en controller:
@PreAuthorize("hasRole('ROLE_ADMIN')")
// vs
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")

// Verificar que el rol en BD tenga prefijo ROLE_
// Correcto: ROLE_ADMIN
// Incorrecto: ADMIN
```

---

## 🐳 Problemas de Docker

### Docker Compose no inicia

**Síntoma**: Error al ejecutar `docker-compose up`

**Solución**:
```bash
# Verificar Docker está corriendo
docker ps

# Limpiar containers antiguos
docker-compose down -v

# Reconstruir imágenes
docker-compose build --no-cache

# Iniciar de nuevo
docker-compose up -d
```

---

### Container backend no se conecta a PostgreSQL

**Síntoma**: Backend muestra error de conexión en Docker

**Solución**:
```yaml
# En docker-compose.yml, verificar:
services:
  backend:
    environment:
      - SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/expense_note_app
      # Usar nombre del servicio 'postgres', no 'localhost'
    depends_on:
      - postgres

  postgres:
    # ...
```

---

### Volumen de PostgreSQL corrupto

**Síntoma**: Base de datos no mantiene cambios

**Solución**:
```bash
# Detener containers
docker-compose down

# Eliminar volúmenes
docker-compose down -v

# Eliminar volumen específico
docker volume ls
docker volume rm <volume_name>

# Recrear todo
docker-compose up -d
```

---

### Frontend no encuentra variables de entorno

**Síntoma**: API_URL es undefined en Docker

**Solución**:
```dockerfile
# En frontend/Dockerfile
# Las variables VITE_ deben definirse en build time:
ARG VITE_API_URL
ENV VITE_API_URL=$VITE_API_URL

# O en docker-compose.yml:
services:
  frontend:
    build:
      args:
        VITE_API_URL: http://localhost:8080/api/v1
```

---

## ⚡ Problemas de Rendimiento

### Backend responde lento

**Verificaciones**:

1. **Conexiones de BD**:
```properties
# application.properties
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
```

2. **Logging excesivo**:
```properties
# Reducir nivel de log en producción
logging.level.root=WARN
logging.level.io.sunbit.app=INFO
```

3. **Queries N+1**:
```java
// Usar fetch joins en queries
@Query("SELECT e FROM Expense e JOIN FETCH e.employee WHERE e.id = :id")
```

---

### Frontend carga lento

**Solución**:
```bash
# Build optimizado para producción
npm run build

# Analizar bundle size
npm install -g vite-bundle-visualizer
vite-bundle-visualizer

# Lazy loading de rutas
const DashboardPage = lazy(() => import('./pages/DashboardPage'));
```

---

## ❌ Errores Comunes

### Error: "Port already in use"

**Solución**:
```bash
# Backend (puerto 8080)
lsof -ti:8080 | xargs kill -9

# Frontend (puerto 3000)
lsof -ti:3000 | xargs kill -9

# O cambiar puerto en configuración
```

---

### Error: "CORS policy blocked"

**Solución**:
```java
// En SecurityConfig.java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.addAllowedOrigin("http://localhost:3000");
    configuration.addAllowedMethod("*");
    configuration.addAllowedHeader("*");
    configuration.setAllowCredentials(true);
    
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
}
```

---

### Error: "Cannot find module"

**Solución**:
```bash
# Backend
mvn clean install

# Frontend
rm -rf node_modules
npm install
```

---

### Error: "OutOfMemoryError"

**Solución**:
```bash
# Aumentar memoria de Maven
export MAVEN_OPTS="-Xmx2048m -XX:MaxPermSize=512m"

# O en Maven command
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xmx2048m"
```

---

## 🔍 Herramientas de Diagnóstico

### Verificar salud del sistema

```bash
# Backend
curl http://localhost:8080/actuator/health

# Ver métricas
curl http://localhost:8080/actuator/metrics

# Ver info
curl http://localhost:8080/actuator/info
```

### Logs útiles

```bash
# Backend logs
tail -f backend-springboot/logs/application.log

# Docker logs
docker-compose logs -f backend
docker-compose logs -f frontend
docker-compose logs -f postgres

# PostgreSQL logs (Ubuntu)
tail -f /var/log/postgresql/postgresql-*.log
```

### Herramientas recomendadas

- **Postman**: Probar endpoints API
- **pgAdmin**: Administrar PostgreSQL
- **Chrome DevTools**: Debuggear frontend
- **IntelliJ IDEA**: IDE para backend
- **VS Code**: Editor para frontend

---

## 📞 Obtener Ayuda

Si ninguna solución funciona:

1. **Revisar documentación**:
   - [INDEX.md](./INDEX.md)
   - [QUICK_START.md](./QUICK_START.md)
   - [API_REFERENCE.md](./API_REFERENCE.md)

2. **Recopilar información**:
   - Logs del backend
   - Logs del frontend (consola del navegador)
   - Versión de Java, Node, PostgreSQL
   - Sistema operativo

3. **Crear un issue en GitHub**:
   - Incluir toda la información recopilada
   - Pasos para reproducir el error
   - Comportamiento esperado vs. actual

4. **Contactar soporte**:
   - Email: support@expensenoteapp.com
   - Incluir logs y capturas de pantalla

---

## ✅ Checklist de Verificación

Antes de reportar un problema, verifica:

- [ ] Backend está corriendo en puerto 8080
- [ ] Frontend está corriendo en puerto 3000
- [ ] PostgreSQL está corriendo y accesible
- [ ] Base de datos existe y tiene datos
- [ ] Variables de entorno están configuradas
- [ ] Dependencias están instaladas
- [ ] Logs no muestran errores obvios
- [ ] Reiniciaste los servicios
- [ ] Probaste en un navegador diferente (frontend)
- [ ] Limpiaste cache y node_modules/target

---

**Última actualización**: Diciembre 2024  
**Versión**: 1.0.0  
**Mantenido por**: ExpenseNoteApp Team
