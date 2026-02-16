**Fecha:** 2025-11-27

# ExpenseNoteApp Architecture

**Actualizado:** 2026-01-07 09:38 UTC

**Versión:** 1.0  
**Última actualización:** Sesión 5  
**Estado:** Autenticación completa, Panel principal y Gastos pendientes

---

## 📋 Tabla de Contenidos

1. [Visión General](#visión-general)
2. [Arquitectura Backend](#arquitectura-backend)
3. [Arquitectura Frontend](#arquitectura-frontend)
4. [Flujo de Datos](#flujo-de-datos)
5. [Seguridad](#seguridad)
6. [Escalabilidad](#escalabilidad)

---

## 🎯 Visión General

### Stack Tecnológico

| Capa | Tecnología | Versión | Propósito |
|------|-----------|---------|----------|
| **Frontend** | React | 18.x | Interfaz de usuario |
| | Vite | 5.x | Herramienta de build |
| | Axios | 1.x | Cliente HTTP |
| | Zustand | - | Gestión de estado |
| | React Router | 6.x | Enrutamiento |
| **Backend** | Spring Boot | 3.3.5 | Framework web |
| | Spring Security | 6.1.x | Autenticación/Autorización |
| | Spring Data JPA | - | ORM |
| | Spring Actuator | - | Verificaciones de salud |
| **Database** | PostgreSQL | 15 | Base de datos |
| | Liquibase | - | Gestión de migraciones |
| **DevOps** | Docker | - | Containerización |
| | Docker Compose | - | Orquestación |
| | Nginx | - | Proxy inverso |

---

### Componentes Principales

```
┌─────────────────────────────────────────────────────────────────┐
│                         FRONTEND (React)                        │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐          │
│  │ Página de inicio de sesión   │  │ Página de registro  │  │ Panel principal    │          │
│  └──────────────┘  └──────────────┘  └──────────────┘          │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐          │
│  │ Página de perfil │  │ Gastos     │  │ Reportes      │          │
│  └──────────────┘  └──────────────┘  └──────────────┘          │
│           ↓ HTTP REST / WebSocket ↓                            │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│                      NGINX (proxy inverso)                     │
│                                                                 │
│                ↓ HTTP REST / Autenticación JWT ↓              │
│                                                                 │
│                    SPRING BOOT BACKEND                         │
├─────────────────────────────────────────────────────────────────┤
│  ┌─────────────────────────────────────────────────────────┐   │
│  │             Capa Controller (APIs REST)                │   │
│  │  ┌────────────┐  ┌────────────┐  ┌────────────┐        │   │
│  │  │ AuthCtrllr │  │ UserCtrllr │  │ ExpenseCtr │        │   │
│  │  └────────────┘  └────────────┘  └────────────┘        │   │
│  └─────────────────────────────────────────────────────────┘   │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │           Capa Service (lógica de negocio)                │   │
│  │  ┌────────────┐  ┌────────────┐  ┌────────────┐        │   │
│  │  │ AuthSvc    │  │ UserSvc    │  │ ExpenseSvc │        │   │
│  │  └────────────┘  └────────────┘  └────────────┘        │   │
│  └─────────────────────────────────────────────────────────┘   │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │            Capa Repository (acceso a datos)               │   │
│  │  ┌────────────┐  ┌────────────┐  ┌────────────┐        │   │
│  │  │ UserRepo   │  │ RoleRepo   │  │ ExpenseRep │        │   │
│  │  └────────────┘  └────────────┘  └────────────┘        │   │
│  └─────────────────────────────────────────────────────────┘   │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │             Capa de Seguridad                              │   │
│  │  ┌─────────────────────────────────────────────────┐   │   │
│  │  │ Generador y validador de tokens JWT               │   │   │
│  │  │ Cadena de filtros de Spring Security                  │   │   │
│  │  │ Codificador de contraseñas BCrypt                       │   │   │
│  │  │ Control de acceso basado en roles (RBAC)              │   │   │
│  │  └─────────────────────────────────────────────────┘   │   │
│  └─────────────────────────────────────────────────────────┘   │
│                ↓ JDBC / ORM ↓                                  │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│              BASE DE DATOS POSTGRESQL (Persistente)                   │
│                                                                 │
│  Tablas: expense_user, role, user_role, expense, payroll, ...  │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

---

## 🏗️ Arquitectura Backend

### Estructura de Directorios

```
backend/
├── src/main/java/io/sunbit/app/
│   ├── ExpenseNoteAppApplication.java
│   │   └── Clase principal de la aplicación Spring Boot
│   │
│   ├── controller/
│   │   ├── IAuthController.java
│   │   ├── AuthenticationController.java      (Registro/Inicio de sesión)
│   │   ├── IUserController.java
│   │   ├── UserControllerImpl.java             (Perfil, cierre de sesión)
│   │   ├── IExpenseController.java
│   │   ├── ExpenseControllerImpl.java          (CRUD de gastos)
│   │   ├── IEmployeeController.java
│   │   ├── EmployeeControllerImpl.java
│   │   ├── IPayrollController.java
│   │   ├── PayrollControllerImpl.java
│   │   ├── IPositionController.java
│   │   └── PositionControllerImpl.java
│   │
│   ├── service/
│   │   ├── IAuthService.java
│   │   ├── AuthServiceImpl.java
│   │   ├── IUserService.java
│   │   ├── UserServiceImpl.java
│   │   ├── IEmployeeService.java
│   │   ├── EmployeeServiceImpl.java
│   │   ├── IExpenseService.java
│   │   ├── ExpenseServiceImpl.java
│   │   ├── IPayrollService.java
│   │   ├── PayrollServiceImpl.java
│   │   ├── IPositionService.java
│   │   └── PositionServiceImpl.java
│   │
│   ├── dao/
│   │   ├── IUserDao.java
│   │   ├── UserDaoImpl.java
│   │   ├── IExpenseDao.java
│   │   ├── ExpenseDaoImpl.java
│   │   ├── IEmployeeDao.java
│   │   ├── EmployeeDaoImpl.java
│   │   ├── IPayrollDao.java
│   │   ├── PayrollDaoImpl.java
│   │   ├── IPositionDao.java
│   │   └── PositionDaoImpl.java
│   │
│   ├── entity/
│   │   ├── ExpenseUser.java          (Entidad principal del usuario)
│   │   ├── Role.java                 (Control de acceso)
│   │   ├── Expense.java              (Registros de gastos)
│   │   ├── Employee.java             (Detalles de empleados)
│   │   ├── Payroll.java              (Información salarial)
│   │   └── Position.java             (Puestos de trabajo)
│   │
│   ├── dto/
│   │   ├── AuthenticationRequest.java
│   │   ├── AuthenticationResponse.java
│   │   ├── SignUpRequest.java
│   │   ├── SignUpResponse.java
│   │   ├── UserMapper.java
│   │   ├── UserUpdateRequest.java
│   │   ├── UserProfileResponse.java
│   │   ├── ExpenseDto.java
│   │   ├── ExpenseMapper.java
│   │   ├── CreateExpenseRequest.java
│   │   ├── UpdateExpenseRequest.java
│   │   ├── EmployeeDto.java
│   │   ├── EmployeeMapper.java
│   │   ├── PayrollDto.java
│   │   ├── PayrollMapper.java
│   │   ├── PositionDto.java
│   │   └── PositionMapper.java
│   │
│   ├── exception/
│   │   ├── BadRequestException.java
│   │   ├── ResourceNotFoundException.java
│   │   ├── UnauthorizedException.java
│   │   ├── InternalServerException.java
│   │   ├── GlobalExceptionHandler.java
│   │   └── ApiErrorResponse.java
│   │
│   └── security/
│       ├── configuration/
│       │   ├── AppSecurityConfig.java    (Spring Security setup)
│       │   └── CorsConfig.java
│       ├── controller/
│       │   └── AuthenticationController.java
│       ├── dao/
│       │   ├── IExpenseUserDao.java
│       │   └── ExpenseUserDaoImpl.java
│       ├── dto/
│       │   ├── AuthenticationRequest.java
│       │   ├── AuthenticationResponse.java
│       │   └── SignUpRequest.java
│       ├── entity/
│       │   ├── ExpenseUser.java
│       │   └── Role.java
│       ├── jwt/
│       │   ├── JwtAuthenticationUtil.java
│       │   ├── JwtAuthenticationFilter.java
│       │   └── JwtAuthenticationEntryPoint.java
│       ├── login/
│       │   └── CustomUserDetailsService.java
│       └── service/
│           ├── IUserService.java
│           └── UserServiceImpl.java
│
├── src/main/resources/
│   ├── application.properties
│   ├── application-dev.properties
│   ├── application-prod.properties
│   ├── META-INF/additional-spring-configuration-metadata.json
│   ├── db/changelog/
│   │   ├── db.changelog-master.xml
│   │   ├── 01-create-tables.xml
│   │   ├── 02-insert-roles.xml
│   │   └── 03-create-expense-table.xml
│   └── templates/
│       ├── index.html
│       ├── login.html
│       └── signup.html
│
└── pom.xml
    ├── Spring Boot Starters
    ├── Spring Security
    ├── Spring Data JPA
    ├── PostgreSQL Driver
    ├── Lombok
    ├── JWT (io.jsonwebtoken)
    ├── Liquibase
    ├── JUnit 5
    └── Mockito
```

---

### Capas Arquitectónicas

#### 1. **Capa Controller** (Endpoints REST)

**Responsabilidades:**
- Recibir requests HTTP
- Validar parámetros de entrada
- Delegar lógica a Services
- Retornar respuestas JSON

**Patrón:** Interface + Implementation
```java
// Interfaz
public interface IAuthController {
    ResponseEntity<?> signup(SignUpRequest request);
    ResponseEntity<?> login(AuthenticationRequest request);
}

// Implementación
@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController implements IAuthController {
    // Implementación
}
```

---

#### 2. **Capa Service** (lógica de negocio)

**Responsabilidades:**
- Implementar reglas de negocio
- Validaciones complejas
- Orquestación de DAOs
- Transacciones

**Patrón:** Interface + Implementation con @Transactional
```java
public interface IUserService {
    ExpenseUser findByEmail(String email);
    ExpenseUser save(ExpenseUser user);
}

@Service
@Transactional
public class UserServiceImpl implements IUserService {
    @Autowired
    private IUserDao userDao;
    
    @Override
    public ExpenseUser save(ExpenseUser user) {
        // Lógica de negocio
        return userDao.save(user);
    }
}
```

---

#### 3. **Capa DAO** (Objetos de acceso a datos)

**Responsabilidades:**
- Acceso directo a BD
- Queries SQL/JPQL
- Mapeo entidad-BD

**Patrón:** Repository pattern con Spring Data JPA
```java
public interface IUserDao {
    Optional<ExpenseUser> findByEmail(String email);
    ExpenseUser save(ExpenseUser user);
    List<ExpenseUser> findAll();
}

@Repository
public class UserDaoImpl implements IUserDao {
    @PersistenceContext
    private EntityManager entityManager;
    
    @Override
    public Optional<ExpenseUser> findByEmail(String email) {
        return entityManager.createQuery(...)
            .getResultList()
            .stream()
            .findFirst();
    }
}
```

---

#### 4. **Capa Entity** (Modelos de dominio)

**Responsabilidades:**
- Representar datos del dominio
- Mapeo JPA
- Validaciones con @NotNull, @Email, etc.

**Ejemplo:**
```java
@Entity
@Table(name = "expense_user")
@Data
@NoArgsConstructor
public class ExpenseUser implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String password;
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_role",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();
    
    // UserDetails implementation...
}
```

---

#### 5. **Capa de Seguridad**

**Componentes:**

| Componente | Responsabilidad |
|-----------|-----------------|
| `AppSecurityConfig` | Configuración global de Spring Security |
| `JwtAuthenticationUtil` | Generación y validación de JWT |
| `JwtAuthenticationFilter` | Filtro que valida JWT en cada request |
| `CustomUserDetailsService` | Carga usuario desde BD |
| `BCryptPasswordEncoder` | Hashing seguro de contraseñas |

**Flujo de Autenticación:**
```
1. Cliente envía (email, password)
   ↓
2. AuthenticationController recibe
   ↓
3. AuthenticationManager (Spring Security)
   - CustomUserDetailsService.loadUserByUsername()
   - Busca usuario en BD
   - Compara password hasheado
   ↓
4. Si válido:
   - JwtAuthenticationUtil genera token
   - Retorna token al cliente
   ↓
5. Cliente incluye token en próximos requests:
   Authorization: Bearer <JWT>
   ↓
6. JwtAuthenticationFilter intercepta
   - Valida token
   - Setea Security Context
   ↓
7. Request llega al endpoint autenticado
```

---

### Flujo de Data

#### Signup Endpoint

```
POST /api/v1/auth/signup
{
  "username": "newuser",
  "email": "new@example.com",
  "password": "SecurePass123"
}
            ↓
AuthenticationController.signup()
            ↓
AuthServiceImpl.register()
            ↓
Validaciones:
  - Email no duplicado
  - Password válido
  - Username válido
            ↓
Procesar:
  - Hash password con BCrypt
  - Crear ExpenseUser
  - Asignar role USER
            ↓
UserServiceImpl.save()
            ↓
UserDaoImpl.save()
            ↓
entityManager.persist() → PostgreSQL
            ↓
201 Created
{
  "id": 7,
  "username": "newuser",
  "email": "new@example.com"
}
```

---

#### Login Endpoint

```
POST /api/v1/auth/login
{
  "email": "user@example.com",
  "password": "SecurePass123"
}
            ↓
AuthenticationController.login()
            ↓
authenticationManager.authenticate()
            ↓
CustomUserDetailsService.loadUserByUsername()
            ↓
UserServiceImpl.loadUserByUsername()
            ↓
UserDaoImpl.findByEmail() → PostgreSQL
            ↓
Obtener ExpenseUser con roles
            ↓
PasswordEncoder.matches() → Validar password
            ↓
Si válido:
  JwtAuthenticationUtil.generateToken(user)
            ↓
200 OK
{
  "email": "user@example.com",
  "accessToken": "<JWT_TOKEN_PLACEHOLDER>"
}
```

---

#### Endpoint Protegido (GET /api/v1/users/profile)

```
GET /api/v1/users/profile
Authorization: Bearer <JWT_TOKEN_PLACEHOLDER>
            ↓
JwtAuthenticationFilter
  - Extrae token de header
  - Valida con JwtAuthenticationUtil
            ↓
Si válido:
  - Extrae claims (email, roles, etc.)
  - Crea Authentication
  - Setea en SecurityContext
            ↓
UserController.getProfile()
            ↓
Authentication auth = SecurityContextHolder.getContext().getAuthentication()
String email = auth.getName()
            ↓
UserServiceImpl.findByEmail(email)
            ↓
UserDaoImpl.findByEmail() → PostgreSQL
            ↓
200 OK
{
  "id": 1,
  "email": "user@example.com",
  "roles": ["USER"]
}
```

---

## 🎨 Arquitectura Frontend

### Estructura de Directorios

```
frontend/
├── src/
│   ├── components/
│   │   ├── PrivateRoute.jsx         (Protected routes)
│   │   ├── ExpenseForm.jsx          (Form component)
│   │   ├── ExpenseList.jsx          (List component)
│   │   ├── Navbar.jsx               (Navigation)
│   │   ├── Sidebar.jsx              (Menu)
│   │   └── LoadingSpinner.jsx       (Loading state)
│   │
│   ├── pages/
│   │   ├── LoginPage.jsx
│   │   ├── SignupPage.jsx
│   │   ├── Panel principalPage.jsx
│   │   ├── ProfilePage.jsx
│   │   ├── GastosPage.jsx
│   │   └── NotFoundPage.jsx
│   │
│   ├── api/
│   │   ├── apiClient.js             (Axios instance)
│   │   ├── endpoints.js             (API endpoints)
│   │   └── interceptors.js          (Request/Response interceptors)
│   │
│   ├── hooks/
│   │   ├── useAuth.js               (Authentication hook)
│   │   ├── useForm.js               (Form handling hook)
│   │   └── useApi.js                (API calling hook)
│   │
│   ├── store/
│   │   ├── authStore.js             (Zustand auth store)
│   │   ├── userStore.js             (User data store)
│   │   └── expenseStore.js          (Expense data store)
│   │
│   ├── styles/
│   │   ├── index.css
│   │   ├── login.css
│   │   ├── dashboard.css
│   │   ├── expenses.css
│   │   └── responsive.css
│   │
│   ├── utils/
│   │   ├── validators.js            (Form validators)
│   │   ├── formatters.js            (Date, currency formatting)
│   │   └── helpers.js               (Utility functions)
│   │
│   ├── App.jsx                      (Main app component)
│   └── main.jsx                     (Entry point)
│
├── package.json
├── vite.config.js
└── index.html
```

---

### Patrones Frontend

#### 1. State Management con Zustand

```javascript
// store/authStore.js
import { create } from 'zustand'

export const useAuthStore = create((set) => ({
  token: localStorage.getItem('authToken') || null,
  user: null,
  
  setToken: (token) => {
    localStorage.setItem('authToken', token)
    set({ token })
  },
  
  setUser: (user) => set({ user }),
  
  logout: () => {
    localStorage.removeItem('authToken')
    set({ token: null, user: null })
  }
}))
```

#### 2. Custom Hooks

```javascript
// hooks/useAuth.js
import { useAuthStore } from '../store/authStore'

export function useAuth() {
  const token = useAuthStore(state => state.token)
  const user = useAuthStore(state => state.user)
  
  return {
    isAuthenticated: !!token,
    token,
    user
  }
}
```

#### 3. API Client con Interceptores

```javascript
// api/apiClient.js
import axios from 'axios'
import { useAuthStore } from '../store/authStore'

const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080/api/v1'
})

// Interceptor de request
apiClient.interceptors.request.use(
  (config) => {
    const token = useAuthStore.getState().token
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  }
)

// Interceptor de response
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Token inválido, logout
      useAuthStore.getState().logout()
    }
    return Promise.reject(error)
  }
)

