# Testing

Guía para ejecutar tests en Bizflow ERP.

## Backend (JUnit + Spring Boot Test)

### Ejecutar todos los tests

```bash
cd backend

# Con Maven wrapper
./mvnw test

# Con perfil de test
./mvnw test -Dspring.profiles.active=test
```

### Ejecutar test específico

```bash
# Una clase
./mvnw test -Dtest=ExpenseServiceTest

# Un método
./mvnw test -Dtest=ExpenseServiceTest#testCreateExpense
```

### Ejecutar con Docker

```bash
# Iniciar entorno de test
docker compose --profile test up -d

# Ejecutar tests
docker compose exec backend-test ./mvnw test
```

### Coverage Report

```bash
./mvnw test jacoco:report
```

El reporte estará en `backend/target/site/jacoco/index.html`

## Frontend (Vitest)

### Ejecutar tests

```bash
cd frontend

# Todos los tests
npm test

# Watch mode
npm run test:watch

# Con coverage
npm run test:coverage
```

### Test específico

```bash
npm test -- ExpenseList.test.jsx
```

## Tests E2E (Playwright)

::: info En desarrollo
Los tests E2E con Playwright están planificados.
:::

```bash
# Instalación
cd frontend
npm install -D @playwright/test
npx playwright install

# Ejecutar
npx playwright test
```

## Testing Manual por Rol

Para verificar el sistema completo, sigue los checklists por rol:

- [Checklist ADMIN](/guide/roles/admin#checklist-de-testing)
- [Checklist MANAGER](/guide/roles/manager#checklist-de-testing)
- [Checklist USER](/guide/roles/user#checklist-de-testing)

### Usuarios de prueba

| Usuario | Rol | Email |
|---------|-----|-------|
| Ada Lovelace | ADMIN | `ada.lovelace@bizflowerp.com` |
| Nikola Tesla | MANAGER | `nikola.tesla@bizflowerp.com` |
| Ken Thompson | USER | `ken.thompson@bizflowerp.com` |

Contraseña: `password123`

## Estructura de Tests

```
backend/src/test/
├── java/io/sunbit/app/
│   ├── controller/
│   │   ├── ExpenseControllerTest.java
│   │   └── PayrollControllerTest.java
│   ├── service/
│   │   ├── ExpenseServiceTest.java
│   │   └── PayrollServiceTest.java
│   └── security/
│       └── JwtAuthenticationTest.java
└── resources/
    ├── application-test.properties
    └── data-test.sql

frontend/src/
├── components/
│   └── __tests__/
│       └── ExpenseList.test.jsx
└── pages/
    └── __tests__/
        └── DashboardPage.test.jsx
```

## Mocking

### Backend - Mockito

```java
@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {
    
    @Mock
    private IExpenseDao expenseDao;
    
    @InjectMocks
    private ExpenseServiceImpl expenseService;
    
    @Test
    void shouldReturnAllExpenses() {
        when(expenseDao.findAll()).thenReturn(mockExpenses);
        List<Expense> result = expenseService.findAll();
        assertEquals(2, result.size());
    }
}
```

### Frontend - Vitest

```javascript
import { vi } from 'vitest'
import { render, screen } from '@testing-library/react'
import ExpenseList from '../ExpenseList'

vi.mock('../../services/api', () => ({
  getExpenses: vi.fn(() => Promise.resolve([]))
}))

test('renders expense list', async () => {
  render(<ExpenseList />)
  expect(screen.getByText('Loading...')).toBeInTheDocument()
})
```
