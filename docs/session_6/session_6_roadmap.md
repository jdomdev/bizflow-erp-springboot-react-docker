# Session 6 Roadmap: Dashboard y Gestión de Gastos

**Estado Actual:** Aplicación con autenticación completa, dashboard vacío  
**Objetivo:** Implementar dashboard, perfiles de usuario, y gestión de gastos  
**Duración Estimada:** 8-10 horas de desarrollo

---

## 📋 Tabla de Contenidos

1. [Visión General](#visión-general)
2. [Fases de Implementación](#fases-de-implementación)
3. [Especificaciones Técnicas](#especificaciones-técnicas)
4. [Testing Plan](#testing-plan)
5. [Riesgos y Mitigaciones](#riesgos-y-mitigaciones)

---

## 🎯 Visión General

### Estado Actual (Fin de Session 5)
```
✅ Autenticación funcional (Signup + Login)
✅ 7 usuarios de test con diferentes roles
✅ Frontend servido por Nginx
✅ Backend Spring Boot 6.1 con Security
❌ Dashboard vacío/no implementado
❌ Sin endpoints para gastos
❌ Sin endpoints para nómina
❌ Sin endpoints para empleados
```

### Estado Objetivo (Fin de Session 6)
```
✅ Dashboard con información del usuario
✅ Perfil de usuario editable
✅ Endpoints CRUD de gastos
✅ Frontend para crear/listar/editar gastos
✅ Autorización basada en roles
✅ Logout funcional
```

---

## 📅 Fases de Implementación

### FASE 1: Dashboard y Navegación (2-3 horas)

#### 1.1 Backend - Endpoint de Datos del Usuario

**Archivo:** `UserControllerImpl.java` (nuevo)

```java
@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserControllerImpl {
    
    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getProfile(Authentication authentication) {
        String email = authentication.getName();
        // Retornar información del usuario actual
        return ResponseEntity.ok(userService.findByEmail(email));
    }
    
    @PutMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateProfile(
        @RequestBody UserUpdateRequest request,
        Authentication authentication
    ) {
        String email = authentication.getName();
        // Actualizar perfil del usuario
        return ResponseEntity.ok(userService.update(email, request));
    }
    
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // Endpoint para limpiar token en cliente
        return ResponseEntity.ok().build();
    }
}
```

**DTOs Necesarios:**
- `UserProfileResponse` - Información del usuario
- `UserUpdateRequest` - Campos actualizables (email, password)

---

#### 1.2 Frontend - Componentes de Dashboard

**Archivo:** `DashboardPage.jsx` (actualizado)

```jsx
import { useAuth } from '../hooks/useAuth'
import { useNavigate } from 'react-router-dom'
import { useState, useEffect } from 'react'

export default function DashboardPage() {
  const { user, logout } = useAuth()
  const navigate = useNavigate()
  const [profile, setProfile] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    // Cargar perfil del usuario
    fetchUserProfile()
  }, [])

  const fetchUserProfile = async () => {
    try {
      const response = await apiClient.get('/users/profile')
      setProfile(response.data)
    } catch (error) {
      console.error('Error loading profile:', error)
    } finally {
      setLoading(false)
    }
  }

  if (loading) return <div>Cargando...</div>

  return (
    <div className="dashboard">
      <nav className="sidebar">
        <div className="user-info">
          <h2>Hola, {profile?.email}</h2>
          <p>{profile?.roles?.join(', ')}</p>
        </div>
        
        <ul className="nav-menu">
          <li><a href="/dashboard">Dashboard</a></li>
          <li><a href="/expenses">Gastos</a></li>
          <li><a href="/profile">Perfil</a></li>
          <li><button onClick={logout}>Logout</button></li>
        </ul>
      </nav>

      <main className="content">
        <h1>Dashboard</h1>
        
        {/* Mostrar información según rol */}
        {profile?.roles?.includes('ADMIN') && (
          <section>
            <h2>Administración</h2>
            {/* Opciones de admin */}
          </section>
        )}
        
        {profile?.roles?.includes('MANAGER') && (
          <section>
            <h2>Gestión de Equipo</h2>
            {/* Opciones de manager */}
          </section>
        )}
        
        <section>
          <h2>Mis Gastos</h2>
          {/* Resumen de gastos */}
        </section>
      </main>
    </div>
  )
}
```

---

#### 1.3 Frontend - Página de Perfil

**Archivo:** `ProfilePage.jsx` (nuevo)

```jsx
import { useState, useEffect } from 'react'
import { useAuth } from '../hooks/useAuth'
import { useNavigate } from 'react-router-dom'

export default function ProfilePage() {
  const { user } = useAuth()
  const navigate = useNavigate()
  const [profile, setProfile] = useState(null)
  const [editing, setEditing] = useState(false)
  const [formData, setFormData] = useState({})

  useEffect(() => {
    fetchProfile()
  }, [])

  const fetchProfile = async () => {
    try {
      const response = await apiClient.get('/users/profile')
      setProfile(response.data)
      setFormData(response.data)
    } catch (error) {
      console.error('Error:', error)
    }
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    try {
      await apiClient.put('/users/profile', formData)
      setEditing(false)
      fetchProfile()
    } catch (error) {
      console.error('Error updating profile:', error)
    }
  }

  if (!profile) return <div>Cargando...</div>

  return (
    <div className="profile-page">
      <h1>Mi Perfil</h1>
      
      {editing ? (
        <form onSubmit={handleSubmit}>
          <input
            type="email"
            value={formData.email}
            onChange={(e) => setFormData({...formData, email: e.target.value})}
          />
          <button type="submit">Guardar</button>
          <button onClick={() => setEditing(false)}>Cancelar</button>
        </form>
      ) : (
        <div>
          <p><strong>Email:</strong> {profile.email}</p>
          <p><strong>Roles:</strong> {profile.roles?.join(', ')}</p>
          <button onClick={() => setEditing(true)}>Editar</button>
        </div>
      )}
    </div>
  )
}
```

---

#### 1.4 Routing Frontend

**Archivo:** `src/App.jsx` - Actualizar Router

```jsx
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom'
import PrivateRoute from './components/PrivateRoute'
import LoginPage from './pages/LoginPage'
import SignupPage from './pages/SignupPage'
import DashboardPage from './pages/DashboardPage'
import ProfilePage from './pages/ProfilePage'
import ExpensesPage from './pages/ExpensesPage'

export default function App() {
  return (
    <Router>
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/signup" element={<SignupPage />} />
        
        <Route element={<PrivateRoute />}>
          <Route path="/dashboard" element={<DashboardPage />} />
          <Route path="/profile" element={<ProfilePage />} />
          <Route path="/expenses" element={<ExpensesPage />} />
        </Route>

        <Route path="/" element={<Navigate to="/dashboard" />} />
      </Routes>
    </Router>
  )
}
```

---

### FASE 2: Gestión de Gastos (3-4 horas)

#### 2.1 Backend - Entity y DAO

**Archivo:** `Expense.java` - Entidad (VERIFICAR/ACTUALIZAR)

```java
@Entity
@Table(name = "expense")
@Data
@NoArgsConstructor
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    private ExpenseCategory category; // TRAVEL, FOOD, OFFICE, OTHER

    @ManyToOne
    @JoinColumn(name = "user_id")
    private ExpenseUser user;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
```

**Archivo:** `IExpenseDao.java`

```java
public interface IExpenseDao {
    List<Expense> findByUserId(Long userId);
    List<Expense> findByDateBetween(LocalDate start, LocalDate end);
    Optional<Expense> findById(Long id);
    Expense save(Expense expense);
    void delete(Long id);
}
```

**Archivo:** `ExpenseDaoImpl.java`

```java
@Repository
public class ExpenseDaoImpl implements IExpenseDao {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Expense> findByUserId(Long userId) {
        return entityManager.createQuery(
            "FROM Expense WHERE user.id = :userId ORDER BY date DESC",
            Expense.class
        ).setParameter("userId", userId).getResultList();
    }

    @Override
    public List<Expense> findByDateBetween(LocalDate start, LocalDate end) {
        return entityManager.createQuery(
            "FROM Expense WHERE date BETWEEN :start AND :end ORDER BY date DESC",
            Expense.class
        ).setParameter("start", start)
         .setParameter("end", end)
         .getResultList();
    }

    // ... otros métodos
}
```

---

#### 2.2 Backend - Service

**Archivo:** `IExpenseService.java`

```java
public interface IExpenseService {
    List<Expense> getUserExpenses(Long userId);
    List<Expense> getExpensesByDateRange(LocalDate start, LocalDate end);
    Expense createExpense(CreateExpenseRequest request, Long userId);
    Expense updateExpense(Long id, UpdateExpenseRequest request, Long userId);
    void deleteExpense(Long id, Long userId);
    Expense getExpense(Long id);
}
```

**Archivo:** `ExpenseServiceImpl.java`

```java
@Service
@Transactional
public class ExpenseServiceImpl implements IExpenseService {
    
    @Autowired
    private IExpenseDao expenseDao;
    
    @Autowired
    private UserService userService;

    @Override
    public List<Expense> getUserExpenses(Long userId) {
        return expenseDao.findByUserId(userId);
    }

    @Override
    public Expense createExpense(CreateExpenseRequest request, Long userId) {
        ExpenseUser user = userService.findById(userId);
        
        Expense expense = new Expense();
        expense.setDescription(request.getDescription());
        expense.setAmount(request.getAmount());
        expense.setDate(request.getDate());
        expense.setCategory(request.getCategory());
        expense.setUser(user);
        
        return expenseDao.save(expense);
    }

    @Override
    public Expense updateExpense(Long id, UpdateExpenseRequest request, Long userId) {
        Expense expense = getExpense(id);
        
        // Verificar que el gasto pertenece al usuario actual
        if (!expense.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("No permission to update this expense");
        }
        
        expense.setDescription(request.getDescription());
        expense.setAmount(request.getAmount());
        expense.setDate(request.getDate());
        expense.setCategory(request.getCategory());
        
        return expenseDao.save(expense);
    }

    // ... otros métodos
}
```

---

#### 2.3 Backend - Controller

**Archivo:** `ExpenseControllerImpl.java` (ACTUALIZAR)

```java
@RestController
@RequestMapping("/api/v1/expenses")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ExpenseControllerImpl {
    
    @Autowired
    private IExpenseService expenseService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getExpenses(Authentication authentication) {
        String email = authentication.getName();
        ExpenseUser user = (ExpenseUser) authentication.getPrincipal();
        
        List<Expense> expenses = expenseService.getUserExpenses(user.getId());
        return ResponseEntity.ok(expenses);
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createExpense(
        @Valid @RequestBody CreateExpenseRequest request,
        Authentication authentication
    ) {
        ExpenseUser user = (ExpenseUser) authentication.getPrincipal();
        Expense expense = expenseService.createExpense(request, user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(expense);
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateExpense(
        @PathVariable Long id,
        @Valid @RequestBody UpdateExpenseRequest request,
        Authentication authentication
    ) {
        ExpenseUser user = (ExpenseUser) authentication.getPrincipal();
        Expense expense = expenseService.updateExpense(id, request, user.getId());
        return ResponseEntity.ok(expense);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteExpense(
        @PathVariable Long id,
        Authentication authentication
    ) {
        ExpenseUser user = (ExpenseUser) authentication.getPrincipal();
        expenseService.deleteExpense(id, user.getId());
        return ResponseEntity.noContent().build();
    }
}
```

---

#### 2.4 DTOs

**Archivo:** `CreateExpenseRequest.java`

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateExpenseRequest {
    
    @NotBlank(message = "Description is required")
    private String description;
    
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;
    
    @NotNull(message = "Date is required")
    private LocalDate date;
    
    @NotNull(message = "Category is required")
    private ExpenseCategory category;
}
```

---

#### 2.5 Frontend - Página de Gastos

**Archivo:** `ExpensesPage.jsx`

```jsx
import { useState, useEffect } from 'react'
import { useAuth } from '../hooks/useAuth'
import ExpenseForm from '../components/ExpenseForm'
import ExpenseList from '../components/ExpenseList'
import '../styles/expenses.css'

export default function ExpensesPage() {
  const { user } = useAuth()
  const [expenses, setExpenses] = useState([])
  const [loading, setLoading] = useState(true)
  const [showForm, setShowForm] = useState(false)

  useEffect(() => {
    fetchExpenses()
  }, [])

  const fetchExpenses = async () => {
    try {
      const response = await apiClient.get('/expenses')
      setExpenses(response.data)
    } catch (error) {
      console.error('Error loading expenses:', error)
    } finally {
      setLoading(false)
    }
  }

  const handleExpenseCreated = (newExpense) => {
    setExpenses([newExpense, ...expenses])
    setShowForm(false)
  }

  const handleExpenseDeleted = (id) => {
    setExpenses(expenses.filter(e => e.id !== id))
  }

  if (loading) return <div>Cargando gastos...</div>

  return (
    <div className="expenses-page">
      <h1>Mis Gastos</h1>
      
      <button 
        className="btn-primary"
        onClick={() => setShowForm(!showForm)}
      >
        + Nuevo Gasto
      </button>

      {showForm && (
        <ExpenseForm 
          onSuccess={handleExpenseCreated}
          onCancel={() => setShowForm(false)}
        />
      )}

      <ExpenseList 
        expenses={expenses}
        onDelete={handleExpenseDeleted}
      />
    </div>
  )
}
```

**Archivo:** `components/ExpenseForm.jsx`

```jsx
import { useState } from 'react'
import apiClient from '../api/apiClient'

export default function ExpenseForm({ onSuccess, onCancel }) {
  const [formData, setFormData] = useState({
    description: '',
    amount: '',
    date: new Date().toISOString().split('T')[0],
    category: 'OTHER'
  })
  const [error, setError] = useState('')

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    
    try {
      const response = await apiClient.post('/expenses', formData)
      onSuccess(response.data)
    } catch (err) {
      setError(err.response?.data?.message || 'Error creating expense')
    }
  }

  return (
    <form className="expense-form" onSubmit={handleSubmit}>
      {error && <div className="error">{error}</div>}
      
      <input
        type="text"
        placeholder="Descripción"
        value={formData.description}
        onChange={(e) => setFormData({...formData, description: e.target.value})}
        required
      />
      
      <input
        type="number"
        placeholder="Monto"
        value={formData.amount}
        onChange={(e) => setFormData({...formData, amount: e.target.value})}
        required
      />
      
      <input
        type="date"
        value={formData.date}
        onChange={(e) => setFormData({...formData, date: e.target.value})}
        required
      />
      
      <select
        value={formData.category}
        onChange={(e) => setFormData({...formData, category: e.target.value})}
      >
        <option value="TRAVEL">Viaje</option>
        <option value="FOOD">Comida</option>
        <option value="OFFICE">Oficina</option>
        <option value="OTHER">Otro</option>
      </select>
      
      <button type="submit">Crear Gasto</button>
      <button type="button" onClick={onCancel}>Cancelar</button>
    </form>
  )
}
```

---

### FASE 3: Autorización y RBAC (1-2 horas)

#### 3.1 Configurar @PreAuthorize Decorators

```java
// Solo usuario autenticado
@PreAuthorize("isAuthenticated()")

// Solo ADMIN
@PreAuthorize("hasRole('ADMIN')")

// Usuario propietario o ADMIN
@PreAuthorize("@authorizationService.isOwnerOrAdmin(#userId, authentication)")

// MANAGER o ADMIN
@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
```

#### 3.2 Crear AuthorizationService

```java
@Service
public class AuthorizationService {
    
    @Autowired
    private UserService userService;
    
    public boolean isOwnerOrAdmin(Long userId, Authentication authentication) {
        ExpenseUser user = (ExpenseUser) authentication.getPrincipal();
        return user.getId().equals(userId) || hasRole(user, "ADMIN");
    }
    
    private boolean hasRole(ExpenseUser user, String roleName) {
        return user.getRoles().stream()
            .anyMatch(role -> role.getName().equals(roleName));
    }
}
```

---

### FASE 4: Testing (1-2 horas)

#### 4.1 Unit Tests

```java
// ExpenseServiceTest.java
@SpringBootTest
public class ExpenseServiceTest {
    
    @MockBean
    private IExpenseDao expenseDao;
    
    @InjectMocks
    private ExpenseServiceImpl expenseService;
    
    @Test
    public void testCreateExpense() {
        // Test crear gasto
    }
    
    @Test
    public void testGetUserExpenses() {
        // Test obtener gastos del usuario
    }
    
    @Test
    public void testUpdateExpenseNotOwner() {
        // Test que usuario no propietario no puede editar
    }
}
```

#### 4.2 Integration Tests

```java
// ExpenseControllerIT.java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ExpenseControllerIT {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    public void testCreateExpenseEndpoint() {
        // Test POST /api/v1/expenses
    }
    
    @Test
    public void testGetExpensesEndpoint() {
        // Test GET /api/v1/expenses
    }
}
```

---

## 🛠️ Especificaciones Técnicas

### Base de Datos - Nuevas Tablas

```sql
-- Tabla de gastos
CREATE TABLE expense (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    description VARCHAR(255) NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    date DATE NOT NULL,
    category VARCHAR(50) NOT NULL,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES expense_user(id)
);

CREATE INDEX idx_expense_user_id ON expense(user_id);
CREATE INDEX idx_expense_date ON expense(date);
```

### Migraciones Liquibase

```xml
<!-- db/changelog/03-create-expense-table.xml -->
<changeSet id="03-create-expense-table" author="dev">
    <createTable tableName="expense">
        <column name="id" type="BIGINT" autoIncrement="true">
            <constraints primaryKey="true"/>
        </column>
        <column name="description" type="VARCHAR(255)">
            <constraints nullable="false"/>
        </column>
        <!-- ... resto de columnas -->
    </createTable>
</changeSet>
```

### Enumeraciones

```java
public enum ExpenseCategory {
    TRAVEL("Viaje"),
    FOOD("Comida"),
    OFFICE("Oficina"),
    OTHER("Otro");
    
    private String label;
    
    ExpenseCategory(String label) {
        this.label = label;
    }
}
```

---

## 🧪 Testing Plan

### Escenarios de Test

#### Test 1: Crear Gasto
```bash
POST /api/v1/expenses
Content-Type: application/json
Authorization: Bearer <JWT>

{
  "description": "Gasolina reunión cliente",
  "amount": 45.50,
  "date": "2025-11-27",
  "category": "TRAVEL"
}

Expected: 201 Created
Response: {id: 1, description: "Gasolina...", ...}
```

#### Test 2: Obtener Gastos del Usuario
```bash
GET /api/v1/expenses
Authorization: Bearer <JWT>

Expected: 200 OK
Response: [
  {id: 1, description: "Gasolina...", amount: 45.50, ...},
  {id: 2, description: "Almuerzo reunión", amount: 25.00, ...}
]
```

#### Test 3: Editar Gasto Propio
```bash
PUT /api/v1/expenses/1
Content-Type: application/json
Authorization: Bearer <JWT>

{
  "amount": 50.00  // Cambiar monto
}

Expected: 200 OK
```

#### Test 4: Intentar Editar Gasto Ajeno (Unauthorized)
```bash
PUT /api/v1/expenses/2  # Gasto de otro usuario
Authorization: Bearer <JWT_USER_A>

Expected: 403 Forbidden
Response: "No permission to update this expense"
```

#### Test 5: Eliminar Gasto Propio
```bash
DELETE /api/v1/expenses/1
Authorization: Bearer <JWT>

Expected: 204 No Content
```

---

## ⚠️ Riesgos y Mitigaciones

| Riesgo | Probabilidad | Impacto | Mitigación |
|--------|-------------|--------|-----------|
| Relación circular entre Expense y ExpenseUser | Media | Alto | Usar DTOs sin referencias circulares |
| Lazy loading en JPA causando N+1 queries | Alta | Medio | Usar FETCH EAGER o EntityGraph |
| Token JWT vencido durante operación | Media | Medio | Implementar refresh token mechanism |
| Validación de autorización incompleta | Media | Alto | Tests exhaustivos de autorización |
| Performance con muchos gastos | Baja | Medio | Implementar pagination desde inicio |

---

## 📊 Métricas de Éxito

### Objetivos de Session 6

| Métrica | Target | Success Criteria |
|---------|--------|-----------------|
| Dashboard funcional | 100% | Página carga, muestra usuario y menu |
| Endpoints CRUD gastos | 100% | Los 4 endpoints funcionan (GET, POST, PUT, DELETE) |
| Autorización | 100% | Usuario no puede acceder recursos de otro usuario |
| Frontend completitud | 80% | Todas las páginas principales implementadas |
| Test coverage | 60% | Al menos los happy paths testeados |
| Performance | <500ms | Endpoints responden en < 500ms |

---

## 📝 Checklist de Session 6

### FASE 1: Dashboard
- [ ] Endpoint GET /api/v1/users/profile implementado
- [ ] Endpoint POST /api/v1/auth/logout implementado
- [ ] DashboardPage.jsx implementado
- [ ] ProfilePage.jsx implementado
- [ ] Navegación sidebar funcional
- [ ] Información del usuario mostrada

### FASE 2: Gestión de Gastos
- [ ] Tabla expense creada en BD
- [ ] ExpenseServiceImpl implementado
- [ ] ExpenseControllerImpl actualizado
- [ ] CRUD endpoints funcionales
- [ ] DTOs de request/response
- [ ] ExpensesPage.jsx implementado
- [ ] ExpenseForm.jsx implementado
- [ ] ExpenseList.jsx implementado

### FASE 3: Autorización
- [ ] @PreAuthorize decorators configurados
- [ ] AuthorizationService implementado
- [ ] Validación de autorización en endpoints

### FASE 4: Testing
- [ ] Unit tests para ExpenseService
- [ ] Integration tests para ExpenseController
- [ ] Tests de autorización
- [ ] Frontend tests (al menos happy path)

### Finalización
- [ ] Todos los endpoints testeados manualmente
- [ ] No hay errores en logs
- [ ] Documentación actualizada
- [ ] Commits granulares realizados

---

## 🚀 Próximos Pasos Después de Session 6

1. **Session 7: Reportes y Estadísticas**
   - Gráficos de gastos por categoría
   - Comparativas de gastos por período
   - Exportar reportes (CSV, PDF)

2. **Session 8: Gestión de Equipo (Managers)**
   - Ver gastos de empleados subordinados
   - Aprobar/rechazar gastos
   - Asignar presupuestos

3. **Session 9: Administración (Admins)**
   - Gestión de usuarios
   - Asignación de roles
   - Auditoría de cambios

4. **Session 10: Polish y Deployment**
   - UI/UX improvements
   - Performance optimization
   - Deployment a Azure/AWS
   - Documentation final

---

## 📚 Referencias

### Artículos Útiles
- Spring Security @PreAuthorize: https://www.baeldung.com/spring-security-preauthorize
- JWT Best Practices: https://tools.ietf.org/html/rfc8725
- React Hooks: https://react.dev/reference/react/hooks

### Documentación
- Spring Data JPA: https://spring.io/projects/spring-data-jpa
- Spring Security: https://spring.io/projects/spring-security
- React Router: https://reactrouter.com/

---

**Status:** 📋 Roadmap listo para Session 6  
**Última actualización:** 27 de Noviembre de 2025  
**Responsable:** Dev Team

