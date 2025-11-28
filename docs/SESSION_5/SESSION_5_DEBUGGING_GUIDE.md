# Guía de Debugging - ExpenseNoteApp

**Última actualización:** Session 5  
**Versión:** 1.0

---

## 📋 Tabla de Contenidos

1. [Errores Comunes y Soluciones](#errores-comunes-y-soluciones)
2. [Logs y Debugging](#logs-y-debugging)
3. [Testing Rápido](#testing-rápido)
4. [Base de Datos](#base-de-datos)
5. [Docker](#docker)
6. [Frontend](#frontend)

---

## 🐛 Errores Comunes y Soluciones

### Error 1: ClassCastException en Authentication

**Síntoma:**
```
java.lang.ClassCastException: org.springframework.security.core.userdetails.User 
cannot be cast to io.sunbit.app.security.dto.ExpenseUser
```

**Ubicación:** `AuthenticationController.java:56`

**Causa:** `UserServiceImpl.loadUserByUsername()` retorna `User` genérico en lugar de `ExpenseUser`

**Solución:**
```java
// En UserServiceImpl.loadUserByUsername()
// MALO ❌
return new org.springframework.security.core.userdetails.User(...);

// BUENO ✅
return optionalUser.get();  // ExpenseUser implements UserDetails
```

---

### Error 2: NullPointerException en findByEmail()

**Síntoma:**
```
java.util.NoSuchElementException: No value present
at java.util.Optional.get(Optional.java:155)
```

**Ubicación:** `EmployeeServiceImpl.java:109` o similar

**Causa:** Llamar `.get()` en un Optional que podría estar vacío

**Solución:**
```java
// MALO ❌
return optional.get();

// BUENO ✅
return optional.orElse(null);
// o
return optional.orElseThrow(() -> new ResourceNotFoundException("Not found"));
```

---

### Error 3: Double Password Encoding

**Síntoma:** Login falla con "Invalid credentials" aunque password es correcto

**Causa:** Contraseña codificada dos veces (en AuthController Y en UserServiceImpl)

**Solución:** AuthController codifica, UserServiceImpl solo guarda
```java
// En UserServiceImpl.setUser()
// MALO ❌
user.setPassword(passwordEncoder.encode(user.getPassword()));

// BUENO ✅
user.setPassword(user.getPassword());  // Ya codificada
```

---

### Error 4: Form Field Mismatch

**Síntoma:** Backend rechaza signup con validación error

**Causa:** Frontend envía campos distintos a los esperados por backend DTO

**Solución:** Verificar `SignUpRequest.java` vs `SignupPage.jsx`
```java
// Backend espera:
{
  "username": "...",
  "email": "...",
  "password": "..."
}

// Verificar que frontend envía lo mismo
```

---

### Error 5: CORS Policy Block

**Síntoma:** Navegador bloquea request con CORS error

**Causa:** Backend falta @CrossOrigin o configuración de CORS

**Solución:**
```java
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/...")
public class MyController { ... }

// O en SecurityConfig:
.cors().and()
```

---

### Error 6: JWT Token Inválido

**Síntoma:** Login exitoso pero navegación a /dashboard falla (401 Unauthorized)

**Causa:** Token no se guardó correctamente o expiró

**Solución:**
```javascript
// Verificar en DevTools > Application > localStorage
console.log(localStorage.getItem('authToken'));

// Decodificar en https://jwt.io
// Verificar que 'exp' sea futura
```

---

### Error 7: Docker Container Unhealthy

**Síntoma:** `docker-compose ps` muestra "unhealthy"

**Causa:** Aplicación no inicia o health check falla

**Solución:**
```bash
# Ver logs del contenedor
docker-compose logs backend

# O ejecutar build desde cero
docker-compose down
docker system prune -a --volumes -f
docker-compose up -d --build
```

---

## 📊 Logs y Debugging

### Ver Logs en Tiempo Real

```bash
# Backend
docker-compose logs -f backend

# Frontend  
docker-compose logs -f frontend

# Database
docker-compose logs -f postgres

# Todos
docker-compose logs -f
```

### Filtrar Logs por Patrón

```bash
# Ver solo errores
docker-compose logs backend | grep ERROR

# Ver solo cierta clase
docker-compose logs backend | grep "UserServiceImpl"

# Últimas 100 líneas
docker-compose logs backend --tail=100
```

### Niveles de Log en Application.properties

```properties
# Para debugging detallado
logging.level.io.sunbit.app=DEBUG
logging.level.org.springframework.security=DEBUG
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE

# Para producción
logging.level.root=WARN
logging.level.io.sunbit.app=INFO
```

---

### Agregar Logs en Código

```java
// Importar
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// En clase
private static final Logger logger = LoggerFactory.getLogger(MyClass.class);

// Usar
logger.debug("Buscando usuario: {}", email);
logger.info("Usuario creado: {}", user.getId());
logger.error("Error al guardar:", exception);
logger.warn("Email duplicado: {}", email);
```

---

## 🧪 Testing Rápido

### Test de Signup

```bash
curl -X POST http://localhost:8080/api/v1/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "Password123"
  }'
```

**Respuestas esperadas:**
- `201 Created` - Éxito
- `400 Bad Request` - Email ya existe o validación
- `422 Unprocessable Entity` - Validación fallida

---

### Test de Login

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "testuser@example.com",
    "password": "TestPass123456"
  }'
```

**Respuesta exitosa:**
```json
{
  "email": "testuser@example.com",
  "accessToken": "eyJhbGciOiJIUzUxMiJ9..."
}
```

---

### Test de Endpoint Autenticado

```bash
# Primero obtener token
TOKEN=$(curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"testuser@example.com","password":"TestPass123456"}' \
  | jq -r '.accessToken')

# Usar token en request
curl -X GET http://localhost:8080/api/v1/users/profile \
  -H "Authorization: Bearer $TOKEN"
```

---

### Test de Health Check

```bash
# Backend
curl http://localhost:8080/actuator/health

# Database (desde contenedor)
docker-compose exec postgres pg_isready -U postgres
```

---

## 🗄️ Base de Datos

### Conectarse a PostgreSQL

```bash
# Vía Docker
docker-compose exec postgres psql -U postgres -d expense_note_app

# O con password
PGPASSWORD=postgres docker-compose exec postgres psql -U postgres -d expense_note_app
```

### Queries Útiles

```sql
-- Ver todos los usuarios
SELECT * FROM expense_user;

-- Ver usuarios con sus roles
SELECT u.id, u.email, r.name 
FROM expense_user u
LEFT JOIN user_role ur ON u.id = ur.user_id
LEFT JOIN role r ON ur.role_id = r.id
ORDER BY u.id;

-- Ver roles disponibles
SELECT * FROM role;

-- Contar gastos por usuario
SELECT u.email, COUNT(e.id) as count
FROM expense_user u
LEFT JOIN expense e ON u.id = e.user_id
GROUP BY u.id, u.email;

-- Crear usuario de test (sin signup API)
INSERT INTO expense_user (email, password) 
VALUES ('testuser@example.com', '$2a$10$...');

-- Reset de contraseña
UPDATE expense_user 
SET password = '$2a$10$...' 
WHERE email = 'user@example.com';

-- Eliminar usuario (si no hay FK)
DELETE FROM expense_user WHERE email = 'testuser@example.com';

-- Ver esquema de tabla
\d expense_user;
```

---

### Backup y Restore

```bash
# Backup
docker-compose exec postgres pg_dump -U postgres expense_note_app > backup.sql

# Restore
docker-compose exec -T postgres psql -U postgres expense_note_app < backup.sql
```

---

## 🐳 Docker

### Verificar Estado

```bash
# Ver contenedores
docker-compose ps

# Ver estadísticas
docker stats

# Ver redes
docker network ls
docker inspect expense_network

# Ver volúmenes
docker volume ls
docker volume inspect expensenoteapp_postgres_data
```

---

### Debugging de Contenedores

```bash
# Entrar al contenedor backend
docker-compose exec backend bash

# Dentro del contenedor:
# Ver procesos
ps aux

# Ver logs locales
tail -f /var/log/...

# Probar conectividad a DB
apt-get update && apt-get install -y postgresql-client
psql -h postgres -U postgres -d expense_note_app
```

---

### Reconstruir desde Cero

```bash
# Opción 1: Reconstrucción limpia
docker-compose down -v                    # Elimina contenedores y volúmenes
docker system prune -a -f --volumes       # Limpia todo
docker-compose up -d --build              # Reconstruye

# Opción 2: Reconstrucción selectiva
docker-compose build backend --no-cache   # Solo backend sin cache
docker-compose up -d                      # Reinicia

# Opción 3: Verificar imágenes
docker images
docker rmi IMAGE_ID                       # Eliminar imagen vieja
```

---

## 🌐 Frontend

### Debugging en Browser

**DevTools > Console:**
```javascript
// Ver token guardado
localStorage.getItem('authToken')

// Ver authStore (Zustand)
console.log(useAuthStore.getState())

// Simular logout
localStorage.removeItem('authToken')

// Ver headers enviados
// Network tab > Seleccionar request > Headers
```

---

### Problemas Comunes Frontend

**Problema: Ruta /dashboard muestra página vacía**
```javascript
// Verificar en App.jsx que la ruta existe
// Verificar que PrivateRoute.jsx protege correctamente
// Verificar en DashboardPage.jsx que carga datos

// Debug:
console.log('User:', user)
console.log('Token:', localStorage.getItem('authToken'))
```

---

**Problema: Formulario no envía datos**
```javascript
// Verificar handleSubmit en form
// Verificar que inputs tenían names/values
// Verificar que apiClient está importado correctamente

// Debug:
console.log('Form data:', formData)
```

---

**Problema: CORS error al hacer request**
```javascript
// 1. Verificar que backend tiene @CrossOrigin
// 2. Verificar que URL es correcta
// 3. Verificar headers

// Debug:
// Abrir DevTools > Network > XHR
// Ver request headers y response headers
```

---

### Reconstruir Frontend

```bash
# Dentro de contenedor frontend
cd /app
npm install
npm run build

# O fuera del contenedor
cd frontend
npm install
npm run build
docker-compose restart frontend
```

---

### Verificar Compilación JSX

```bash
# Ver errores de build
npm run build

# Ver warnings
npm run lint

# Test unitarios
npm test
```

---

## 🔍 Checklist de Debugging

Cuando algo no funciona, seguir este orden:

- [ ] ¿Error en logs del contenedor? → Ver `docker-compose logs`
- [ ] ¿Error en DevTools console? → Verificar en browser
- [ ] ¿Status code HTTP inválido? → Ver respuesta en Network tab
- [ ] ¿Validación fallando? → Verificar DTOs y constraints
- [ ] ¿Autorización fallando? → Verificar token y roles
- [ ] ¿Conectividad DB? → Probar query SQL directa
- [ ] ¿Token expirado? → Verificar `exp` en jwt.io
- [ ] ¿Cambios no reflejados? → Rebuild con `--no-cache`
- [ ] ¿Todavía no funciona? → Clean slate con `docker system prune -a -f --volumes`

---

## 📞 Contacto y Escalación

Cuando todo falla:

1. **Revisar commits recientes:**
   ```bash
   git log --oneline -10
   git diff HEAD~1
   ```

2. **Revertir último cambio:**
   ```bash
   git revert HEAD --no-edit
   ```

3. **Backup y comprobar en rama limpia:**
   ```bash
   git stash
   git checkout clean-backup-branch
   ```

4. **Documentar el error:**
   - Captura de logs
   - Stack trace completo
   - Pasos para reproducir
   - Versiones (Java, Spring, Node, etc.)

---

**Status:** ✅ Guía completa  
**Útil para:** Sessions 6+  
**Mantener actualizado:** Sí