export default apiClient
```

#### 4. Protected Routes

```javascript
// components/PrivateRoute.jsx
import { Navigate, Outlet } from 'react-router-dom'
import { useAuth } from '../hooks/useAuth'

export default function PrivateRoute() {
  const { isAuthenticated } = useAuth()
  
  return isAuthenticated ? <Outlet /> : <Navigate to="/login" />
}
```

---

## 🔄 Flujo de Datos

### Diagrama de Interacción

```
┌─────────────────────────────────────────────────────┐
│ Usuario accede a http://localhost                  │
└────────────────┬────────────────────────────────────┘
                 ↓
         ┌───────────────┐
         │ Nginx Server  │
         │ (Frontend)    │
         └───────┬───────┘
                 ↓
    ┌────────────────────────┐
    │ React App (Vite)       │
    │ - App.jsx              │
    │ - Router Setup         │
    │ - useAuth Hook         │
    └────────┬───────────────┘
             ↓
    Si NO autenticado:
      ┌──────────────┐
      │ LoginPage    │
      └──────┬───────┘
             ↓
      POST /api/v1/auth/login
      (Email + Password)
             ↓
    ┌─────────────────────────────────────┐
    │ Backend Spring Boot                 │
    │ ┌────────────────────────────────┐  │
    │ │ AuthenticationController       │  │
    │ └──────────┬─────────────────────┘  │
    │            ↓                         │
    │ ┌────────────────────────────────┐  │
    │ │ AuthenticationManager          │  │
    │ │ - Validate credentials         │  │
    │ │ - Hash password               │  │
    │ └──────────┬─────────────────────┘  │
    │            ↓                         │
    │ ┌────────────────────────────────┐  │
    │ │ CustomUserDetailsService       │  │
    │ │ - Load from DB                 │  │
    │ │ - Fetch roles                  │  │
    │ └──────────┬─────────────────────┘  │
    │            ↓                         │
    │ ┌────────────────────────────────┐  │
    │ │ PostgreSQL Database            │  │
    │ │ - expense_user table           │  │
    │ │ - user_role table              │  │
    │ │ - role table                   │  │
    │ └──────────┬─────────────────────┘  │
    │            ↓                         │
    │ JWT Generated                        │
    └────────────┬──────────────────────────┘
                 ↓
    Response: {token, email, roles}
                 ↓
    ┌──────────────────────────────┐
    │ Frontend - Zustand Store     │
    │ useAuthStore.setToken(token) │
    │ localStorage.setItem(token)  │
    └──────────────┬───────────────┘
                   ↓
    ┌──────────────────────────────┐
    │ Panel principalPage (Protected)     │
    │ - Load user profile          │
    │ - Fetch expenses             │
    │ - Display navigation          │
    └──────────────────────────────┘
