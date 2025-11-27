# 🗄️ Guía de Inicialización de Base de Datos

**Documento:** Database Initialization Guide  
**Fecha:** 27 Noviembre 2025  
**Objetivo:** Poblar la BD con datos iniciales para desarrollo y testing

---

## 📋 Resumen de Cambios Implementados

Se han creado dos soluciones complementarias para inicializar datos en la base de datos:

### 1️⃣ SQL Script (`init-data.sql`)
- **Ubicación:** `backend-springboot/src/main/resources/db/init-data.sql`
- **Propósito:** Insertar datos iniciales directamente en PostgreSQL
- **Contenido:**
  - 3 roles por defecto: `ADMIN`, `USER`, `MANAGER`
  - 1 usuario administrador: `admin` / `admin123`
  - 3 posiciones de ejemplo para testing

### 2️⃣ DataLoader Bean (`DataLoader.java`)
- **Ubicación:** `backend-springboot/src/main/java/io/sunbit/app/config/DataLoader.java`
- **Propósito:** Ejecutar automáticamente al iniciar la aplicación
- **Ventajas:**
  - ✅ Evita duplicados (verifica primero)
  - ✅ Usa `PasswordEncoder` para hashear contraseñas
  - ✅ No requiere SQL manual
  - ✅ Se ejecuta una sola vez
  - ✅ Logs detallados para debugging

---

## 🔐 Datos de Acceso Iniciales

### Usuario Administrador Automático
```
Username: admin
Password: admin123
Email:    admin@expenseapp.com
Roles:    ADMIN
```

### Roles Disponibles
```
1. ADMIN    - Acceso total a todas las funciones
2. USER     - Acceso limitado a funciones estándar
3. MANAGER  - Acceso a gestión de equipo
```

---

## 🚀 Cómo Funciona

### Al Iniciar la Aplicación

```
1. Spring Boot arranca
2. Hibernate crea tablas (si no existen)
3. DataLoader.run() se ejecuta automáticamente
4. Verifica si ya hay roles en la BD
5. Si NO hay roles → Crea ADMIN, USER, MANAGER
6. Verifica si existe usuario 'admin'
7. Si NO existe → Crea usuario admin con rol ADMIN
8. Logs de confirmación aparecen en consola
```

### Verificación en Logs

Cuando ves estos logs, significa que la inicialización funcionó:

```
[2025-11-27 10:00:00] INFO  ... ========== Starting DataLoader ==========
[2025-11-27 10:00:00] INFO  ... No roles found. Creating default roles...
[2025-11-27 10:00:00] INFO  ... ✓ ADMIN role created
[2025-11-27 10:00:00] INFO  ... ✓ USER role created
[2025-11-27 10:00:00] INFO  ... ✓ MANAGER role created
[2025-11-27 10:00:00] INFO  ... Admin user not found. Creating default admin user...
[2025-11-27 10:00:00] INFO  ... ✓ Admin user created successfully
[2025-11-27 10:00:00] INFO  ...   Username: admin
[2025-11-27 10:00:00] INFO  ...   Email: admin@expenseapp.com
[2025-11-27 10:00:00] INFO  ... ⚠️ Default password: admin123 (CHANGE IN PRODUCTION)
[2025-11-27 10:00:00] INFO  ... ========== DataLoader Completed Successfully ==========
```

---

## 🧪 Cómo Probar

### Opción 1: Login en Frontend
```
1. Abre http://localhost en navegador
2. Intenta login con:
   - Username: admin
   - Password: admin123
3. Deberías poder acceder
4. Podrás crear nuevos usuarios desde el panel admin
```

### Opción 2: Verificar BD Directamente
```bash
# Conectar a PostgreSQL
docker-compose exec postgres psql -U postgres -d expense_note_app

# Dentro de psql:
SELECT * FROM role;                    -- Ver roles
SELECT * FROM "user";                  -- Ver usuarios
SELECT * FROM user_role;               -- Ver asignaciones rol-usuario
```

