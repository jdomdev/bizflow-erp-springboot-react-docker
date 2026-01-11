# Análisis de GitHub Actions Workflows y Estrategia de Testing

**Fecha:** 16 de Diciembre de 2025  
**Objetivo:** Evaluar workflows actuales y determinar necesidad de más tests automáticos

---

## 📊 RESUMEN EJECUTIVO

### Estado Actual
- ✅ **3 workflows configurados** (Backend, Docker, Frontend)
- ✅ **10 archivos de test** encontrados en el proyecto
- ⚠️ **PROBLEMA CRÍTICO**: Tests de integración necesitan PostgreSQL pero workflows NO lo tienen configurado
- ⚠️ **COBERTURA INCOMPLETA**: Faltan tests de controller para endpoints principales

### Recomendaciones Prioritarias
1. 🔴 **CRÍTICO**: Añadir PostgreSQL service container a `backend-build-test.yml`
2. 🟡 **IMPORTANTE**: Crear tests de controller (Employee, Position, Payroll, Expense)
3. 🟡 **IMPORTANTE**: Mejorar tests de frontend (actualmente con `continue-on-error: true`)
4. 🟢 **OPCIONAL**: Añadir tests E2E con Playwright/Cypress

---

## 🔍 ANÁLISIS DETALLADO DE WORKFLOWS

### 1️⃣ Backend Build & Test (`backend-build-test.yml`)

#### **Cuándo se ejecuta:**
```yaml
on:
  push:
    branches: [ main, dev, fix/*, feature/* ]
    paths:
      - 'backend/**'
      - 'pom.xml'
  pull_request:
    branches: [ main, dev ]
```
- ✅ Se activa en **push** a `main`, `dev`, y ramas `fix/*`, `feature/*`
- ✅ Se activa en **pull requests** contra `main` o `dev`
- ✅ Solo si hay cambios en `backend/` o `pom.xml` (eficiente)

---

#### **Job 1: Build (Tests + Coverage)**

**Paso 1: Setup Java**
```yaml
- name: Set up JDK 17
  uses: actions/setup-java@v3
  with:
    java-version: '17'
    distribution: 'adopt'
    cache: maven
```
- Instala Java 17 (AdoptOpenJDK)
- Cachea dependencias Maven para builds más rápidos

---

**Paso 2: Build Maven**
```yaml
- name: Build with Maven
  run: mvn clean package -DskipTests -X
  env:
    MAVEN_OPTS: "-Xmx1024m"
```
- ✅ Compila el proyecto sin ejecutar tests
- Usa 1GB de memoria heap
- `-X` = modo debug (verbose)

---

**Paso 3: Run Unit Tests**
```yaml
- name: Run Unit Tests
  run: mvn test -Dtest="**/*Test" --no-transfer-progress
```
- Ejecuta tests que coincidan con patrón `**/*Test`
- 🔴 **PROBLEMA**: Tu proyecto tiene tests como `EmployeeTest`, `PayrollTest`, etc. que son `@DataJpaTest`
- 🔴 **FALLA**: No hay PostgreSQL configurado → tests de BD fallarán

---

**Paso 4: Run Integration Tests**
```yaml
- name: Run Integration Tests
  run: mvn verify -DskipUnitTests --no-transfer-progress
```
- Ejecuta fase `verify` (tests de integración)
- 🔴 **PROBLEMA**: Igual que arriba, necesita PostgreSQL

---

**Paso 5: Coverage (Jacoco)**
```yaml
- name: Generate Coverage Report
  run: mvn jacoco:report

- name: Upload Coverage to Codecov
  uses: codecov/codecov-action@v3
  with:
    files: ./build/backend/site/jacoco/jacoco.xml
```
- ✅ Genera reporte de cobertura con Jacoco
- ✅ Sube a Codecov (si tienes cuenta)
- ⚠️ Solo mide cobertura de tests que se ejecutan (si fallan, cobertura = 0%)

---

**Paso 6: Archive Artifacts**
```yaml
- name: Archive test results
  if: always()
  uses: actions/upload-artifact@v3
  with:
    name: maven-test-reports
    path: build/backend/surefire-reports/

- name: Archive coverage reports
  if: always()
  uses: actions/upload-artifact@v3
  with:
    name: coverage-reports
    path: build/backend/site/jacoco/
```
- ✅ Guarda reportes de tests (incluso si fallan)
- ✅ Puedes descargarlos desde GitHub Actions UI

