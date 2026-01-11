**Fecha:** 2025-11-27

# 🔐 Analysis and Solution: Signup Issue

**Documento:** Signup Issue Analysis & Solutions  
**Fecha:** 27 Noviembre 2025  
**Status:** Análisis + Plan de solución

---

## 🔍 Problema Identificado

**Síntoma:** Acceder a signup en frontend devuelve error o no permite registrarse  
**Causa Probable:** No hay usuarios en la BD, y la lógica de signup requiere un usuario administrativo o roles existentes

---

## 🏗️ Análisis de la Arquitectura de Seguridad

### Entidades Relacionadas

```
user (tabla) ← Spring Security User
    ↓
user_role (tabla) ← Muchos-a-muchos
    ↓
role (tabla) ← ADMIN, USER, etc.
```

### Problema en Ciclo de Bootstrap

```
1. Primera vez: BD vacía
2. Intenta acceder a signup
3. Frontend llama a algún endpoint
4. Backend busca roles o usuarios (no existen)
5. Error 500 o 400
6. No se puede registrar
```

---

## ✅ Soluciones Disponibles

### Solución 1: Insertar Datos Iniciales (Recomendado)

**Nivel de Dificultad:** Bajo  
**Tiempo:** 10 minutos  
**Escalabilidad:** Media

```sql
-- Ejecutar una sola vez
INSERT INTO role (id, name, description) VALUES 
    (1, 'ADMIN', 'Administrator role'),
    (2, 'USER', 'Regular user role');

-- Crear usuario admin por defecto
INSERT INTO "user" (id, name, surname, password, email, enabled) VALUES
    (1, 'admin', 'hashed_password_here', 'admin@app.com', true);

-- Asignar rol ADMIN al usuario
INSERT INTO user_role (user_id, role_id) VALUES (1, 1);
```

**Archivos a crear:**
- `backend/src/main/resources/db/init.sql`
- Modificar `application.properties` para ejecutar en startup

---

### Solución 2: DataLoader en Spring Boot

**Nivel de Dificultad:** Medio  
**Tiempo:** 20 minutos  
**Escalabilidad:** Alta (mejor para producción)

```java
@Component
@Slf4j
public class DataLoader implements ApplicationRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // Solo si tablas vacías
        if (roleRepository.count() == 0) {
            log.info("Loading initial data...");

            // Crear roles
            Role adminRole = Role.builder()
                .name("ADMIN")
                .description("Administrator role")
                .build();
            roleRepository.save(adminRole);

            Role userRole = Role.builder()
                .name("USER")
                .description("Regular user role")
                .build();
            roleRepository.save(userRole);

            // Crear usuario admin
            User admin = User.builder()
                .name("Admin").surname("User")
                .password(passwordEncoder.encode("admin123"))
                .email("admin@expenseapp.com")
                .enabled(true)
                .roles(Set.of(adminRole))
                .build();
            userRepository.save(admin);

            log.info("Initial data loaded successfully");
        }
    }
}
```

**Ventajas:**
- Ejecuta automáticamente
- No modifica SQL
- Se puede deshabilitar por configuración

---

### Solución 3: Modificar Signup para Permitir Registro Libre

**Nivel de Dificultad:** Bajo-Medio  
**Tiempo:** 15 minutos  
**Escalabilidad:** Alta (mejor para público)

```java
@RestController
@RequestMapping("/api/v1/auth")
@Slf4j
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignUpRequest signupRequest) {
        
        // Validar input
        // Validación de nombre y apellido si es necesario
        }

        if (userService.existsByEmail(signupRequest.getEmail())) {
            throw new BadRequestException("Email already registered");
        }

        // Crear usuario
        User newUser = User.builder()
            .name(signupRequest.getName()).surname(signupRequest.getSurname())
            .password(passwordEncoder.encode(signupRequest.getPassword()))
            .email(signupRequest.getEmail())
            .enabled(true)
            .build();

        // Asignar rol por defecto (USER)
        Role userRole = roleRepository.findByName("USER")
            .orElseGet(() -> {
                // Si no existe, crear el rol
                Role newRole = Role.builder()
                    .name("USER")
                    .description("Regular user role")
                    .build();
                return roleRepository.save(newRole);
            });

        newUser.setRoles(Set.of(userRole));

        User savedUser = userService.save(newUser);
        
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new MessageResponse("User registered successfully"));
    }
}
```

---

### Solución 4: SQL Migration Script (Para Producción)

**Nivel de Dificultad:** Medio  
**Tiempo:** 30 minutos  
**Escalabilidad:** Máxima (recomendado para producción)

```
backend/src/main/resources/db/migration/
├── V1__initial_schema.sql
├── V2__insert_roles.sql
└── V3__insert_admin_user.sql
```

**V2__insert_roles.sql:**
```sql
INSERT INTO role (id, name, description) VALUES 
    (1, 'ADMIN', 'Administrator role'),
    (2, 'USER', 'Regular user role'),
    (3, 'MANAGER', 'Manager role');
```

**V3__insert_admin_user.sql:**
```sql
INSERT INTO "user" (id, username, password, email, enabled) VALUES
    (1, 'admin', '$2a$10$...',  'admin@app.com', true);

INSERT INTO user_role (user_id, role_id) VALUES (1, 1);
```

