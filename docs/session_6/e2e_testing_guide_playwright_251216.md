# Prioridad 4: Tests E2E con Playwright - Guía de Implementación

**Fecha:** 16 de Diciembre de 2025  
**Estado:** OPCIONAL - Implementación futura  
**Tiempo Estimado:** 4-6 horas

---

## ¿Por qué E2E Tests?

Los tests E2E (End-to-End) simulan el comportamiento real de un usuario, probando:
- Interacción completa Frontend ↔ Backend ↔ Database
- Flujos de usuario reales (login → navegar → crear → editar → borrar)
- Renders del DOM, JavaScript, CSS
- Comportamiento del navegador real

**Complementan** (no reemplazan) los tests unitarios y de integración.

---

## Setup Inicial

### 1. Instalar Playwright

```bash
cd frontend
npm install --save-dev @playwright/test
npx playwright install
npx playwright install-deps  # Instala dependencias del sistema
```

### 2. Configurar Playwright

Crear `frontend/playwright.config.ts`:

```typescript
import { defineConfig, devices } from '@playwright/test';

export default defineConfig({
  testDir: './e2e',
  fullyParallel: true,
  forbidOnly: !!process.env.CI,
  retries: process.env.CI ? 2 : 0,
  workers: process.env.CI ? 1 : undefined,
  reporter: 'html',
  
  use: {
    baseURL: 'http://localhost:3000',
    trace: 'on-first-retry',
    screenshot: 'only-on-failure',
  },

  projects: [
    {
      name: 'chromium',
      use: { ...devices['Desktop Chrome'] },
    },
    {
      name: 'firefox',
      use: { ...devices['Desktop Firefox'] },
    },
    {
      name: 'webkit',
      use: { ...devices['Desktop Safari'] },
    },
  ],

  webServer: {
    command: 'npm run dev',
    url: 'http://localhost:3000',
    reuseExistingServer: !process.env.CI,
  },
});
```

### 3. Crear estructura de carpetas

```bash
mkdir -p frontend/e2e
mkdir -p frontend/e2e/fixtures
mkdir -p frontend/e2e/helpers
```

---

## Ejemplos de Tests E2E

### Test 1: Login Flow

`frontend/e2e/auth.spec.ts`:

```typescript
import { test, expect } from '@playwright/test';

test.describe('Authentication', () => {
  test('should login successfully with valid credentials', async ({ page }) => {
    // Navigate to login page
    await page.goto('/login');
    
    // Fill login form
    await page.fill('input[name="email"]', 'admin@example.com');
    await page.fill('input[name="password"]', 'admin123');
    
    // Click login button
    await page.click('button[type="submit"]');
    
    // Wait for navigation to dashboard
    await expect(page).toHaveURL(/.*dashboard/);
    
    // Verify welcome message or user menu
    await expect(page.locator('text=Welcome')).toBeVisible();
  });

  test('should show error with invalid credentials', async ({ page }) => {
    await page.goto('/login');
    
    await page.fill('input[name="email"]', 'wrong@example.com');
    await page.fill('input[name="password"]', 'wrongpassword');
    await page.click('button[type="submit"]');
    
    // Should stay on login page
    await expect(page).toHaveURL(/.*login/);
    
    // Should show error message
    await expect(page.locator('text=/invalid|error|incorrect/i')).toBeVisible();
  });

  test('should logout successfully', async ({ page }) => {
    // Login first
    await page.goto('/login');
    await page.fill('input[name="email"]', 'admin@example.com');
    await page.fill('input[name="password"]', 'admin123');
    await page.click('button[type="submit"]');
    await expect(page).toHaveURL(/.*dashboard/);
    
    // Logout
    await page.click('button:has-text("Logout")');
    
    // Should redirect to login
    await expect(page).toHaveURL(/.*login/);
  });
});
```

---

### Test 2: Employee CRUD Flow

`frontend/e2e/employee-crud.spec.ts`:

