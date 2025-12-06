# Arquitectura del Sistema - ExpenseNoteApp v1.1.0

Documentación completa de la arquitectura técnica del sistema de gestión de gastos empresariales.

## 📋 Tabla de Contenidos

1. [Visión General](#visión-general)
2. [Arquitectura de Alto Nivel](#arquitectura-de-alto-nivel)
3. [Componentes del Sistema](#componentes-del-sistema)
4. [Arquitectura del Backend](#arquitectura-del-backend)
5. [Arquitectura del Frontend](#arquitectura-del-frontend)
6. [Modelo de Datos](#modelo-de-datos)
7. [Seguridad](#seguridad)
8. [Flujos de Datos](#flujos-de-datos)
9. [Decisiones de Diseño](#decisiones-de-diseño)

---

## 🎯 Visión General

ExpenseNoteApp es una aplicación web full-stack moderna que sigue una arquitectura de tres capas:

- **Capa de Presentación**: Frontend React con Vite
- **Capa de Lógica de Negocio**: Backend Spring Boot con REST API
- **Capa de Datos**: PostgreSQL con JPA/Hibernate

### Características Arquitectónicas

- **Estilo**: RESTful API con arquitectura cliente-servidor
- **Autenticación**: JWT (JSON Web Tokens)
- **Patrón**: MVC (Model-View-Controller) en backend
- **Estado**: Gestión centralizada con Zustand en frontend
- **Comunicación**: HTTP/HTTPS con JSON
- **Contenedorización**: Docker y Docker Compose

---

## 🏗️ Arquitectura de Alto Nivel

```
┌─────────────────────────────────────────────────────────────────┐
│                         USUARIOS                                 │
│                    (Navegador Web)                               │
└────────────────────────────┬────────────────────────────────────┘
                             │ HTTPS/HTTP
                             │
┌────────────────────────────▼────────────────────────────────────┐
│                    CAPA DE PRESENTACIÓN                          │
│                                                                  │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │              React Frontend (Vite)                        │  │
│  │  - React Router (Navegación)                              │  │
│  │  - Zustand (Estado Global)                                │  │
│  │  - Axios (HTTP Client)                                    │  │
│  │  - Tailwind CSS (Estilos)                                 │  │
│  │  - Framer Motion (Animaciones)                            │  │
│  └──────────────────────────────────────────────────────────┘  │
│                                                                  │
└────────────────────────────┬────────────────────────────────────┘
                             │ REST API (JSON)
                             │ /api/v1/*
┌────────────────────────────▼────────────────────────────────────┐
│                  CAPA DE LÓGICA DE NEGOCIO                       │
│                                                                  │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │           Spring Boot Backend (Java 21)                   │  │
│  │                                                            │  │
│  │  ┌─────────────────────────────────────────────────────┐ │  │
│  │  │          Security Layer                              │ │  │
│  │  │  - JWT Authentication Filter                         │ │  │
│  │  │  - Spring Security 6.3.3                             │ │  │
│  │  │  - CORS Configuration                                │ │  │
│  │  └─────────────────────────────────────────────────────┘ │  │
│  │                                                            │  │
│  │  ┌─────────────────────────────────────────────────────┐ │  │
│  │  │          Controllers (REST Endpoints)                │ │  │
│  │  │  - ExpenseController                                 │ │  │
│  │  │  - EmployeeController                                │ │  │
│  │  │  - PayrollController                                 │ │  │
│  │  │  - UserController                                    │ │  │
│  │  │  - AuthenticationController                          │ │  │
│  │  └─────────────────────────────────────────────────────┘ │  │
│  │                                                            │  │
│  │  ┌─────────────────────────────────────────────────────┐ │  │
│  │  │          Services (Business Logic)                   │ │  │
│  │  │  - ExpenseService                                    │ │  │
│  │  │  - EmployeeService                                   │ │  │
│  │  │  - PayrollService                                    │ │  │
│  │  │  - UserService                                       │ │  │
│  │  └─────────────────────────────────────────────────────┘ │  │
│  │                                                            │  │
│  │  ┌─────────────────────────────────────────────────────┐ │  │
│  │  │          Repositories (Data Access)                  │ │  │
│  │  │  - ExpenseRepository (JPA)                           │ │  │
│  │  │  - EmployeeRepository (JPA)                          │ │  │
│  │  │  - PayrollRepository (JPA)                           │ │  │
│  │  │  - UserRepository (JPA)                              │ │  │
│  │  └─────────────────────────────────────────────────────┘ │  │
│  │                                                            │  │
│  └──────────────────────────────────────────────────────────┘  │
│                                                                  │
└────────────────────────────┬────────────────────────────────────┘
                             │ JDBC
                             │
┌────────────────────────────▼────────────────────────────────────┐
│                      CAPA DE DATOS                               │
│                                                                  │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │              PostgreSQL Database                          │  │
│  │  - expense (gastos)                                       │  │
│  │  - employee (empleados)                                   │  │
│  │  - payroll (nóminas)                                      │  │
│  │  - users (usuarios)                                       │  │
│  │  - roles (roles)                                          │  │
│  │  - positions (puestos)                                    │  │
│  └──────────────────────────────────────────────────────────┘  │
│                                                                  │
└──────────────────────────────────────────────────────────────────┘
```

---

## 🧩 Componentes del Sistema

### 1. Frontend (React + Vite)

**Tecnologías**:
- React 18.2.0
- Vite 5.0.0 (Build tool)
- React Router 6.x (Routing)
- Zustand 4.4.7 (State management)
- Axios 1.6.0 (HTTP client)
- Tailwind CSS 3.4.0 (Styling)
- Framer Motion 10.16.4 (Animations)

**Responsabilidades**:
- Renderizar interfaz de usuario
- Gestionar estado de la aplicación
- Validar inputs del usuario
- Comunicarse con el backend vía REST API
- Manejar autenticación JWT
- Proporcionar feedback visual al usuario

---

### 2. Backend (Spring Boot)

**Tecnologías**:
- Spring Boot 3.3.4
- Spring Framework 6.1.13
- Spring Security 6.3.3
- Spring Data JPA
- JWT (jjwt 0.12.6)
- Hibernate 6.6.5
- Java 21

**Responsabilidades**:
- Exponer REST API
- Validar y procesar peticiones
- Implementar lógica de negocio
- Gestionar autenticación y autorización
- Persistir datos en base de datos
- Logging y monitoreo

---

### 3. Base de Datos (PostgreSQL)

**Tecnología**: PostgreSQL 12+

**Responsabilidades**:
- Almacenar datos persistentes
- Garantizar integridad referencial
- Optimizar queries con índices
- Realizar backups

---

## 🔧 Arquitectura del Backend

### Capas del Backend

```
┌────────────────────────────────────────────┐
│         REST Controllers                    │
│  @RestController, @RequestMapping           │
│  - Maneja HTTP requests/responses           │
│  - Validación de inputs                     │
│  - Serialización JSON                       │
└─────────────────┬──────────────────────────┘
                  │
┌─────────────────▼──────────────────────────┐
│         Service Layer                       │
│  @Service, @Transactional                   │
│  - Lógica de negocio                        │
│  - Validaciones complejas                   │
│  - Orquestación de operaciones              │
└─────────────────┬──────────────────────────┘
                  │
┌─────────────────▼──────────────────────────┐
│         Repository Layer                    │
│  @Repository, Spring Data JPA               │
│  - Acceso a datos                           │
│  - Queries personalizadas                   │
│  - Transacciones                            │
└─────────────────┬──────────────────────────┘
                  │
┌─────────────────▼──────────────────────────┐
│         Database (PostgreSQL)               │
└────────────────────────────────────────────┘
```

### Patrones Implementados

1. **MVC (Model-View-Controller)**:
   - Model: Entidades JPA
   - View: JSON responses
   - Controller: REST Controllers

2. **Repository Pattern**:
   - Abstracción del acceso a datos
   - Spring Data JPA

3. **Dependency Injection**:
   - @Autowired, @Component, @Service
   - IoC Container de Spring

4. **DTO Pattern**:
   - Separación de entidades y datos de transferencia
   - ModelMapper para conversiones

---

## ⚛️ Arquitectura del Frontend

### Estructura de Componentes

```
src/
├── App.jsx                    # Componente raíz, routing
├── main.jsx                   # Entry point
│
├── pages/                     # Páginas principales
│   ├── LoginPage.jsx          # Autenticación
│   ├── SignupPage.jsx         # Registro
│   ├── DashboardPage.jsx      # Panel principal
│   ├── ExpensesPage.jsx       # Gestión de gastos
│   ├── PayrollPage.jsx        # Nóminas
│   ├── EmployeesPage.jsx      # Gestión de empleados
│   └── SettingsPage.jsx       # Configuración
│
├── components/                # Componentes reutilizables
│   ├── Layout.jsx             # Layout principal con sidebar
│   ├── Button.jsx             # Botón reutilizable
│   ├── Input.jsx              # Input reutilizable
│   ├── Card.jsx               # Tarjeta reutilizable
│   ├── Modal.jsx              # Modal reutilizable
│   └── ...
│
├── services/                  # Servicios de negocio
│   └── api.js                 # Cliente Axios, API calls
│
├── store/                     # State management (Zustand)
│   ├── authStore.js           # Estado de autenticación
│   └── expenseStore.js        # Estado de gastos
│
└── utils/                     # Utilidades
    ├── validators.js          # Validaciones
    └── formatters.js          # Formateadores
```

### Flujo de Datos en Frontend

```
User Action (Click, Input)
         │
         ▼
    Component
         │
         ├─► Local State (useState)
         │
         ├─► Global State (Zustand)
         │
         └─► API Call (Axios)
                  │
                  ▼
             Backend API
                  │
                  ▼
           Update State
                  │
                  ▼
           Re-render UI
```

---

## 💾 Modelo de Datos

### Diagrama ER (Entity-Relationship)

```
┌──────────────┐
│    User      │
├──────────────┤
│ id (PK)      │
│ email        │
│ password     │
│ created_at   │
└──────┬───────┘
       │ 1:N
       │
       ▼
┌──────────────┐         ┌──────────────┐
│  User_Role   │    N:M  │    Role      │
├──────────────┤◄────────┤──────────────┤
│ user_id (FK) │         │ id (PK)      │
│ role_id (FK) │         │ name         │
└──────────────┘         │ description  │
                         └──────────────┘

┌──────────────┐
│  Employee    │
├──────────────┤
│ id (PK)      │
│ name         │
│ surname      │
│ email        │
│ birth_date   │
│ start_date   │
│ status       │
│ position_id  │───┐
└──────┬───────┘   │
       │ 1:N       │
       │           │
       ▼           │ N:1
┌──────────────┐   │
│   Expense    │   │
├──────────────┤   │
│ id (PK)      │   │
│ amount       │   │
│ description  │   │
│ date         │   │
│ category     │   │
│ status       │   │
│ employee_id  │───┘
└──────┬───────┘
       │ 1:N
       │
       ▼
┌──────────────────┐
│ExpenseAttachment │
├──────────────────┤
│ id (PK)          │
│ filename         │
│ file_path        │
│ expense_id (FK)  │
└──────────────────┘

┌──────────────┐
│   Position   │
├──────────────┤
│ id (PK)      │
│ name         │
│ department   │
│ salary       │
│ description  │
└──────────────┘
       ▲
       │ 1:N
       │
┌──────┴───────┐
│  Employee    │
│ position_id  │
└──────┬───────┘
       │ 1:N
       │
       ▼
┌──────────────┐
│   Payroll    │
├──────────────┤
│ id (PK)      │
│ amount       │
│ period       │
│ payment_date │
│ status       │
│ deductions   │
│ net_amount   │
│ employee_id  │
└──────────────┘
```

### Entidades Principales

#### 1. User (Usuario)
```java
@Entity
public class User {
    @Id @GeneratedValue
    private Long id;
    
    @Column(unique = true)
    private String email;
    
    private String password; // BCrypt hashed
    
    @ManyToMany
    private Set<Role> roles;
    
    private LocalDateTime createdAt;
}
```

#### 2. Employee (Empleado)
```java
@Entity
public class Employee {
    @Id @GeneratedValue
    private Long id;
    
    private String name;
    private String surname;
    
    @Column(unique = true)
    private String email;
    
    private LocalDate birthDate;
    private LocalDate startDate;
    
    @Enumerated(EnumType.STRING)
    private EmployeeStatus status;
    
    @ManyToOne
    private Position position;
    
    @OneToMany(mappedBy = "employee")
    private List<Expense> expenses;
}
```

#### 3. Expense (Gasto)
```java
@Entity
public class Expense {
    @Id @GeneratedValue
    private Long id;
    
    private BigDecimal amount;
    private String description;
    private LocalDate date;
    private String category;
    
    @Enumerated(EnumType.STRING)
    private ExpenseStatus status; // PENDING, APPROVED, REJECTED
    
    @ManyToOne
    private Employee employee;
    
    @OneToMany(mappedBy = "expense")
    private List<ExpenseAttachment> attachments;
}
```

---

## 🔒 Seguridad

### Arquitectura de Seguridad

```
┌────────────────────────────────────────────────────────┐
│                    Client Request                       │
└──────────────────────┬─────────────────────────────────┘
                       │
                       ▼
┌────────────────────────────────────────────────────────┐
│              CORS Filter                                │
│  - Valida origen de la petición                        │
│  - Configura headers CORS                              │
└──────────────────────┬─────────────────────────────────┘
                       │
                       ▼
┌────────────────────────────────────────────────────────┐
│        JWT Authentication Filter                        │
│  - Extrae token del header Authorization               │
│  - Valida firma JWT                                    │
│  - Extrae claims (userId, email, roles)                │
│  - Crea SecurityContext                                │
└──────────────────────┬─────────────────────────────────┘
                       │
                       ▼
┌────────────────────────────────────────────────────────┐
│         Spring Security Filter Chain                    │
│  - Valida autenticación                                │
│  - Valida autorización (@PreAuthorize)                 │
└──────────────────────┬─────────────────────────────────┘
                       │
                       ▼
┌────────────────────────────────────────────────────────┐
│              Controller Method                          │
│  @PreAuthorize("hasRole('ROLE_ADMIN')")                │
└────────────────────────────────────────────────────────┘
```

### JWT Token Structure

```json
{
  "header": {
    "alg": "HS256",
    "typ": "JWT"
  },
  "payload": {
    "sub": "user@example.com",
    "userId": 123,
    "roles": ["ROLE_USER"],
    "iat": 1699564800,
    "exp": 1699651200
  },
  "signature": "..."
}
```

### Niveles de Seguridad

1. **Autenticación**: JWT tokens
2. **Autorización**: Role-based access control (RBAC)
3. **Encriptación**: BCrypt para contraseñas
4. **Transporte**: HTTPS en producción
5. **Validación**: Bean Validation en DTOs

---

## 🔄 Flujos de Datos

### Flujo de Autenticación

```
1. Usuario ingresa email/password en frontend
         │
         ▼
2. Frontend envía POST /api/v1/auth/login
         │
         ▼
3. Backend valida credenciales
         │
         ├─► Credenciales inválidas → 401 Unauthorized
         │
         └─► Credenciales válidas
                   │
                   ▼
4. Backend genera JWT token
         │
         ▼
5. Backend retorna token + user info
         │
         ▼
6. Frontend guarda token en Zustand store
         │
         ▼
7. Frontend incluye token en todas las peticiones:
   Authorization: Bearer <token>
```

### Flujo de Creación de Gasto

```
1. Usuario completa formulario de gasto
         │
         ▼
2. Frontend valida inputs localmente
         │
         ▼
3. Frontend envía POST /api/v1/expense/
   con Authorization header
         │
         ▼
4. Backend valida JWT token
         │
         ▼
5. Backend valida permisos (@PreAuthorize)
         │
         ▼
6. Backend valida datos (@Valid)
         │
         ▼
7. ExpenseService procesa lógica de negocio
         │
         ▼
8. ExpenseRepository guarda en BD
         │
         ▼
9. Backend retorna gasto creado (201 Created)
         │
         ▼
10. Frontend actualiza estado y UI
```

---

## 🎯 Decisiones de Diseño

### 1. ¿Por qué Spring Boot 3?
- **Soporte a largo plazo (LTS)**
- **Mejor rendimiento** que versiones 2.x
- **Java 21** con nuevas características
- **Actualizaciones de seguridad** frecuentes

### 2. ¿Por qué React con Vite?
- **Vite es más rápido** que Webpack
- **Hot Module Replacement** instantáneo
- **Build optimizado** para producción
- **React 18** con concurrent features

### 3. ¿Por qué JWT en lugar de sesiones?
- **Stateless**: No requiere almacenar sesiones en servidor
- **Escalable**: Funciona en arquitecturas distribuidas
- **Mobile-friendly**: Fácil de usar en apps móviles
- **CORS-friendly**: No depende de cookies

### 4. ¿Por qué PostgreSQL?
- **ACID compliant**: Garantía de transacciones
- **Open source**: Sin costos de licencia
- **Robustez**: Probado en producción
- **Extensible**: Soporte para JSON, arrays, etc.

### 5. ¿Por qué Zustand en lugar de Redux?
- **Más simple**: Menos boilerplate
- **Más pequeño**: ~1KB vs ~10KB
- **Performance**: Updates más eficientes
- **Developer Experience**: API más intuitiva

---

## 📊 Consideraciones de Escalabilidad

### Escalabilidad Horizontal

```
┌─────────────┐
│ Load        │
│ Balancer    │
└──────┬──────┘
       │
       ├──────┬──────┬──────┐
       │      │      │      │
┌──────▼─┐ ┌─▼────┐ ┌▼─────┐ ┌▼──────┐
│Backend │ │Backend│ │Backend│ │Backend│
│   1    │ │   2   │ │   3   │ │   N   │
└──────┬─┘ └─┬────┘ └┬─────┘ └┬──────┘
       │     │       │        │
       └─────┴───────┴────────┘
                │
         ┌──────▼──────┐
         │ PostgreSQL  │
         │   Cluster   │
         └─────────────┘
```

### Caché Strategy

- **Frontend**: Service Worker para assets estáticos
- **Backend**: Redis para sesiones y datos frecuentes
- **Database**: Query cache y índices optimizados

---

## 🔍 Monitoreo y Observabilidad

### Métricas Clave

1. **Application Metrics**:
   - Request rate
   - Response time
   - Error rate

2. **System Metrics**:
   - CPU usage
   - Memory usage
   - Disk I/O

3. **Business Metrics**:
   - Gastos creados por día
   - Tiempo promedio de aprobación
   - Usuarios activos

### Herramientas Recomendadas

- **Spring Boot Actuator**: Métricas del backend
- **Prometheus**: Recolección de métricas
- **Grafana**: Visualización
- **ELK Stack**: Logs centralizados

---

**Última actualización**: Diciembre 2024  
**Versión**: 1.0.0  
**Arquitecto**: ExpenseNoteApp Team