---

#### **Job 2: SonarQube (Análisis de Calidad)**

```yaml
sonarqube:
  runs-on: ubuntu-latest
  needs: build  # ⚠️ Solo se ejecuta si 'build' tiene éxito
  
  steps:
  - name: SonarQube Scan
    env:
      SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
    run: |
      mvn clean verify sonar:sonar \
        -Dsonar.projectKey=BizflowERP \
        -Dsonar.sources=src/main \
        -Dsonar.exclusions='src/test/**'
    if: env.SONAR_TOKEN != ''
```

**Qué hace:**
- ✅ Analiza código buscando bugs, code smells, vulnerabilidades
- ✅ Solo se ejecuta si tienes `SONAR_TOKEN` configurado (opcional)
- ✅ Excluye tests del análisis (`src/test/**`)

**Métricas que revisa:**
- Complejidad ciclomática
- Duplicación de código
- Deuda técnica
- Vulnerabilidades de seguridad
- Cobertura de tests

---

### 📋 **Tests que SE EJECUTAN en Backend Workflow**

Según tu estructura de proyecto, estos son los tests que GitHub Actions intenta ejecutar:

| Test File | Tipo | ¿Necesita BD? | Estado Actual |
|-----------|------|---------------|---------------|
| `EmployeeTest.java` | @DataJpaTest | ✅ SÍ (PostgreSQL) | 🔴 **FALLA** (sin BD) |
| `PayrollTest.java` | @DataJpaTest | ✅ SÍ (PostgreSQL) | 🔴 **FALLA** (sin BD) |
| `ExpenseTest.java` | @DataJpaTest | ✅ SÍ (PostgreSQL) | 🔴 **FALLA** (sin BD) |
| `PositionTest.java` | @DataJpaTest | ✅ SÍ (PostgreSQL) | 🔴 **FALLA** (sin BD) |
| `UserTest.java` | @DataJpaTest | ✅ SÍ (PostgreSQL) | 🔴 **FALLA** (sin BD) |
| `RoleTest.java` | @DataJpaTest | ✅ SÍ (PostgreSQL) | 🔴 **FALLA** (sin BD) |
| `IUserDaoTest.java` | @DataJpaTest | ✅ SÍ (PostgreSQL) | 🔴 **FALLA** (sin BD) |
| `IRoleDaoTest.java` | @DataJpaTest | ✅ SÍ (PostgreSQL) | 🔴 **FALLA** (sin BD) |
| `UserControllerTest.java` | @SpringBootTest+Mock | ❌ NO (usa @MockBean) | ✅ **PASA** |
| `DateUtilTest.java` | Test unitario | ❌ NO | ✅ **PASA** |

**Conclusión:** 8 de 10 tests FALLAN porque necesitan PostgreSQL.

---

### 🔧 **SOLUCIÓN: Añadir PostgreSQL Service Container**

Para que los tests de BD funcionen en GitHub Actions, necesitas añadir esto a `backend-build-test.yml`:

```yaml
jobs:
  build:
    runs-on: ubuntu-latest
    
    # ⭐ AÑADIR ESTO:
    services:
      postgres:
        image: postgres:16-alpine
        env:
          POSTGRES_DB: testdb
          POSTGRES_USER: testuser
          POSTGRES_PASSWORD: testpass
        ports:
          - 5434:5432
        options: >-
          --health-cmd pg_isready
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5
    
    steps:
    - uses: actions/checkout@v3
    # ... resto de steps
```

**Configurar `application-test.properties`:**
```properties
spring.datasource.url=jdbc:postgresql://localhost:5434/testdb
spring.datasource.username=testuser
spring.datasource.password=testpass
```

---

## 2️⃣ Docker Build & Push (`docker-build-test.yml`)

#### **Cuándo se ejecuta:**
```yaml
on:
  push:
    branches: [ main, dev ]
    tags: [ 'v*' ]
  workflow_dispatch:  # ⭐ También manual
```
- ✅ Push a `main` o `dev` (NO ramas feature/fix)
- ✅ Cuando creas tags tipo `v1.0.0`
- ✅ Manualmente desde GitHub UI