```

---

## 🔐 Seguridad

### Autenticación (Authentication)

- **Método:** JWT (JSON Web Tokens)
- **Algoritmo:** HS512 (HMAC-SHA512)
- **Almacenamiento:** localStorage (frontend)
- **Transmisión:** Authorization header
- **Expiración:** Configurable (por defecto: 24 horas)

**JWT Payload:**
```json
{
  "sub": "1,user@example.com",  // Subject (user id + email)
  "roles": "[USER]",             // User roles
  "iss": "ExpenseNoteApp",       // Issuer
  "iat": 1764282042,             // Issued at
  "exp": 1764368442              // Expiration
}
```

---

### Autorización (Authorization)

**Niveles de acceso:**

| Recurso | PUBLIC | USER | MANAGER | ADMIN |
|---------|--------|------|---------|-------|
| /login | ✅ | ✅ | ✅ | ✅ |
| /signup | ✅ | ✅ | ✅ | ✅ |
| /profile | - | ✅ | ✅ | ✅ |
| /expenses (propios) | - | ✅ | ✅ | ✅ |
| /expenses (de otros) | - | ❌ | ✅ (subordinados) | ✅ |
| /admin/users | - | ❌ | ❌ | ✅ |

---

### Password Hashing

- **Algoritmo:** BCrypt
- **Salt:** Generado aleatoriamente
- **Costo:** 10 rondas (por defecto)
- **Verificación:** Spring Security PasswordEncoder

**Ejemplo:**
```
Contraseña plaintext: "MyPassword123"
Hash BCrypt: $2a$10$N9qo8ucoaQQeVVsGXpA.0e6RmjGLG.khMy18xVe8YL9xgDDLbCxKq
            └─ $2a:     Identificador BCrypt
            └─ $10:     Número de rondas
            └─ $.N9q..  Salt (16 bytes en base64)
            └─ o8ucoaQ.. Hash encriptado