### Opción 3: API Test con curl
```bash
# Test login endpoint
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# Deberías recibir un token JWT si es correcto
```

---

## ⚙️ Configuración en `application.properties`

La inicialización ocurre automáticamente sin necesidad de configuración adicional porque:

1. **DataLoader es un @Component**
   - Spring lo detecta automáticamente
   - Se ejecuta al startup

2. **PasswordEncoder está disponible**
   - Se configura en SecurityConfiguration
   - Se inyecta en DataLoader

3. **Repositories están disponibles**
   - UserRepository y RoleRepository se inyectan
   - Se pueden usar directamente

---

## 🔄 Ciclo de Vida

### Primera Ejecución (BD Vacía)
```
✅ DataLoader detecta BD vacía
✅ Crea todos los roles
✅ Crea usuario admin
✅ Asigna rol ADMIN a admin
✅ Listo para login
```

### Siguientes Ejecuciones (BD con Datos)
```
✅ DataLoader detecta roles ya existen
✅ Salta creación de roles
✅ Detecta usuario admin ya existe
✅ Salta creación de admin
✅ No hay duplicados
✅ Base de datos sin cambios
```

### Si Eliminas Usuario Admin
```
✅ DataLoader lo detecta
✅ Lo vuelve a crear automáticamente
✅ Garantiza que siempre hay admin disponible
```

---

## 📝 Personalizar Datos Iniciales

### Cambiar Contraseña Admin

**Opción A: En DataLoader.java (Temporal)**
```java
// Línea ~96 en DataLoader.java
String encodedPassword = passwordEncoder.encode("TU_NUEVA_CONTRASEÑA");
```

**Opción B: Via Frontend (Recomendado)**
```
1. Login con admin / admin123
2. Ve a Settings o Admin Panel
3. Cambia contraseña
4. Cambia la línea en DataLoader.java
5. Reinicia aplicación
```

### Agregar Más Roles

En `DataLoader.java`, en método `createDefaultRoles()`:
```java
// Agregua antes de cerrar el método
Role supervisorRole = Role.builder()
    .name("SUPERVISOR")
    .description("Supervisor with limited permissions")
    .build();
roleRepository.save(supervisorRole);
log.info("✓ SUPERVISOR role created");
```

### Agregar Más Usuarios Iniciales

En `DataLoader.java`, en método `run()`:
```java
// Después de createAdminUser()
if (userRepository.findByUsername("testuser").isEmpty()) {
    Role userRole = roleRepository.findByName("USER")
        .orElseThrow(() -> new RuntimeException("USER role not found"));
    
    User testUser = User.builder()
        .username("testuser")
        .password(passwordEncoder.encode("test123"))
        .email("test@expenseapp.com")
        .enabled(true)
        .roles(Set.of(userRole))
        .build();
    userRepository.save(testUser);
    log.info("✓ Test user created");
}
```

---

## ⚠️ Consideraciones de Producción

### Seguridad

```
❌ NO hacer esto en Producción:
   - Usar credenciales hardcodeadas
   - Contraseña "admin123" visible en código
   - DataLoader accesible sin verificación

✅ Hacer esto en Producción:
   - Usar variables de entorno para contraseñas
   - Cambiar admin password en primera ejecución
   - Deshabilitar DataLoader si no es necesario
   - Usar migrations (Flyway/Liquibase) en lugar de DataLoader
```

### Disable DataLoader en Producción (Opcional)

**application-prod.properties:**
```properties
# Deshabilitar DataLoader en producción
spring.data-loader.enabled=false
```

**Modificar DataLoader.java:**
```java
@Component
@ConditionalOnProperty(name = "spring.data-loader.enabled", havingValue = "true", matchIfMissing = true)
@Slf4j
public class DataLoader implements ApplicationRunner {
    // ... rest of code
}
```

---

## 🐳 Docker - Verificación

### Verificar que los Datos Persisten