---

#### **Job 1: Docker (Build & Push)**

**Paso 1: Docker Buildx**
```yaml
- name: Set up Docker Buildx
  uses: docker/setup-buildx-action@v2
```
- Habilita builds multi-plataforma (amd64, arm64)
- Mejora cache de layers

**Paso 2: Login a GHCR**
```yaml
- name: Log in to Container Registry
  uses: docker/login-action@v2
  with:
    registry: ghcr.io
    username: ${{ github.actor }}
    password: ${{ secrets.GITHUB_TOKEN }}
```
- ✅ Autentica con GitHub Container Registry
- ✅ Usa token automático (no necesitas configurar secrets)

**Paso 3: Extract Metadata**
```yaml
- name: Extract metadata
  id: meta
  uses: docker/metadata-action@v4
  with:
    images: ${{ env.REGISTRY }}/${{ env.IMAGE_NAME }}
    tags: |
      type=ref,event=branch      # main → ghcr.io/jdomdev/bizflow:main
      type=semver,pattern={{version}}  # v1.2.3 → ghcr.io/jdomdev/bizflow:1.2.3
      type=sha                    # ghcr.io/jdomdev/bizflow:sha-abc123
      type=raw,value=latest,enable={{is_default_branch}}
```
- ✅ Genera tags automáticos según el evento

**Paso 4: Build & Push**
```yaml
- name: Build and push Docker image
  uses: docker/build-push-action@v4
  with:
    context: .
    push: ${{ github.event_name != 'pull_request' }}
    tags: ${{ steps.meta.outputs.tags }}
    cache-from: type=gha
    cache-to: type=gha,mode=max
```
- ✅ Construye imagen desde raíz del proyecto
- ✅ Solo pushea en push events (NO en PRs)
- ✅ Usa GitHub Actions cache (builds más rápidos)

---

#### **Job 2: Docker Compose Test**

```yaml
docker-compose-test:
  runs-on: ubuntu-latest
  needs: docker  # ⚠️ Solo si docker job tiene éxito
  
  steps:
  - name: Start services with docker-compose
    run: docker-compose up -d
  
  - name: Wait for services to be healthy
    run: |
      timeout 120 bash -c 'until docker exec expense_backend curl -f http://localhost:8080/actuator/health; do sleep 5; done'
  
  - name: Test backend health
    run: curl -f http://localhost:8080/actuator/health || exit 1
  
  - name: Test database connection
    run: docker exec expense_db pg_isready -U postgres || exit 1
  
  - name: Run integration test
    run: |
      curl -X POST http://localhost:8080/api/v1/auth/login \
        -H "Content-Type: application/json" \
        -d '{"email":"admin@example.com","password":"admin123"}' || exit 1
```

**Qué prueba:**
1. ✅ Backend responde en `/actuator/health`
2. ✅ PostgreSQL está disponible (`pg_isready`)
3. ✅ Endpoint de login funciona

**Limitaciones:**
- ⚠️ Solo prueba 1 endpoint (login)
- ⚠️ No prueba CRUD de Employee, Position, Payroll, Expense
- ⚠️ No valida respuestas (solo que no fallan)

---

### 📋 **Tests que SE EJECUTAN en Docker Workflow**

| Test | Tipo | Resultado |
|------|------|-----------|
| Backend health check | Smoke test | ✅ |
| Database connection | Smoke test | ✅ |
| POST /api/v1/auth/login | Integration test | ✅ |

**Total:** 3 tests mínimos (smoke tests)

---

## 3️⃣ Frontend Build & Test (`frontend-build-test.yml`)

#### **Cuándo se ejecuta:**
```yaml
on:
  push:
    branches: [ main, dev, fix/*, feature/* ]
    paths:
      - 'frontend/**'
      - 'package.json'
  pull_request:
    branches: [ main, dev ]
```
- ✅ Solo si hay cambios en `frontend/` o `package.json`

---

#### **Job 1: Build (Matrix Strategy)**

**Matrix:**
```yaml
strategy:
  matrix:
    node-version: ['18.x', '20.x']
```
- ✅ Ejecuta tests en Node 18 y Node 20 (compatibilidad)

**Paso 1: Install**
```yaml
- name: Install dependencies
  run: |
    cd frontend
    npm ci  # ⚠️ npm ci = install desde lock file (determinístico)
```

