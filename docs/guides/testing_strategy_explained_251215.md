# Estrategia de Testing - Explicación Completa

**Fecha:** 15 de Diciembre de 2025  
**Tema:** Testing Local vs CI/CD, Tipos de Tests, y Postman

---

## 📚 ÍNDICE

1. [Tipos de Tests que Tienes](#tipos-de-tests-que-tienes)
2. [Tests Locales vs GitHub Actions](#tests-locales-vs-github-actions)
3. [Cómo Exportar Colección de Postman](#cómo-exportar-colección-de-postman)
4. [Problema con Swagger (401)](#problema-con-swagger-401)
5. [GitHub Actions - Correcciones Necesarias](#github-actions---correcciones-necesarias)
6. [Plan de Acción](#plan-de-acción)

---

## 1️⃣ Tipos de Tests que Tienes

### 📂 **Tests Actuales en `/backend/src/test/java/`**

#### **A) Tests de Integración con Base de Datos** (`@DataJpaTest`)

**Ejemplo:** `EmployeeTest.java`

```java
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class EmployeeTest {
    @Autowired
    IEmployeeDao employeeDao;
    
    @Test
    public void testEmployeeSaving() {
        // Prueba guardando en BD real
    }
}
```

**Características:**
- ✅ Usa **base de datos REAL** (PostgreSQL test)
- ✅ Prueba la **capa DAO/Repository**
- ✅ Verifica que las queries funcionen
- ✅ Verifica relaciones entre entidades
- ❌ NO prueba endpoints HTTP
- ❌ NO prueba controllers
- ❌ NO prueba seguridad/autenticación

**Qué prueban:**
- Operaciones CRUD en BD
- Relaciones entre entidades (Employee-Position, Employee-Payroll)
- Constraints de BD
- Queries personalizadas

---

#### **B) Tests de Controller/Endpoint** (`@SpringBootTest` + `@AutoConfigureMockMvc`)

**Ejemplo:** `UserControllerTest.java`

```java
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private UserServiceImpl userService;
    
    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetUserById() throws Exception {
        mockMvc.perform(get("/api/users/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1));
    }
}
```

**Características:**
- ✅ Prueba **endpoints HTTP** (GET, POST, PUT, DELETE)
- ✅ Prueba **Controllers**
- ✅ Simula requests HTTP
- ✅ Verifica códigos de estado (200, 404, 401, etc.)
- ✅ Verifica JSON de respuesta
- ✅ Prueba seguridad (`@WithMockUser`)
- ⚠️ Usa **mocks** (no BD real por defecto)

**Qué prueban:**
- Endpoints REST
- Validación de entrada
- Respuestas HTTP
- Seguridad/Autorización
- Serialización JSON

---

#### **C) Tests Unitarios Puros**

**Ejemplo:** `DateUtilTest.java`

```java
public class DateUtilTest {
    @Test
    public void testDateFormatting() {
        LocalDateTime date = LocalDateTime.of(2025, 12, 15, 10, 30);
        String formatted = DateUtil.format(date);
        assertEquals("15/12/2025", formatted);
    }
}
```

**Características:**
- ✅ Prueba **lógica pura** (sin Spring)
- ✅ Muy rápidos
- ✅ Sin dependencias externas
- ❌ NO prueban integración

---

### 🆚 **Comparación de Tipos de Tests**

| Tipo | Anota | BD Real | HTTP | Velocidad | Qué Prueba |
|------|-------|---------|------|-----------|------------|
| **@DataJpaTest** | `@DataJpaTest` | ✅ Sí | ❌ No | Media | DAO/Repositories |
| **@SpringBootTest + MockMvc** | `@SpringBootTest`<br>`@AutoConfigureMockMvc` | ⚠️ Mock | ✅ Sí | Lenta | Controllers/Endpoints |
| **@WebMvcTest** | `@WebMvcTest` | ❌ Mock | ✅ Sí | Rápida | Solo Controller (sin Service) |
| **Unitarios** | `@Test` | ❌ No | ❌ No | Muy rápida | Lógica pura |

---

## 2️⃣ Tests Locales vs GitHub Actions

### 🏠 **Tests Locales (En tu máquina)**

**Para qué sirven:**
- ✅ Desarrollo diario
- ✅ Depuración (puedes poner breakpoints)
- ✅ Feedback inmediato
- ✅ Probar cambios antes de commitear

**Cómo ejecutarlos:**

```bash
# Todos los tests
./mvnw test

# Solo tests de Employee
./mvnw test -Dtest=EmployeeTest

# Solo un test específico
./mvnw test -Dtest=EmployeeTest#testEmployeeSaving

# Con perfil test
./mvnw test -Dspring.profiles.active=test
```

**Ventajas:**
- ⚡ Rápido (no esperas a GitHub)
- 🐛 Puedes debuggear
- 🔄 Iteración rápida

**Desventajas:**
- ⚠️ Depende de tu entorno local
- ⚠️ Puedes olvidar ejecutarlos

---

### ☁️ **GitHub Actions (CI/CD)**

**Para qué sirven:**
- ✅ Validación automática en cada commit/PR
- ✅ Entorno limpio y consistente
- ✅ No depende de tu máquina
- ✅ Puerta de calidad antes de merge
- ✅ Ejecuta en múltiples entornos (Java 17, 21, etc.)

**Cuándo se ejecutan:**
```yaml
on:
  push:
    branches: [ main, develop, fix/*, feature/* ]
  pull_request:
    branches: [ main, develop ]
```

**Lo que hacen:**
1. Descargan el código
2. Configuran Java
3. Compilan el proyecto
4. Ejecutan tests
5. Generan reportes de cobertura
6. Suben artefactos

**Ventajas:**
- 🔒 Garantiza que el código funciona antes de merge
- 🌍 Entorno reproducible
- 📊 Reportes automáticos

**Desventajas:**
- ⏱️ Más lento (esperas la cola de GitHub)
- 💰 Consume minutos de GitHub Actions

---

### 🎯 **¿Necesitas Ambos?**

**SÍ, y aquí está el por qué:**

#### **Workflow Ideal:**

1. **Desarrollo Local:**
   ```bash
   # Mientras programas
   ./mvnw test -Dtest=EmployeeTest
   ```
   → Feedback inmediato

2. **Antes de Commitear:**
   ```bash
   ./mvnw test
   ```
   → Aseguras que todo funciona

3. **Después de Push:**
   → GitHub Actions ejecuta automáticamente
   → Valida en entorno limpio

4. **Pull Request:**
   → GitHub Actions valida antes de merge
   → Protección de rama

---

## 3️⃣ Cómo Exportar Colección de Postman

### 📤 **Paso a Paso**

#### **Opción 1: Exportar Colección Completa**

1. **Abre Postman**
2. **Click derecho en tu colección** (e.g., "BizFlow ERP")
3. **Selecciona "Export"**
4. **Elige el formato:**
   - `Collection v2.1` (recomendado) ✅
   - `Collection v2.0`
5. **Guarda el archivo** → `bizflow-erp-postman-collection.json`
6. **Coloca en el repo:**
   ```bash
   mkdir -p docs/postman
   mv ~/Downloads/bizflow-erp-postman-collection.json docs/postman/
   ```

#### **Opción 2: Exportar Colección + Environment**

Si usas variables de entorno en Postman:

1. **Exporta la colección** (pasos anteriores)
2. **Exporta el environment:**
   - Click en el icono de ambiente (⚙️)
   - Click en "..." junto al ambiente
   - "Export"
3. **Guarda ambos:**
   ```
   docs/postman/
   ├── bizflow-erp-collection.json
   ├── bizflow-erp-environment-prod.json
   ├── bizflow-erp-environment-dev.json
   └── README.md  (instrucciones de uso)
   ```

#### **Opción 3: Generar Tests en Postman**

Puedes añadir **tests automáticos** en Postman:

```javascript
// En la pestaña "Tests" de un request
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Response has employee data", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData).to.have.property('id');
    pm.expect(jsonData).to.have.property('name');
});
```

Luego exporta y tendrás tests automáticos incluidos.

---

## 4️⃣ Problema con Swagger (401)

### 🔒 **Error HTTP 401 - Unauthorized**

**Causa:** Swagger UI requiere autenticación.

### **Soluciones:**

#### **A) Deshabilitar seguridad para Swagger (Desarrollo)**

En `SecurityConfig.java` o similar:

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Permitir Swagger sin autenticación
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .anyRequest().authenticated()
            );
        return http.build();
    }
}
```

#### **B) Usar Autenticación en Swagger**

Si quieres mantener seguridad:

```java
@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .components(new Components()
                .addSecuritySchemes("bearer-jwt",
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")))
            .addSecurityItem(new SecurityRequirement()
                .addList("bearer-jwt"));
    }
}
```

Luego en Swagger UI:
1. Click en "Authorize" 🔓
2. Pega tu JWT token
3. Ya puedes hacer requests

#### **C) Verificar URL Correcta**

Swagger puede estar en diferentes URLs:

```
✅ http://localhost:8181/swagger-ui.html
✅ http://localhost:8181/swagger-ui/index.html
✅ http://localhost:8181/v3/api-docs
```

---

## 5️⃣ GitHub Actions - Correcciones Necesarias

### ❌ **Problemas Actuales**

#### **1. Branch Name Incorrecto**

**En los workflows:**
```yaml
branches: [ main, develop, fix/*, feature/* ]
```

**Problema:** Tu rama es `dev`, no `develop`

**Solución:**
```yaml
branches: [ main, dev, fix/*, feature/* ]
```

#### **2. Falta Configuración de Entornos**

**Actualmente:** GitHub Actions solo ejecuta en un entorno (probablemente sin perfil específico)

**Deberías tener:**
- Tests con perfil `test` ✅
- No necesitas ejecutar en `dev` o `prod` en CI/CD ❌

**¿Por qué?**
- `dev` y `prod` son para **despliegue**, no para tests
- Los tests deben ejecutarse en entorno `test` limpio
- No quieres tests modificando datos de `dev` o `prod`

---

### ✅ **Correcciones a Aplicar**

#### **Archivo: `.github/workflows/backend-build-test.yml`**

**Cambio 1:** Corregir nombre de rama

```yaml
on:
  push:
    branches: [ main, dev, fix/*, feature/* ]  # ← Cambiar develop por dev
  pull_request:
    branches: [ main, dev ]  # ← Cambiar develop por dev
```

**Cambio 2:** Usar perfil test

```yaml
- name: Run Unit Tests
  run: |
    cd backend
    mvn test -Dspring.profiles.active=test --no-transfer-progress
  env:
    SPRING_PROFILES_ACTIVE: test
```

**Cambio 3:** Configurar base de datos test en CI/CD

Puedes usar:
- PostgreSQL service container
- H2 in-memory para tests rápidos

```yaml
services:
  postgres:
    image: postgres:16-alpine
    env:
      POSTGRES_DB: erp_test_db
      POSTGRES_USER: erp_test_user
      POSTGRES_PASSWORD: <DB_PASSWORD>word
    ports:
      - 5434:5432
    options: >-
      --health-cmd pg_isready
      --health-interval 10s
      --health-timeout 5s
      --health-retries 5
```

---

## 6️⃣ Plan de Acción

### 🎯 **Corto Plazo (Hoy/Mañana)**

#### **1. Exportar Colección de Postman**

```bash
# 1. Exportar desde Postman
# 2. Guardar en el proyecto
mkdir -p docs/postman
mv ~/Downloads/BizFlow-ERP.postman_collection.json docs/postman/

# 3. Crear README
cat > docs/postman/postman_collection_guide.md << 'EOF'
# Colección de Postman - BizFlow ERP

## Cómo Usar

1. Importar colección en Postman
2. Configurar variables de entorno:
   - `base_url`: http://localhost:8181
   - `jwt_token`: (obtener del endpoint /auth/login)
3. Ejecutar requests

## Endpoints Incluidos

- Employee CRUD
- Position CRUD
- Payroll CRUD
- Expense CRUD
- Auth (login, signup)
EOF

# 4. Commitear
git add docs/postman/
git commit -m "docs: add Postman collection for API testing"
```

#### **2. Corregir GitHub Actions**

Voy a crear los archivos corregidos.

#### **3. Arreglar Swagger**

Voy a mostrarte cómo configurarlo.

---

### 🎯 **Medio Plazo (Esta Semana)**

#### **4. Crear Tests de Controller para Employee**

```java
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class EmployeeControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetAllEmployees() throws Exception {
        mockMvc.perform(get("/api/employees"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    public void testCreateEmployee() throws Exception {
        String employeeJson = """
        {
            "name": "Test",
            "surname": "Employee",
            "birthDate": "1990-01-01T00:00:00",
            "email": "test@bizflowerp.com",
            "position": { "id": 1 }
        }
        """;
        
        mockMvc.perform(post("/api/employees")
            .contentType(MediaType.APPLICATION_JSON)
            .content(employeeJson))
            .andExpect(status().isCreated());
    }
}
```

---

### 🎯 **Largo Plazo (Próximas Semanas)**

#### **5. Mejorar CI/CD**

- ✅ Añadir tests de integración
- ✅ Configurar base de datos test en GitHub Actions
- ✅ Añadir reportes de cobertura
- ✅ Configurar SonarQube
- ✅ Añadir linting/code quality checks

---

## 📊 Resumen Visual

```
┌─────────────────────────────────────────────────┐
│           ESTRATEGIA DE TESTING                 │
└─────────────────────────────────────────────────┘

🏠 LOCAL                        ☁️ CI/CD (GitHub Actions)
─────────                       ────────────────────────
│                               │
├─ Unit Tests                   ├─ Unit Tests
│  (@Test)                      │  (@Test)
│  ├─ DateUtilTest              │  ├─ Todos los tests
│  └─ ...                       │  └─ En entorno limpio
│                               │
├─ Integration Tests            ├─ Integration Tests
│  (@DataJpaTest)               │  (@DataJpaTest)
│  ├─ EmployeeTest              │  ├─ Con PostgreSQL service
│  ├─ PayrollTest               │  └─ Base de datos test
│  └─ ...                       │
│                               │
├─ Controller Tests             ├─ Controller Tests
│  (@SpringBootTest)            │  (@SpringBootTest)
│  ├─ UserControllerTest        │  └─ Con autenticación mock
│  └─ [CREAR MÁS] ⏳            │
│                               │
└─ Manual Tests                 └─ Deployment Tests
   (Postman) 🖱️                   (E2E - futuro)

```

---

## ✅ Checklist de Acción

- [ ] Exportar colección de Postman
- [ ] Corregir rama `develop` → `dev` en workflows
- [ ] Configurar Swagger UI (permitir sin auth o con JWT)
- [ ] Crear `EmployeeControllerTest.java`
- [ ] Añadir PostgreSQL service a GitHub Actions
- [ ] Añadir más controller tests (Position, Payroll, Expense)
- [ ] Documentar proceso de testing en README
- [ ] Configurar reportes de cobertura

---

¿Por dónde quieres empezar? Te recomiendo:
1. Exportar Postman (5 minutos)
2. Corregir GitHub Actions (10 minutos)
3. Arreglar Swagger (5 minutos)
4. Crear EmployeeControllerTest (30 minutos)