```

---

### Validación de Entrada

```java
// DTOs con constraints
@Data
public class SignUpRequest {
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 100, message = "Password must be 6-100 chars")
    private String password;
}
```

**Validación en respuesta:**
```json
{
  "status": 422,
  "message": "Validation failed",
  "errors": [
    {
      "field": "email",
      "message": "Email must be valid"
    },
    {
      "field": "password",
      "message": "Password must be 6-100 chars"
    }
  ]
}
```

---

### CORS (Cross-Origin Resource Sharing)

```java
@CrossOrigin(
    origins = "*",        // Permitir todas las origins
    methods = {           // Métodos permitidos
        RequestMethod.GET,
        RequestMethod.POST,
        RequestMethod.PUT,
        RequestMethod.DELETE
    },
    maxAge = 3600,        // Cache por 1 hora
    allowedHeaders = "*"  // Headers permitidos
)
```

---

## 📈 Escalabilidad

### Horizontal Scaling

**Componentes sin estado (Stateless):**
- ✅ Spring Boot backend (sin sesiones)
- ✅ Frontend (JWT en client)
- ✅ Nginx (load balancer)

**Estrategia:**
```
                ┌─────────────────────────┐
                │   Load Balancer         │
                │   (HAProxy/AWS ELB)     │
                └──────┬────┬────┬────────┘
                       ↓    ↓    ↓
                ┌───────┐ ┌───────┐ ┌───────┐
                │ Bkend1│ │ Bkend2│ │ Bkend3│
                └───────┘ └───────┘ └───────┘
                       ↓    ↓    ↓
                ┌─────────────────────────┐
                │  Shared Database        │
                │  (PostgreSQL Cluster)   │
                └─────────────────────────┘