**Paso 2: Lint**
```yaml
- name: Lint
  run: |
    cd frontend
    npm run lint
  continue-on-error: true  # ⚠️ NO falla el workflow si lint tiene errores
```
- ⚠️ **PROBLEMA**: `continue-on-error: true` significa que ignora errores de linting
- 🟡 **RECOMENDACIÓN**: Cambiar a `false` para forzar código limpio

**Paso 3: Build**
```yaml
- name: Build
  run: |
    cd frontend
    npm run build
```
- ✅ Compila el proyecto React
- ✅ Detecta errores de TypeScript/compilación

**Paso 4: Run Tests**
```yaml
- name: Run Tests
  run: |
    cd frontend
    npm test -- --coverage --watchAll=false
  continue-on-error: true  # ⚠️ TAMBIÉN IGNORA FALLOS
```
- ⚠️ **PROBLEMA**: Tests de frontend pueden fallar y el workflow sigue verde
- 🔴 **CRÍTICO**: Cambiar a `false` para garantizar que tests pasen

**Paso 5: Archive Artifacts**
```yaml
- name: Archive build artifacts
  if: success()
  uses: actions/upload-artifact@v3
  with:
    name: frontend-build-${{ matrix.node-version }}
    path: frontend/dist/

- name: Archive test coverage
  if: always()
  uses: actions/upload-artifact@v3
  with:
    name: frontend-coverage-${{ matrix.node-version }}
    path: frontend/coverage/
```
- ✅ Guarda build final (`dist/`)
- ✅ Guarda cobertura de tests

---

#### **Job 2: Security**

```yaml
security:
  runs-on: ubuntu-latest
  
  steps:
  - name: Run npm audit
    run: |
      cd frontend
      npm audit --audit-level=moderate
    continue-on-error: true
  
  - name: Run Snyk security scan
    run: |
      cd frontend
      npm install -g snyk
      snyk test --all-projects
    env:
      SNYK_TOKEN: ${{ secrets.SNYK_TOKEN }}
    continue-on-error: true
```

**Qué hace:**
- ✅ `npm audit`: Busca vulnerabilidades en dependencias
- ✅ `snyk`: Análisis avanzado de seguridad (requiere cuenta)
- ⚠️ Ambos con `continue-on-error: true` (no bloquean)

---

### 📋 **Tests que SE EJECUTAN en Frontend Workflow**

| Test | Comando | ¿Bloquea CI? |
|------|---------|--------------|
| Linting (ESLint) | `npm run lint` | ❌ NO (`continue-on-error`) |
| Unit tests | `npm test` | ❌ NO (`continue-on-error`) |
| Build | `npm run build` | ✅ SÍ |
| Security audit | `npm audit` | ❌ NO (`continue-on-error`) |
| Snyk scan | `snyk test` | ❌ NO (`continue-on-error`) |

**Conclusión:** Solo el BUILD bloquea el workflow. Tests pueden fallar sin que te enteres.

---

## 🎯 MATRIZ DE COBERTURA ACTUAL

### Backend

| Entidad/Controller | DAO Test | Controller Test | Integration Test (Docker) |
|-------------------|----------|-----------------|---------------------------|
| Employee | ✅ (pero falla en CI) | ❌ FALTA | ❌ FALTA |
| Position | ✅ (pero falla en CI) | ❌ FALTA | ❌ FALTA |
| Payroll | ✅ (pero falla en CI) | ❌ FALTA | ❌ FALTA |
| Expense | ✅ (pero falla en CI) | ❌ FALTA | ❌ FALTA |
| User | ✅ (pero falla en CI) | ✅ EXISTE | ✅ Login test |
| Role | ✅ (pero falla en CI) | ❌ FALTA | ❌ FALTA |

**Cobertura de Controllers:** 1/6 (16.7%)

### Frontend

| Componente | Unit Test | Integration Test | E2E Test |
|------------|-----------|------------------|----------|
| Employee CRUD | ❓ Desconocido | ❌ FALTA | ❌ FALTA |
| Position CRUD | ❓ Desconocido | ❌ FALTA | ❌ FALTA |
| Payroll CRUD | ❓ Desconocido | ❌ FALTA | ❌ FALTA |
| Expense CRUD | ❓ Desconocido | ❌ FALTA | ❌ FALTA |
| Login/Auth | ❓ Desconocido | ❌ FALTA | ✅ (smoke test) |