```typescript
import { test, expect } from '@playwright/test';

// Helper function to login before each test
async function login(page) {
  await page.goto('/login');
  await page.fill('input[name="email"]', 'admin@example.com');
  await page.fill('input[name="password"]', 'admin123');
  await page.click('button[type="submit"]');
  await expect(page).toHaveURL(/.*dashboard/);
}

test.describe('Employee CRUD', () => {
  test.beforeEach(async ({ page }) => {
    await login(page);
  });

  test('should create a new employee', async ({ page }) => {
    // Navigate to employees page
    await page.click('a:has-text("Employees")');
    await expect(page).toHaveURL(/.*employees/);
    
    // Click "Add Employee" button
    await page.click('button:has-text("Add Employee")');
    
    // Fill form
    await page.fill('input[name="name"]', 'Test E2E');
    await page.fill('input[name="surname"]', 'Playwright');
    await page.fill('input[name="email"]', 'e2e.playwright@test.com');
    await page.fill('input[name="birthDate"]', '1990-01-01');
    await page.selectOption('select[name="position"]', { label: 'Software Engineer' });
    
    // Submit form
    await page.click('button[type="submit"]');
    
    // Verify success message
    await expect(page.locator('text=/success|created/i')).toBeVisible();
    
    // Verify employee appears in list
    await expect(page.locator('text=Test E2E Playwright')).toBeVisible();
  });

  test('should edit an existing employee', async ({ page }) => {
    // Go to employees
    await page.click('a:has-text("Employees")');
    
    // Search for employee (assuming search functionality exists)
    await page.fill('input[placeholder*="Search"]', 'Test E2E');
    
    // Click edit button
    await page.click('tr:has-text("Test E2E") button:has-text("Edit")');
    
    // Update name
    await page.fill('input[name="name"]', 'Test E2E Updated');
    await page.click('button[type="submit"]');
    
    // Verify update
    await expect(page.locator('text=Test E2E Updated')).toBeVisible();
  });

  test('should view employee details', async ({ page }) => {
    await page.click('a:has-text("Employees")');
    
    // Click on employee name to view details
    await page.click('tr:has-text("Test E2E") a');
    
    // Verify details page
    await expect(page.locator('h1:has-text("Employee Details")')).toBeVisible();
    await expect(page.locator('text=e2e.playwright@test.com')).toBeVisible();
  });

  test('should delete an employee', async ({ page }) => {
    await page.click('a:has-text("Employees")');
    
    // Click delete button
    await page.click('tr:has-text("Test E2E") button:has-text("Delete")');
    
    // Confirm deletion in modal
    await page.click('button:has-text("Confirm")');
    
    // Verify employee is gone
    await expect(page.locator('text=Test E2E Updated')).not.toBeVisible();
    
    // Verify success message
    await expect(page.locator('text=/deleted|removed/i')).toBeVisible();
  });
});
```

---

### Test 3: Complete Payroll Flow

`frontend/e2e/payroll-flow.spec.ts`:

```typescript
import { test, expect } from '@playwright/test';

test.describe('Payroll Management', () => {
  test('complete payroll creation flow', async ({ page }) => {
    // Login
    await page.goto('/login');
    await page.fill('input[name="email"]', 'admin@example.com');
    await page.fill('input[name="password"]', 'admin123');
    await page.click('button[type="submit"]');
    
    // Navigate to payrolls
    await page.click('a:has-text("Payrolls")');
    await expect(page).toHaveURL(/.*payroll/);
    
    // Create new payroll
    await page.click('button:has-text("New Payroll")');
    
    // Select employee
    await page.selectOption('select[name="employee"]', { index: 1 });
    
    // Fill amount and date
    await page.fill('input[name="amount"]', '3500.00');
    await page.fill('input[name="paymentDate"]', '2025-12-16');
    
    // Submit
    await page.click('button[type="submit"]');
    
    // Verify in list
    await expect(page.locator('text=3500.00')).toBeVisible();
  });
});
```

---

## Helper Functions

`frontend/e2e/helpers/auth.ts`:

```typescript
import { Page } from '@playwright/test';

export async function loginAsAdmin(page: Page) {
  await page.goto('/login');
  await page.fill('input[name="email"]', 'admin@example.com');
  await page.fill('input[name="password"]', 'admin123');
  await page.click('button[type="submit"]');
  await page.waitForURL(/.*dashboard/);
}

export async function loginAsUser(page: Page) {
  await page.goto('/login');
  await page.fill('input[name="email"]', 'user@example.com');
  await page.fill('input[name="password"]', 'user123');
  await page.click('button[type="submit"]');
  await page.waitForURL(/.*dashboard/);
}
```

`frontend/e2e/helpers/navigation.ts`:

```typescript
import { Page } from '@playwright/test';

export async function navigateTo(page: Page, section: string) {
  await page.click(`a:has-text("${section}")`);
}
```

---

## Fixtures (Datos de Test)

`frontend/e2e/fixtures/employees.json`:

```json
{
  "testEmployee": {
    "name": "E2E Test",
    "surname": "User",
    "email": "e2e.test@example.com",
    "birthDate": "1990-01-01",
    "position": "Software Engineer"
  },
  "adminEmployee": {
    "name": "Admin",
    "surname": "User",
    "email": "admin@example.com",
    "birthDate": "1985-05-15",
    "position": "CTO"
  }
}
```

---

## GitHub Actions Workflow para E2E

`.github/workflows/e2e-tests.yml`:

```yaml
name: E2E Tests

on:
  push:
    branches: [ main, dev ]
  pull_request:
    branches: [ main, dev ]
  workflow_dispatch:

jobs:
  e2e-tests:
    runs-on: ubuntu-latest
    timeout-minutes: 15
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Start backend services
      run: |
        docker-compose up -d expense_db expense_backend
        sleep 10
    
    - name: Wait for backend to be ready
      run: |
        timeout 120 bash -c 'until curl -f http://localhost:8080/actuator/health; do sleep 5; done'
    
    - name: Setup Node.js
      uses: actions/setup-node@v3
      with:
        node-version: '20.x'
        cache: 'npm'
        cache-dependency-path: frontend/package-lock.json
    
    - name: Install frontend dependencies
      run: |
        cd frontend
        npm ci
    
    - name: Install Playwright browsers
      run: |
        cd frontend
        npx playwright install --with-deps chromium
    
    - name: Run E2E tests
      run: |
        cd frontend
        npx playwright test --project=chromium
      env:
        CI: true
    
    - name: Upload Playwright report
      if: always()
      uses: actions/upload-artifact@v3
      with:
        name: playwright-report
        path: frontend/playwright-report/
        retention-days: 7
    
    - name: Upload test results
      if: always()
      uses: actions/upload-artifact@v3
      with:
        name: playwright-results
        path: frontend/test-results/
        retention-days: 7
    
    - name: Cleanup
      if: always()
      run: docker-compose down -v
```

---

## Comandos Útiles

### Ejecutar todos los tests E2E
```bash
cd frontend
npx playwright test
```

### Ejecutar un test específico
```bash
npx playwright test e2e/auth.spec.ts
```

### Ejecutar en modo headed (ver navegador)
```bash
npx playwright test --headed
```

### Ejecutar en modo debug
```bash
npx playwright test --debug
```

### Ver reporte HTML
```bash
npx playwright show-report
```

### Generar código de test (grabador)
```bash
npx playwright codegen http://localhost:3000
```

---

## Best Practices

### 1. **Page Object Model (POM)**

`frontend/e2e/pages/LoginPage.ts`:

```typescript
import { Page } from '@playwright/test';

export class LoginPage {
  constructor(private page: Page) {}

  async goto() {
    await this.page.goto('/login');
  }

  async login(email: string, password: string) {
    await this.page.fill('input[name="email"]', email);
    await this.page.fill('input[name="password"]', password);
    await this.page.click('button[type="submit"]');
  }

  async getErrorMessage() {
    return this.page.locator('.error-message').textContent();
  }
}
```

Uso:
```typescript
import { LoginPage } from './pages/LoginPage';

test('login with POM', async ({ page }) => {
  const loginPage = new LoginPage(page);
  await loginPage.goto();
  await loginPage.login('admin@example.com', 'admin123');
  await expect(page).toHaveURL(/.*dashboard/);
});
```

### 2. **Usar data-testid para selectores estables**