**Ventajas:**
- Versionado con Flyway/Liquibase
- Reproducible en cualquier ambiente
- Escalable profesionalmente

---

## 🎯 Recomendación: Solución Híbrida

**Para desarrollo + producción:**

1. **Desarrollo local:** Solución 2 (DataLoader) + Solución 3 (Signup libre)
2. **Staging:** SQL migrations (Solución 4)
3. **Producción:** SQL migrations + backup de datos

---

## 📋 Implementación Recomendada (Session 3)

### Paso 1: Crear Endpoint de Signup Mejorado

```java
// API: POST /api/v1/auth/signup
// Body: { "name": "User", "surname": "Test", "password": "pass123", "email": "user@test.com" }
// Response: { "id": 1, "name": "User", "surname": "Test", "email": "user@test.com", "message": "Registered" }
```

### Paso 2: Crear DataLoader para Roles

```java
@Component
public class RoleDataLoader implements ApplicationRunner {
    // ... crear roles ADMIN, USER, MANAGER
}
```

### Paso 3: Frontend - Mejorar Página de Signup

```javascript
// frontend/src/pages/Signup.jsx
const handleSignup = async (formData) => {
    try {
        const response = await axios.post('/api/v1/auth/signup', {
            name: formData.name,
            surname: formData.surname,
            email: formData.email,
            password: formData.password
        });
        
        if (response.status === 201) {
            // Éxito: mostrar mensaje
            // Redirigir a login
            alert('Registration successful! Please login.');
            navigate('/login');
        }
    } catch (error) {
        // Mostrar error específico del servidor
        alert(error.response.data.message || 'Registration failed');
    }
};
```

### Paso 4: Mejorar Validaciones

```java
public class SignUpRequest {
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 50, message = "Name must be 2-50 characters")
    private String name;
    @NotBlank(message = "Surname is required")
    @Size(min = 2, max = 128, message = "Surname must be 2-128 characters")
    private String surname;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 50, message = "Password must be 6-50 characters")
    private String password;
}
```

---

## 🧪 Testing del Fix

```bash
# 1. Verificar roles en BD
docker-compose exec postgres psql -U postgres -d expense_note_app -c "SELECT * FROM role;"

# 2. Probar signup via API
curl -X POST http://localhost:8080/api/v1/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test",
    "surname": "User",
    "email": "test@app.com",
    "password": "password123"
  }'

# 3. Verificar usuario creado
docker-compose exec postgres psql -U postgres -d expense_note_app -c "SELECT * FROM \"user\";"

# 4. Probar login
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test",
    "surname": "User",
    "password": "password123"
  }'
```

---

## 🔐 Seguridad en Signup

### Validaciones Críticas

```java
// ✅ HACER
- Validar email con regex
- Encriptar contraseña (PasswordEncoder)
- Validar longitud (8+ caracteres recomendado)
- Verificar duplicados
- Limitar intentos de signup

// ❌ NO HACER
- Guardar contraseña en texto plano
- Aceptar emails inválidos
-- Permitir nombres/apellidos especiales
- Loguear contraseñas
- Revelar si user existe
```

---

## 📊 Flow Completo de Signup Mejorado

```
1. Usuario accede a /signup en frontend
    ↓
2. Frontend valida input (password length, email format)
    ↓
3. POST /api/v1/auth/signup
    ↓
4. Backend valida (validaciones en RequestBody)
    ↓
5. Backend verifica que no exista usuario
    ↓
6. Backend encripta contraseña
    ↓
7. Backend crea usuario con rol USER por defecto
    ↓
8. Backend retorna 201 CREATED
    ↓
9. Frontend muestra "Registration successful"
    ↓
10. Frontend redirige a login
    ↓
11. Usuario hace login con credenciales nuevas
    ↓
12. Backend retorna JWT token
    ↓
13. Frontend guarda token y redirige a dashboard
```

---

## 📋 Checklist para Session 3

- [ ] Analizar código actual de signup en backend
- [ ] Analizar código actual de signup en frontend
- [ ] Crear/actualizar clase `SignUpRequest` con validaciones
- [ ] Crear o mejorar `AuthController.signup()`
- [ ] Crear `RoleDataLoader` para roles iniciales
- [ ] Mejorar página de signup en frontend
- [ ] Mejorar manejo de errores en frontend
- [ ] Crear tests para signup
- [ ] Probar end-to-end

---

## 🎓 Notas Importantes

### Por qué "arrays vacíos" no es culpa de persistencia

1. ✅ BD persiste datos
2. ✅ Volumen Docker guarda todo
3. ❌ El problema es: **no hay usuarios iniciales**
4. ❌ Signup no funciona sin roles
5. ❌ Crear datos requiere usuario logueado

### Solución: Datos Iniciales + Signup Libre

- Crear roles iniciales (DataLoader)
- Permitir signup sin ser logueado
- Asignar rol USER por defecto
- ✅ Listo para empezar

---

**Documento:** Signup Issue Analysis  
**Próxima acción:** Implementar en Session 3  
**Estimado:** 1-2 horas para implementación completa