---

## 🚨 PROBLEMAS IDENTIFICADOS

### 🔴 **CRÍTICO**

1. **Backend tests fallan en GitHub Actions**
   - **Causa:** No hay PostgreSQL service container
   - **Impacto:** 8/10 tests fallan → cobertura 0%
   - **Solución:** Añadir service container (ver sección anterior)

2. **Frontend tests no bloquean CI**
   - **Causa:** `continue-on-error: true`
   - **Impacto:** Código con tests fallidos puede mergearse
   - **Solución:** Cambiar a `continue-on-error: false`

### 🟡 **IMPORTANTE**

3. **Falta cobertura de controllers**
   - **Qué falta:** EmployeeController, PositionController, PayrollController, ExpenseController
   - **Impacto:** No se valida que endpoints HTTP funcionen correctamente
   - **Solución:** Crear tests con `@SpringBootTest` + `MockMvc`

4. **Docker workflow solo prueba 1 endpoint**
   - **Qué falta:** Tests de todos los endpoints CRUD
   - **Impacto:** Cambios pueden romper endpoints sin detectarse
   - **Solución:** Añadir más `curl` tests o usar Postman/Newman

### 🟢 **MEJORAS OPCIONALES**

5. **No hay tests E2E**
   - **Qué falta:** Tests de flujo completo (login → crear employee → listar)
   - **Herramientas sugeridas:** Playwright, Cypress, Selenium
   - **Beneficio:** Detecta problemas de integración frontend-backend

6. **SonarQube solo funciona si tienes cuenta**
   - **Alternativa gratuita:** SonarCloud (para repos públicos)

---

## 📝 PLAN DE ACCIÓN RECOMENDADO

### 🎯 **Prioridad 1: Hacer que tests actuales pasen en CI** (30 min)

**Tarea 1.1: Añadir PostgreSQL a backend-build-test.yml**

```yaml
# En .github/workflows/backend-build-test.yml
jobs:
  build:
    runs-on: ubuntu-latest
    
    services:
      postgres:
        image: postgres:16-alpine
        env:
          POSTGRES_DB: testdb
          POSTGRES_USER: testuser
          POSTGRES_PASSWORD: testpass
        ports:
          - 5434:5432
        options: >-
          --health-cmd pg_isready
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5
```

**Tarea 1.2: Verificar application-test.properties**

```properties
# backend/src/test/resources/application-test.properties
spring.datasource.url=jdbc:postgresql://localhost:5434/testdb
spring.datasource.username=testuser
spring.datasource.password=testpass
spring.jpa.hibernate.ddl-auto=create-drop
```

**Tarea 1.3: Eliminar continue-on-error del frontend**

```yaml
# En .github/workflows/frontend-build-test.yml
- name: Lint
  run: npm run lint
  # ❌ ELIMINAR: continue-on-error: true

- name: Run Tests
  run: npm test -- --coverage --watchAll=false
  # ❌ ELIMINAR: continue-on-error: true
```

**Verificación:**
```bash
git add .github/workflows/
git commit -m "fix: add PostgreSQL service and enforce frontend tests"
git push
# Ir a GitHub Actions y verificar que pasan
```

---

### 🎯 **Prioridad 2: Añadir tests de controllers faltantes** (2-3 horas)

**Tarea 2.1: EmployeeControllerTest**