```bash
# 1. Verificar datos fueron creados
docker-compose exec postgres psql -U postgres -d expense_note_app -c "SELECT * FROM role;"

# 2. Detener contenedor
docker-compose down

# 3. Reiniciar
docker-compose up -d

# 4. Verificar datos siguen ahí
docker-compose exec postgres psql -U postgres -d expense_note_app -c "SELECT * FROM role;"

# Deberías ver los mismos roles (sin duplicados)
```

---

## 🔧 Troubleshooting

### Problema: "No roles found" pero sigo viendo error 500

**Causa:** DataLoader no se ejecutó  
**Solución:**
```bash
# Reinicia con logs
docker-compose logs -f backend

# Busca "Starting DataLoader"
# Si no está → DataLoader no se ejecutó
```

### Problema: Duplicados de roles

**Causa:** DataLoader ejecutado múltiples veces  
**Solución:** Ya está protegido con `if (roleCount == 0)`

### Problema: Contraseña no funciona

**Causa:** Contraseña no está hasheada  
**Solución:** Verifica que `PasswordEncoder` esté en SecurityConfiguration

### Problema: "username admin already exists"

**Causa:** Usuario admin ya existe pero sin rol  
**Solución:** Ejecuta manualmente:
```sql
INSERT INTO user_role (user_id, role_id)
SELECT u.id, r.id FROM "user" u, role r
WHERE u.username = 'admin' AND r.name = 'ADMIN';
```

---

## 📊 Verificación Completa

### Checklist de Verificación

```bash
# 1. ¿Roles existen?
docker-compose exec postgres psql -U postgres -d expense_note_app -c "SELECT * FROM role WHERE name IN ('ADMIN', 'USER', 'MANAGER');"

# 2. ¿Usuario admin existe?
docker-compose exec postgres psql -U postgres -d expense_note_app -c "SELECT id, username, email FROM \"user\" WHERE username = 'admin';"

# 3. ¿Rol asignado correctamente?
docker-compose exec postgres psql -U postgres -d expense_note_app -c "SELECT ur.* FROM user_role ur JOIN \"user\" u ON ur.user_id = u.id WHERE u.username = 'admin';"

# 4. ¿Posiciones de ejemplo existen?
docker-compose exec postgres psql -U postgres -d expense_note_app -c "SELECT * FROM position LIMIT 5;"
```

### Expected Output
```
role:
 id | name    | description
────┼─────────┼──────────────────────────────────────
 1  | ADMIN   | Administrator with full permissions
 2  | USER    | Regular user with standard permissions
 3  | MANAGER | Manager with team oversight permissions

user:
 id | username | email
────┼──────────┼──────────────────────────
 1  | admin    | admin@expenseapp.com

user_role:
 user_id | role_id
─────────┼─────────
 1       | 1

position:
 id | name                  | description
────┼───────────────────────┼──────────────
 1  | Software Developer    | Full-stack...
 2  | Project Manager       | Project...
 3  | Designer              | UI/UX...
```

---

## 🎯 Próximos Pasos

### Después de Verificar Datos

1. ✅ Login con admin / admin123
2. ✅ Cambiar contraseña admin
3. ✅ Crear nuevos usuarios desde frontend
4. ✅ Asignar roles apropiados
5. ✅ Agregar más posiciones
6. ✅ Crear empleados y gastos

### Para Implementar Endpoint de User Creation

Ver: `EXCEPTION_HANDLING_PLAN.md` para crear endpoint seguro y validado

---

## 📚 Referencias

- **DataLoader:** `backend-springboot/src/main/java/io/sunbit/app/config/DataLoader.java`
- **SQL Init:** `backend-springboot/src/main/resources/db/init-data.sql`
- **SecurityConfig:** `backend-springboot/src/main/java/io/sunbit/app/security/configuration/SecurityConfiguration.java`
- **Repositorios:** `backend-springboot/src/main/java/io/sunbit/app/security/dao/`

---

**Documento:** Database Initialization Guide  
**Fecha:** 27 Noviembre 2025  
**Estado:** Implementado ✅  
**Próximo:** Crear endpoint para crear usuarios en Session 4
