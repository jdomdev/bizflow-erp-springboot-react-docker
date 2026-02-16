# Arquitectura

Bizflow ERP sigue una arquitectura de tres capas con separación clara entre frontend, backend y base de datos.

## Diagrama General

```
┌─────────────────────────────────────────────────────────────────┐
│                         CLIENTE                                 │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐         │
│  │   Browser   │    │   Mobile    │    │   Postman   │         │
│  └──────┬──────┘    └──────┬──────┘    └──────┬──────┘         │
└─────────┼──────────────────┼──────────────────┼─────────────────┘
          │                  │                  │
          ▼                  ▼                  ▼
┌─────────────────────────────────────────────────────────────────┐
│                     FRONTEND (React 18)                         │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │  Nginx (puerto 3000)                                     │   │
│  │  ├── React Router (SPA)                                  │   │
│  │  ├── Zustand (State Management)                          │   │
│  │  ├── Axios (HTTP Client)                                 │   │
│  │  └── WebSocket Client (Notificaciones)                   │   │
│  └─────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
          │
          │ REST API + WebSocket
          ▼
┌─────────────────────────────────────────────────────────────────┐
│                  BACKEND (Spring Boot 3.3.4)                    │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │  Tomcat (puerto 8080)                                    │   │
│  │  ├── Controllers (REST API)                              │   │
│  │  ├── Services (Business Logic)                           │   │
│  │  ├── DAOs/Repositories (Data Access)                     │   │
│  │  ├── Spring Security + JWT                               │   │
│  │  └── WebSocket Handler (STOMP)                           │   │
│  └─────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
          │
          │ JDBC
          ▼
┌─────────────────────────────────────────────────────────────────┐
│                    BASE DE DATOS (PostgreSQL 16)                │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │  Tablas:                                                 │   │
│  │  ├── expense_user (usuarios)                             │   │
│  │  ├── employee (empleados)                                │   │
│  │  ├── expense (gastos)                                    │   │
│  │  ├── payroll (nóminas)                                   │   │
│  │  ├── position (cargos)                                   │   │
│  │  └── role (roles)                                        │   │
│  └─────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
```

## Capas del Backend

### Controllers

Manejan las peticiones HTTP y delegan la lógica a los servicios.

```java
@RestController
@RequestMapping("/api/v1/expenses")
public class ExpenseControllerImpl {
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<Expense>> getAllExpenses() {
        return ResponseEntity.ok(expenseService.findAll());
    }
}
```

### Services

Contienen la lógica de negocio y coordinan las operaciones.

```java
@Service
public class ExpenseServiceImpl implements IExpenseService {
    
    public List<Expense> findWithFilters(String token) {
        if (jwtUtil.isAdminTokenUser(token) || jwtUtil.isManagerTokenUser(token)) {
            return expenseDao.findAll();
        }
        return expenseDao.findByCreatorId(jwtUtil.getUserId(token));
    }
}
```

### DAOs (Repositories)

Acceden a la base de datos usando JPA.

```java
public interface IExpenseDao extends JpaRepository<Expense, Long>, 
                                     JpaSpecificationExecutor<Expense> {
    List<Expense> findByCreatorId(Long creatorId);
}
```

## Sistema de Roles

```
┌────────────────────────────────────────────────────────────┐
│                    JERARQUÍA DE ROLES                       │
├────────────────────────────────────────────────────────────┤
│                                                            │
│  ADMIN (roleId: 1)                                         │
│  ├── Acceso completo a todos los módulos                   │
│  ├── CRUD de usuarios                                      │
│  ├── CRUD de empleados, gastos, nóminas, cargos            │
│  └── Recibe notificaciones de gastos nuevos                │
│                                                            │
│  MANAGER (roleId: 3)                                       │
│  ├── Ver todos los recursos (lectura)                      │
│  ├── Editar solo sus propios registros                     │
│  ├── Sin acceso a gestión de usuarios                      │
│  └── Recibe notificaciones de nóminas                      │
│                                                            │
│  USER (roleId: 2)                                          │
│  ├── Ver/editar solo sus propios recursos                  │
│  ├── CRUD de gastos propios                                │
│  └── Recibe notificaciones de nóminas                      │
│                                                            │
└────────────────────────────────────────────────────────────┘
```

## Flujo de Autenticación

```
┌──────────┐          ┌──────────┐          ┌──────────┐
│  Cliente │          │  Backend │          │    DB    │
└────┬─────┘          └────┬─────┘          └────┬─────┘
     │                     │                     │
     │  POST /auth/login   │                     │
     │  {email, password}  │                     │
     │────────────────────>│                     │
     │                     │  Buscar usuario     │
     │                     │────────────────────>│
     │                     │  Usuario encontrado │
     │                     │<────────────────────│
     │                     │                     │
     │                     │  Validar password   │
     │                     │  (BCrypt)           │
     │                     │                     │
     │  {token, user}      │                     │
     │<────────────────────│                     │
     │                     │                     │
     │  GET /api/expenses  │                     │
     │  Authorization:     │                     │
     │  Bearer <token>     │                     │
     │────────────────────>│                     │
     │                     │  Validar JWT        │
     │                     │  Extraer roles      │
     │                     │                     │
     │  [expenses]         │                     │
     │<────────────────────│                     │
```

## WebSocket (Notificaciones)

```
┌──────────────────────────────────────────────────────────┐
│                    WEBSOCKET FLOW                         │
├──────────────────────────────────────────────────────────┤
│                                                          │
│  1. Cliente conecta: ws://localhost:8080/ws              │
│  2. STOMP handshake con token JWT                        │
│  3. Suscripción: /topic/notifications/{userId}           │
│                                                          │
│  Cuando se crea un gasto:                                │
│  ├── ExpenseService.save()                               │
│  ├── NotificationService.notify()                        │
│  └── WebSocketHandler.broadcast() → ADMIN users          │
│                                                          │
│  Cuando se crea una nómina:                              │
│  ├── PayrollService.save()                               │
│  ├── NotificationService.notify()                        │
│  └── WebSocketHandler.send() → Usuario específico        │
│                                                          │
└──────────────────────────────────────────────────────────┘
```

## Estructura de Carpetas

```
bizflow-erp/
├── backend/
│   └── src/main/java/io/sunbit/app/
│       ├── controller/      # REST Controllers
│       ├── service/         # Business Logic
│       ├── dao/             # Data Access (JPA)
│       ├── entity/          # JPA Entities
│       ├── dto/             # Data Transfer Objects
│       ├── security/        # JWT, Filters
│       └── config/          # Spring Configuration
│
├── frontend/
│   └── src/
│       ├── components/      # React Components
│       ├── pages/           # Page Components
│       ├── contexts/        # React Contexts
│       ├── hooks/           # Custom Hooks
│       ├── services/        # API Services
│       └── store/           # Zustand Stores
│
├── docker/                  # Dockerfiles base
├── scripts/                 # Utilidades y seeds
├── sql/                     # Migraciones SQL
└── docs/                    # Documentación
```