```java
@SpringBootTest
@AutoConfigureMockMvc
public class EmployeeControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private EmployeeService employeeService;
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetAllEmployees() throws Exception {
        // Mock data
        List<Employee> employees = Arrays.asList(
            new Employee(1L, "John", "Doe", ...),
            new Employee(2L, "Jane", "Smith", ...)
        );
        when(employeeService.findAll()).thenReturn(employees);
        
        // Test
        mockMvc.perform(get("/api/employees"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].name").value("John"));
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetEmployeeById() throws Exception {
        Employee employee = new Employee(1L, "John", "Doe", ...);
        when(employeeService.findById(1L)).thenReturn(Optional.of(employee));
        
        mockMvc.perform(get("/api/employees/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("John"));
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateEmployee() throws Exception {
        String employeeJson = """
            {
                "name": "John",
                "surname": "Doe",
                "email": "john.doe@test.com",
                "position": {"id": 1}
            }
            """;
        
        mockMvc.perform(post("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(employeeJson))
            .andExpect(status().isCreated());
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateEmployee() throws Exception {
        String updatedJson = """
            {
                "name": "John Updated",
                "surname": "Doe",
                "email": "john.updated@test.com",
                "position": {"id": 1}
            }
            """;
        
        mockMvc.perform(put("/api/employees/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedJson))
            .andExpect(status().isOk());
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteEmployee() throws Exception {
        mockMvc.perform(delete("/api/employees/1"))
            .andExpect(status().isNoContent());
    }
    
    @Test
    void testUnauthorizedAccess() throws Exception {
        mockMvc.perform(get("/api/employees"))
            .andExpect(status().isUnauthorized());
    }
}
```

**Repetir para:**
- `PositionControllerTest`
- `PayrollControllerTest`
- `ExpenseControllerTest`

---

### 🎯 **Prioridad 3: Mejorar tests de Docker workflow** (1 hora)

**Opción A: Añadir más curl tests**

```yaml
# En .github/workflows/docker-build-test.yml
- name: Run integration tests
  run: |
    # Login y obtener token
    TOKEN=$(curl -s -X POST http://localhost:8080/api/v1/auth/login \
      -H "Content-Type: application/json" \
      -d '{"email":"admin@example.com","password":"admin123"}' \
      | jq -r '.token')
    
    # Test GET /api/employees
    curl -f -H "Authorization: Bearer $TOKEN" \
      http://localhost:8080/api/employees || exit 1
    
    # Test GET /api/positions
    curl -f -H "Authorization: Bearer $TOKEN" \
      http://localhost:8080/api/positions || exit 1
    
    # Test GET /api/payrolls
    curl -f -H "Authorization: Bearer $TOKEN" \
      http://localhost:8080/api/payrolls || exit 1
    
    # Test GET /api/expenses
    curl -f -H "Authorization: Bearer $TOKEN" \
      http://localhost:8080/api/expenses || exit 1
```

**Opción B: Usar Newman (Postman CLI)**

```yaml
- name: Install Newman
  run: npm install -g newman

- name: Run Postman collection
  run: newman run docs/postman/bizflow_erp_app.postman_collection.json \
    --environment postman-env.json \
    --reporters cli,json \
    --reporter-json-export newman-report.json
```

---

### 🎯 **Prioridad 4: Tests E2E (Opcional)** (4-6 horas)

**Herramienta recomendada:** Playwright

**Setup:**
```bash
cd frontend
npm install --save-dev @playwright/test
npx playwright install
```

**Ejemplo test E2E:**
```typescript
// frontend/e2e/employee-crud.spec.ts
import { test, expect } from '@playwright/test';

test('complete employee CRUD flow', async ({ page }) => {
  // Login
  await page.goto('http://localhost:3000/login');
  await page.fill('[name="email"]', 'admin@example.com');
  await page.fill('[name="password"]', 'admin123');
  await page.click('button[type="submit"]');
  await expect(page).toHaveURL('/dashboard');
  
  // Navigate to Employees
  await page.click('text=Employees');
  await expect(page).toHaveURL('/employees');
  
  // Create employee
  await page.click('text=Add Employee');
  await page.fill('[name="name"]', 'Test User');
  await page.fill('[name="surname"]', 'QA');
  await page.fill('[name="email"]', 'qa@test.com');
  await page.selectOption('[name="position"]', '1');
  await page.click('button[type="submit"]');
  
  // Verify creation
  await expect(page.locator('text=Test User')).toBeVisible();
  
  // Edit employee
  await page.click('text=Test User');
  await page.click('text=Edit');
  await page.fill('[name="name"]', 'Test User Updated');
  await page.click('button[type="submit"]');
  await expect(page.locator('text=Test User Updated')).toBeVisible();
  
  // Delete employee
  await page.click('text=Test User Updated');
  await page.click('text=Delete');
  await page.click('text=Confirm');
  await expect(page.locator('text=Test User Updated')).not.toBeVisible();
});
```