```

---

### Caching

**Frontend:**
- LocalStorage: JWT token, user preferences
- SesiónStorage: Temporary data
- Browser cache: Static assets (CSS, JS, imágenes)

**Backend:**
```java
@Cacheable("users")
public ExpenseUser findByEmail(String email) {
    return userDao.findByEmail(email);
}

@CacheEvict(value = "users", key = "#email")
public void updateUser(String email, UserUpdateRequest req) {
    // Update logic
}
```

---

### Database Optimization

**Índices:**
```sql
CREATE INDEX idx_user_email ON expense_user(email);
CREATE INDEX idx_expense_user_id ON expense(user_id);
CREATE INDEX idx_expense_date ON expense(date);
```

**Pagination:**
```java
// Backend
Page<Expense> findByUserId(Long userId, Pageable pageable);

// Frontend
const [page, setPage] = useState(0);
const response = await apiClient.get('/expenses', {
  params: { page, size: 10 }
});
```

---

### Monitoring y Observabilidad

**Spring Boot Actuator:**
```
GET /actuator/health          → Health check
GET /actuator/metrics         → Metrics
GET /actuator/prometheus      → Prometheus metrics
```

**Logging:**
```properties
# application.properties
logging.level.root=WARN
logging.level.io.sunbit.app=INFO
logging.pattern.console=%d{HH:mm:ss} %-5p %c{1} - %m%n
```

---

## 🔄 Integración Continua (CI/CD)

### Pipeline Sugerido

```yaml
# .github/workflows/pipeline.yml
name: CI/CD Pipeline

on:
  push:
    branches: [main, develop]
  pull_request:
    branches: [main]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      
      - name: Build Backend
        run: |
          cd backend
          mvn clean package
      
      - name: Build Frontend
        run: |
          cd frontend
          npm install
          npm run build
      
      - name: Docker Build
        run: docker-compose build
      
      - name: Run Tests
        run: |
          mvn test
          npm test
      
      - name: Push to Registry
        run: docker push myregistry/expenseapp:latest
      
      - name: Deploy
        run: kubectl apply -f k8s/
```

---

**Estado de Documento:** ✅ Completo  
**Última revisión:** Sesión 5  
**Próxima revisión:** Sesión 6 (después de dashboard/gastos)
---
Actualizado: 2026-01-07 09:38 UTC