HTML:
```html
<button data-testid="login-button" type="submit">Login</button>
```

Test:
```typescript
await page.click('[data-testid="login-button"]');
```

### 3. **Evitar sleeps, usar waitFor**

❌ Malo:
```typescript
await page.click('button');
await page.waitForTimeout(5000);  // Anti-pattern
```

✅ Bueno:
```typescript
await page.click('button');
await page.waitForSelector('.success-message');
```

### 4. **Limpiar datos después de cada test**

```typescript
test.afterEach(async ({ page }) => {
  // Cleanup: eliminar datos de test
  await page.request.delete('/api/v1/employee/test-user-id');
});
```

---

## Ventajas de Playwright vs Otras Herramientas

| Característica | Playwright | Cypress | Selenium |
|----------------|-----------|---------|----------|
| Velocidad | ⚡⚡⚡ Muy rápido | ⚡⚡ Rápido | ⚡ Lento |
| Multi-browser | ✅ Chrome, Firefox, Safari | ⚠️ Solo Chrome, Edge | ✅ Todos |
| Auto-wait | ✅ Automático | ✅ Automático | ❌ Manual |
| API Testing | ✅ Integrado | ⚠️ Limitado | ❌ No |
| Screenshot/Video | ✅ Automático | ✅ Automático | ⚠️ Manual |
| TypeScript | ✅ Nativo | ✅ Soportado | ⚠️ Limitado |
| Parallel tests | ✅ Fácil | ⚠️ Con config | ✅ Con Grid |

---

## Métricas de Cobertura E2E

### Coverage Mínimo Recomendado

- **Happy paths:** 100% (flujos principales funcionan)
- **Error handling:** 80% (validaciones y errores comunes)
- **Edge cases:** 50% (casos raros)

### Flujos Críticos para BizFlow ERP

1. ✅ Login/Logout
2. ✅ Employee CRUD
3. ✅ Position management
4. ✅ Payroll creation
5. ✅ Expense tracking
6. ⚠️ Reports generation (si existe)
7. ⚠️ User management (admin panel)

---

## Integración con CI/CD

El workflow E2E debería:
1. ✅ Ejecutarse en PRs contra `main` y `dev`
2. ✅ Ejecutarse en push a `main` (pre-deploy)
3. ⚠️ Opcional en push a feature branches (puede ser lento)
4. ✅ Generar reportes HTML con screenshots de fallos
5. ✅ Fallar el build si cualquier test E2E falla

---

## Estimación de Tiempo

| Tarea | Tiempo |
|-------|--------|
| Setup inicial Playwright | 30 min |
| Test de login/auth | 1 hora |
| Test Employee CRUD completo | 2 horas |
| Tests de Position, Payroll, Expense | 2 horas |
| Page Object Model setup | 1 hora |
| Workflow de GitHub Actions | 30 min |
| Debug y ajustes | 1-2 horas |
| **TOTAL** | **7-8 horas** |

---

## ¿Cuándo Implementar?

### ✅ Implementar Ahora Si:
- El frontend tiene flujos críticos complejos
- Tienes bugs frecuentes que no detectan los unit tests
- Vas a deploy frecuente y necesitas confianza

### ⏸️ Posponer Si:
- El frontend aún está en desarrollo activo
- Los unit tests y integration tests cubren bien
- Recursos limitados (priorizar otros tests primero)

---

## Próximos Pasos (Si decides implementar)

1. [ ] Ejecutar `npm install --save-dev @playwright/test`
2. [ ] Crear `playwright.config.ts`
3. [ ] Escribir primer test de login
4. [ ] Ejecutar localmente y verificar que pasa
5. [ ] Crear workflow de GitHub Actions
6. [ ] Expandir cobertura a CRUD completo
7. [ ] Documentar en README principal

---

**Nota Final:** Esta es la Prioridad 4 (OPCIONAL). Las Prioridades 1-3 ya están completadas y son suficientes para tener una batería de tests robusta. Los tests E2E son el "cherry on top" para máxima confianza antes de deploys.

**Última actualización:** 16 de diciembre de 2025  
**Estado:** Guía completa para implementación futura