**Workflow para E2E:**
```yaml
# .github/workflows/e2e-tests.yml
name: E2E Tests

on:
  push:
    branches: [ main, dev ]
  pull_request:
    branches: [ main, dev ]

jobs:
  e2e:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Start services
      run: docker-compose up -d
    
    - name: Wait for services
      run: |
        timeout 120 bash -c 'until curl -f http://localhost:8080/actuator/health; do sleep 5; done'
        timeout 120 bash -c 'until curl -f http://localhost:3000; do sleep 5; done'
    
    - name: Install Playwright
      run: |
        cd frontend
        npm ci
        npx playwright install --with-deps
    
    - name: Run E2E tests
      run: |
        cd frontend
        npx playwright test
    
    - name: Upload test results
      if: always()
      uses: actions/upload-artifact@v3
      with:
        name: playwright-report
        path: frontend/playwright-report/
```

---

## 📊 COMPARATIVA: ANTES vs DESPUÉS

### Antes (Estado Actual)

| Aspecto | Cobertura |
|---------|-----------|
| Backend DAO tests en CI | 🔴 0% (fallan sin PostgreSQL) |
| Backend Controller tests | 🟡 16.7% (1/6) |
| Frontend tests en CI | 🟡 Desconocido (no bloquean) |
| Integration tests (Docker) | 🟡 1 endpoint (login) |
| E2E tests | 🔴 0% |

### Después (Con cambios propuestos)

| Aspecto | Cobertura |
|---------|-----------|
| Backend DAO tests en CI | ✅ 100% (con PostgreSQL) |
| Backend Controller tests | ✅ 100% (6/6) |
| Frontend tests en CI | ✅ Aplicado (bloquean CI) |
| Integration tests (Docker) | ✅ 5+ endpoints |
| E2E tests | ✅ Flujos críticos |

---

## ✅ CHECKLIST DE IMPLEMENTACIÓN

### Fase 1: Estabilizar CI (CRÍTICO)
- [ ] Añadir PostgreSQL service a `backend-build-test.yml`
- [ ] Verificar `application-test.properties`
- [ ] Eliminar `continue-on-error` de frontend workflow
- [ ] Push y verificar que workflows pasan
- [ ] Revisar reportes de cobertura en Codecov

### Fase 2: Tests de Controllers (IMPORTANTE)
- [ ] Crear `EmployeeControllerTest.java`
- [ ] Crear `PositionControllerTest.java`
- [ ] Crear `PayrollControllerTest.java`
- [ ] Crear `ExpenseControllerTest.java`
- [ ] Ejecutar localmente: `mvn test`
- [ ] Verificar en GitHub Actions

### Fase 3: Mejorar Integration Tests (IMPORTANTE)
- [ ] Decidir: curl extendido vs Newman
- [ ] Implementar tests de todos los endpoints CRUD
- [ ] Añadir validaciones de respuestas (no solo status codes)
- [ ] Documentar en README

### Fase 4: E2E Tests (OPCIONAL)
- [ ] Instalar Playwright
- [ ] Crear test de login flow
- [ ] Crear test de employee CRUD
- [ ] Crear test de payroll flow
- [ ] Crear workflow `e2e-tests.yml`
- [ ] Configurar parallel execution

---

## 📚 RECURSOS Y DOCUMENTACIÓN

### Spring Boot Testing
- [Spring Boot Test Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing)
- [@DataJpaTest](https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/test/autoconfigure/orm/jpa/DataJpaTest.html)
- [@SpringBootTest](https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/test/context/SpringBootTest.html)
- [MockMvc](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/test/web/servlet/MockMvc.html)

### GitHub Actions
- [Service Containers](https://docs.github.com/en/actions/using-containerized-services/about-service-containers)
- [PostgreSQL Service Example](https://docs.github.com/en/actions/using-containerized-services/creating-postgresql-service-containers)

### Frontend Testing
- [Playwright](https://playwright.dev/)
- [Cypress](https://www.cypress.io/)
- [React Testing Library](https://testing-library.com/docs/react-testing-library/intro/)

### Tools
- [Newman (Postman CLI)](https://github.com/postmanlabs/newman)
- [Codecov](https://about.codecov.io/)
- [SonarCloud](https://sonarcloud.io/)

---

**Última actualización:** 16 de diciembre de 2025  
**Autor:** GitHub Copilot  
**Estado:** Documento de análisis y planificación
