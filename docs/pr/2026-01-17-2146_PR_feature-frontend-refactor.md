# Pull Request: feature/frontend-refactor → dev

**Fecha:** 2026-01-17  
**Rama origen:** `feature/frontend-refactor`  
**Rama destino:** `dev`  
**Commits:** 20+  
**Archivos modificados:** 55+  
**Líneas:** +5,500 / -800

---

## 📋 Resumen Ejecutivo

Esta rama representa una **refactorización integral del frontend** junto con **mejoras críticas de seguridad en el backend**, un **sistema de notificaciones en tiempo real con WebSocket**, y un **panel de administración CRUD completo**. El proyecto evoluciona de una aplicación de gestión de gastos básica a un **ERP empresarial completo** con interfaz moderna, responsive, autenticación robusta y capacidades de gestión avanzadas para administradores.

---

## 🎯 Objetivos Cumplidos

| Objetivo | Estado |
|----------|--------|
| Rediseño completo de UI (dark → light theme) | ✅ |
| Layout responsive mobile-first | ✅ |
| Seguridad: Usuarios ven solo sus nóminas | ✅ |
| Auto-linking Employee↔User por email | ✅ |
| Endpoint `/payroll/my` para usuario autenticado | ✅ |
| Bidirectional linking User↔Employee | ✅ |
| Limpieza templates Thymeleaf legacy | ✅ |
| Documentación OpenAPI/Swagger | ✅ |
| Normalización REST endpoints (sin trailing slash) | ✅ |
| Rebranding: Expense Note App → Bizflow ERP | ✅ |
| **Sistema de notificaciones en tiempo real (WebSocket)** | ✅ |
| **Panel de administración CRUD (Positions, Employees, Users)** | ✅ |
| **CRUD completo de Payroll para administradores** | ✅ |
| **Mejora de respuesta de login (id, roleId, roleName)** | ✅ |
| **Edición de gastos propios por usuarios** | ✅ |
| **Corrección de bugs en Dashboard y Payroll** | ✅ |

---

## 🔐 CAMBIOS DE SEGURIDAD (CRÍTICOS)

### 1. Endpoint `/payroll/my` - Visibilidad de Nóminas

**Problema resuelto:** Usuarios no-admin podían ver TODAS las nóminas del sistema.

**Solución implementada:**
```
GET /api/v1/payroll/my
Authorization: Bearer <token>
```

- Usuarios normales: Solo ven sus propias nóminas
- Administradores: Ven todas las nóminas via `/payroll`
- Consulta por `expense_user_id` Y `employee_id` (si está vinculado)

**Archivos modificados:**
- `PayrollControllerImpl.java` - Nuevo endpoint `/my`
- `PayrollServiceImpl.java` - Lógica de filtrado por usuario autenticado

### 2. Auto-linking Employee↔User

**Funcionalidad:** Cuando se crea/actualiza un Payroll, el sistema vincula automáticamente el `Employee` con el `ExpenseUser` si comparten el mismo email.

**Flujo:**
1. Usuario crea expense con employee_id
2. Sistema busca Employee por ID
3. Si email del Employee coincide con un ExpenseUser existente
4. Se establece la relación bidireccional

### 3. Bidirectional User↔Employee Linking

**Cambios en base de datos:**
```sql
-- sql/common/01_schema.sql
ALTER TABLE employee ADD COLUMN expense_user_id BIGINT UNIQUE;
ALTER TABLE employee ADD CONSTRAINT fk_employee_expense_user 
    FOREIGN KEY (expense_user_id) REFERENCES expense_user(id);
```

**Cambios en entidad:**
```java
// Employee.java
@OneToOne(mappedBy = "employee")
@JsonIgnore
private ExpenseUser expenseUser;

@JsonGetter("expenseUserId")
public Long getExpenseUserId() {
    return expenseUser != null ? expenseUser.getId() : null;
}
```

**Datos enlazados:** 46 de 61 employees vinculados con usuarios en todos los entornos.

---

## 🎨 REDISEÑO FRONTEND

### Transformación Visual

| Aspecto | Antes | Después |
|---------|-------|---------|
| Tema | Dark (oscuro) | Light (claro, elegante) |
| Estilo | Básico | Glass morphism + gradients |
| Responsive | Limitado | Mobile-first completo |
| Sidebar | Fija | Drawer móvil + fija desktop |
| Branding | Expense Note App | Bizflow ERP |

### Páginas Rediseñadas

#### LoginPage
- Hero section con gradiente verde → azul
- Formulario centrado con efecto glass
- Toggle para mostrar/ocultar contraseña
- Links a registro y recuperación
- Testimonios y beneficios (landing page style)

#### SignupPage  
- Layout two-column (form + benefits)
- Lista de beneficios con iconos
- Validación de formularios mejorada
- Diseño consistente con LoginPage

#### DashboardPage
- Stat cards coloridas (azul, verde, morado, naranja)
- Tabla de gastos con acciones (ver, editar, eliminar)
- Modal de detalle de gasto con animaciones
- Botón exportar CSV
- Filtros y búsqueda

#### PayrollPage
- Tabla responsive con datos de nóminas
- Filtrado automático según rol (admin vs user)
- Integración con `/payroll/my` endpoint
- Fix de roleId: 1=ADMIN, 2=USER, 3=MANAGER

### Componentes Actualizados

| Componente | Mejoras |
|------------|---------|
| `Button.jsx` | Variantes: primary, secondary, ghost, danger |
| `Card.jsx` | Efecto glass, bordes suaves, sombras |
| `Input.jsx` | Toggle password visibility, iconos |
| `Layout.jsx` | Sidebar responsive, drawer móvil, header mejorado |

### CSS y Estilos

**`index.css`** - 289 líneas añadidas:
- Variables CSS para colores y gradientes
- Clases utilitarias personalizadas
- Animaciones (fadeIn, slideIn, pulse)
- Responsive breakpoints
- Glass morphism effects

---

## 🧹 LIMPIEZA Y MANTENIMIENTO

### Templates Thymeleaf Eliminados
```
- backend/src/main/resources/templates/index.html  (60 líneas)
- backend/src/main/resources/templates/login.html  (95 líneas)
- backend/src/main/resources/templates/signup.html (95 líneas)
```

**Razón:** El proyecto ahora usa frontend SPA (React/Vite). Los templates Thymeleaf eran código legacy no utilizado.

### Configuración OpenAPI/Swagger

**Nuevo archivo:** `OpenApiConfig.java`
```java
@OpenAPIDefinition(
    info = @Info(
        title = "Bizflow ERP API",
        version = "1.0",
        description = "API REST para gestión empresarial"
    )
)
```

**RootController:** Redirección de `/` a `/swagger-ui.html`

---

## 📐 NORMALIZACIÓN REST API

### Convención Adoptada: Sin trailing slash

**Antes:**
```
GET /api/v1/payroll/
GET /api/v1/employee/
```

**Después:**
```
GET /api/v1/payroll
GET /api/v1/employee
```

**Controladores actualizados:**
- `PayrollControllerImpl.java`
- `EmployeeControllerImpl.java`
- `ExpenseControllerImpl.java`
- `PositionControllerImpl.java`
- `UserControllerImpl.java`
- `RoleControllerImpl.java`

**Frontend actualizado:**
- `api.js` - Todos los endpoints sin trailing slash

---

## 📁 Archivos Modificados por Categoría

### Backend (Java)
| Archivo | Cambios |
|---------|---------|
| `PayrollControllerImpl.java` | +21 líneas, endpoint `/my` |
| `PayrollServiceImpl.java` | +60 líneas, lógica autolinking |
| `UserServiceImpl.java` | +28 líneas, mejoras servicio |
| `Employee.java` | +10 líneas, relación bidireccional |
| `RootController.java` | +17 líneas, nuevo controlador |
| `OpenApiConfig.java` | Nuevo, configuración Swagger |
| Controladores REST | Normalización endpoints |

### Frontend (React/JSX)
| Archivo | Cambios |
|---------|---------|
| `LoginPage.jsx` | +328/-112 líneas, rediseño completo |
| `SignupPage.jsx` | +380/-160 líneas, rediseño completo |
| `DashboardPage.jsx` | +445/-120 líneas, nuevas funcionalidades |
| `PayrollPage.jsx` | +419/-25 líneas, integración /my |
| `Layout.jsx` | +246/-45 líneas, responsive sidebar |
| `Button.jsx` | +23 líneas, variantes |
| `Card.jsx` | +16 líneas, estilos |
| `Input.jsx` | +62 líneas, password toggle |
| `index.css` | +289 líneas, estilos globales |
| `api.js` | +15 líneas, normalización |

### SQL
| Archivo | Cambios |
|---------|---------|
| `01_schema.sql` | expense_user_id en employee |
| `05_expense_admin_bootstrap.sql` | Linking bidireccional |
| `15_expense_users_seed.sql` | Linking bidireccional |

### Documentación
| Archivo | Descripción |
|---------|-------------|
| `2026-01-12-6-summary-0106.md` | CI/CD fixes, env standardization |
| `2026-01-13-6-summary-0034.md` | UI redesign session |
| `2026-01-14-6-summary-0035.md` | Password migration, frontend |
| `2026-01-15-6-summary-0015.md` | Security fix planning |

---

## 🧪 Testing Realizado

### Entornos Verificados

| Entorno | Backend | Database | Frontend | Estado |
|---------|---------|----------|----------|--------|
| DEV | :8082 ✅ | :5433 ✅ | Vite :3000 | ✅ |
| TEST | :8083 ✅ | :5434 ✅ | :8086 ✅ | ✅ |
| PROD | :8181 ✅ | :5442 ✅ | :8080 ✅ | ✅ |

### Datos Verificados (por entorno)
- 61 employees
- 46 expense_users (todos vinculados a employees)
- 305 payrolls
- Relación bidireccional Employee↔User funcionando

### Endpoints Probados
- `POST /api/v1/auth/login` ✅
- `GET /api/v1/payroll/my` ✅
- `GET /api/v1/employee/{id}` (retorna expenseUserId) ✅
- `GET /api/v1/payroll` (admin only) ✅

---

## 📝 Commits Detallados

```
06937cc feat: add bidirectional User↔Employee linking
26351e1 refactor: normalize REST endpoints - remove trailing slashes
e17cf5f docs: add session summaries (2026-01-12 to 2026-01-15)
5552aba feat(frontend): enhance Dashboard with actions and improve components
ab6c9ce chore(backend): cleanup Thymeleaf templates and add OpenAPI config
3730d31 feat(frontend): use /payroll/my endpoint in PayrollPage
3e6b881 feat(api): add /payroll/my endpoint with auto-linking Employee↔User
e1af0ab feat(frontend): redesign UI with light theme and responsive layout
```

---

## ⚠️ Notas de Migración

### Para DEV/TEST
No requiere acciones adicionales. Los seeds se ejecutan automáticamente.

### Para PROD
1. La columna `expense_user_id` se añade automáticamente
2. El UPDATE para linking se ejecuta en el seed
3. **Verificar** que 46 usuarios estén correctamente vinculados

### Breaking Changes
- Endpoints ahora **sin** trailing slash
- Frontend requiere proxy configurado a puerto correcto
- Templates Thymeleaf eliminados (no afecta si usas SPA)

---

## 🔮 Próximos Pasos (Post-Merge)

1. **Crear rama desde dev** para nuevos registros de seed
2. **Considerar contraseñas PROD diferenciadas** (más seguras)
3. **Añadir tests E2E** para nuevos endpoints
4. **Documentar API** completamente en Swagger

---

## 🔔 SISTEMA DE NOTIFICACIONES EN TIEMPO REAL (Sesión 16-17 Enero 2026)

### Arquitectura WebSocket con STOMP

Se ha implementado un sistema completo de notificaciones en tiempo real utilizando **WebSocket con STOMP sobre SockJS**. Este sistema permite a los usuarios recibir notificaciones instantáneas cuando ocurren eventos relevantes en la aplicación.

#### Componentes Backend

**Configuración WebSocket (`WebSocketConfig.java`):**
```java
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
}
```

**Entidad Notification:**
- `id`, `userId`, `type`, `title`, `message`, `relatedEntityId`, `relatedEntityType`
- `isRead`, `createdAt`
- Tipos: `EXPENSE_CREATED`, `EXPENSE_UPDATED`, `EXPENSE_DELETED`, `PAYROLL_CREATED`, `PAYROLL_UPDATED`, `PAYROLL_DELETED`, `SYSTEM`, `INFO`, `WARNING`

**Servicio de Notificaciones (`NotificationServiceImpl.java`):**
- Creación y persistencia de notificaciones
- Envío en tiempo real vía `SimpMessagingTemplate`
- Marcado como leído (individual y masivo)
- Consulta de notificaciones no leídas
- Eliminación de notificaciones

**Controlador REST (`NotificationController.java`):**
```
GET    /api/v1/notifications           → Todas las notificaciones del usuario
GET    /api/v1/notifications/unread    → Solo no leídas
GET    /api/v1/notifications/unread/count → Contador de no leídas
PUT    /api/v1/notifications/{id}/read → Marcar como leída
PUT    /api/v1/notifications/read-all  → Marcar todas como leídas
DELETE /api/v1/notifications/{id}      → Eliminar notificación
```

**Triggers de Notificación:**
- `ExpenseControllerImpl`: Notifica a admins cuando se crea/actualiza/elimina un gasto
- `PayrollControllerImpl`: Notifica al empleado cuando se crea/actualiza/elimina su nómina

#### Componentes Frontend

**Store de Notificaciones (`notificationStore.js`):**
```javascript
// Zustand store con persistencia
- notifications: array de notificaciones
- unreadCount: contador de no leídas
- addNotification: añade y actualiza contador
- markAsRead: marca individual
- markAllAsRead: marca todas
- fetchNotifications: carga inicial desde API
```

**Cliente WebSocket (`websocket.js`):**
```javascript
// Conexión STOMP sobre SockJS
- connectWebSocket(userId): establece conexión
- Suscripción a /user/{userId}/queue/notifications
- Auto-reconexión en caso de desconexión
- Integración con notificationStore
```

**Componente NotificationBell (`NotificationBell.jsx`):**
- Icono de campana con badge de contador
- Dropdown con lista de notificaciones
- Iconos por tipo (gasto, nómina, sistema)
- Colores diferenciados según tipo
- Botón "Marcar todas como leídas"
- Formateo relativo de fechas ("hace 5 min", "ayer")
- Animaciones con Framer Motion

**Dependencias añadidas:**
```json
"@stomp/stompjs": "^7.0.0",
"sockjs-client": "^1.6.1",
"@heroicons/react": "^2.2.0"
```

#### Seguridad

**Configuración en `AppSecurityConfig.java`:**
```java
.requestMatchers("/ws/**").permitAll()  // WebSocket endpoint público
```

El sistema utiliza autenticación a nivel de aplicación: el `userId` se pasa al conectar y las notificaciones se filtran por usuario en el backend.

---

## 🛠️ PANEL DE ADMINISTRACIÓN CRUD (Sesión 17 Enero 2026)

### Páginas de Gestión para Administradores

Se han implementado 4 páginas CRUD completas accesibles únicamente para usuarios con rol de administrador (`roleId === 1`).

#### 1. Gestión de Cargos (`PositionsPage.jsx`) - Ruta: `/positions`

**Funcionalidades:**
- **Listado**: Tabla con nombre, descripción y salario base
- **Búsqueda**: Filtro en tiempo real por nombre o descripción
- **Crear**: Modal con formulario validado (nombre obligatorio, salario > 0)
- **Editar**: Modal precargado con datos existentes
- **Eliminar**: Confirmación antes de eliminar (aviso de empleados asociados)

**Campos del formulario:**
- Nombre del cargo (obligatorio)
- Descripción (opcional)
- Salario base en EUR (obligatorio, validación numérica)

**Diseño:**
- Icono de maletín (Briefcase) para cada cargo
- Formateo de moneda con `Intl.NumberFormat`
- Mensaje de "sin permisos" para no-admins

#### 2. Gestión de Empleados (`EmployeesPage.jsx`) - Ruta: `/employees`

**Funcionalidades:**
- **Listado**: Tabla con avatar, nombre, email, cargo y fecha de nacimiento
- **Búsqueda**: Filtro por nombre completo o email
- **Crear**: Modal con selector de cargo (dropdown dinámico)
- **Editar**: Parseo inteligente de fechas (array LocalDateTime o string ISO)
- **Eliminar**: Aviso de eliminación de nóminas y gastos asociados

**Campos del formulario:**
- Nombre (obligatorio)
- Apellido (obligatorio)
- Email (opcional, validación de formato)
- Fecha de nacimiento (obligatorio, input type="date")
- Cargo (obligatorio, selector con cargos desde API)

**Diseño:**
- Avatar con iniciales y gradiente azul-índigo
- Badge de cargo con estilo pill
- Carga paralela de empleados y cargos (`Promise.all`)

#### 3. Gestión de Usuarios (`UsersPage.jsx`) - Ruta: `/users`

**Funcionalidades:**
- **Listado**: Tabla con avatar, nombre, email y rol
- **Búsqueda**: Filtro por nombre completo o email
- **Crear**: Formulario con contraseña obligatoria y selector de rol
- **Editar**: Contraseña opcional (campo vacío = mantener actual)
- **Eliminar**: Protección contra auto-eliminación
- **Toggle contraseña**: Botón para mostrar/ocultar

**Campos del formulario:**
- Nombre (obligatorio)
- Apellido (obligatorio)
- Email (obligatorio, validación formato)
- Contraseña (obligatoria en creación, opcional en edición, mínimo 6 caracteres)
- Rol (obligatorio, selector dinámico desde API `/role`)
- Empleado asociado (opcional, selector dinámico)

**Diseño:**
- Avatar con gradiente índigo-púrpura
- Badge de rol con colores: Admin (púrpura), User (verde), otros (gris)
- Indicador "(Tú)" para el usuario actual
- Botón eliminar deshabilitado para usuario actual
- Highlight azul para fila del usuario actual

#### 4. CRUD en Nóminas (`PayrollPage.jsx`) - Mejoras para Admin

**Nuevas funcionalidades para administradores:**
- **Botón "Nueva Nómina"**: Visible solo para admins
- **Botón Editar**: En cada fila de la tabla
- **Botón Eliminar**: Con confirmación
- **Modal de creación/edición**: Selector de empleado, monto y fecha

**Campos del formulario:**
- Empleado (obligatorio, selector dinámico)
- Monto en $ (obligatorio, validación > 0)
- Fecha de pago (obligatorio, input type="date")

**Servicio API (`payrollAdminService`):**
```javascript
export const payrollAdminService = {
  create: (data) => apiClient.post('/payroll/', data),
  update: (id, data) => apiClient.put(`/payroll/${id}`, data),
  delete: (id) => apiClient.delete(`/payroll/${id}`),
};
```

### Navegación Condicional

**Layout.jsx - Menú lateral dinámico:**
```javascript
const isAdmin = user?.roleId === 1;

const menuItems = [
  { icon: Home, label: 'Dashboard', path: '/dashboard' },
  { icon: CreditCard, label: 'Gastos', path: '/expenses' },
  { icon: Wallet, label: 'Nómina', path: '/payroll' },
  ...(isAdmin ? [
    { icon: Briefcase, label: 'Cargos', path: '/positions' },
    { icon: Users, label: 'Empleados', path: '/employees' },
    { icon: UserCog, label: 'Usuarios', path: '/users' },
  ] : []),
  { icon: User, label: 'Perfil', path: '/profile' },
  { icon: Settings, label: 'Configuración', path: '/settings' },
];
```

**App.jsx - Rutas añadidas:**
```jsx
<Route path="/positions" element={<PositionsPage />} />
<Route path="/employees" element={<EmployeesPage />} />
<Route path="/users" element={<UsersPage />} />
```

---

## 🔧 MEJORAS DE AUTENTICACIÓN (Sesión 16-17 Enero 2026)

### Respuesta de Login Mejorada

**Problema:** El frontend necesitaba el `roleId` para determinar permisos, pero solo recibía el token.

**Solución implementada en `AuthenticationController.java`:**
```java
// Extraer información del rol
Long roleId = null;
String roleName = null;
if (!user.getRoles().isEmpty()) {
    Role role = user.getRoles().iterator().next();
    roleId = role.getId();
    roleName = role.getName();
}

return new AuthenticationResponse(
    user.getId(),
    token,
    roleId,
    roleName
);
```

**AuthenticationResponse.java actualizado:**
```java
public class AuthenticationResponse {
    private Long id;
    private String token;
    private Long roleId;
    private String roleName;
}
```

**Frontend (`LoginPage.jsx`, `SignupPage.jsx`):**
```javascript
// Almacena datos completos en authStore
login(response.data.token, {
  id: response.data.id,
  email: formData.email,
  roleId: response.data.roleId,
  roleName: response.data.roleName
});
```

---

## 🐛 CORRECCIÓN DE BUGS (Sesión 16-17 Enero 2026)

### 1. Dashboard - Fecha "Invalid Date"

**Problema:** Las fechas se mostraban como "Invalid Date" porque el backend devuelve arrays `[2026, 1, 15, 10, 30]` en lugar de strings ISO.

**Solución en `DashboardPage.jsx`:**
```javascript
const formatDate = (dateValue) => {
  if (!dateValue) return 'Sin fecha';
  
  let date;
  if (Array.isArray(dateValue)) {
    const [year, month, day, hour = 0, minute = 0] = dateValue;
    date = new Date(year, month - 1, day, hour, minute);
  } else {
    date = new Date(dateValue);
  }
  
  if (isNaN(date.getTime())) return 'Fecha inválida';
  
  return date.toLocaleDateString('es-ES', {
    day: '2-digit', month: 'short', year: 'numeric'
  });
};
```

### 2. Dashboard - Campo "description" inexistente

**Problema:** El código buscaba `expense.description` pero el campo real es `expense.concept`.

**Solución:** Cambiar todas las referencias de `description` a `concept`.

### 3. Dashboard - Usuarios veían todos los gastos

**Problema:** La llamada `expenseService.getAll()` mostraba todos los gastos del sistema.

**Solución:**
```javascript
if (user?.roleId === 1) {
  response = await expenseService.getAll();
} else {
  response = await expenseService.getByUserId(user.id);
}
```

### 4. PayrollPage - Nombre de empleado vacío

**Problema:** El nombre del empleado aparecía vacío en la tabla.

**Solución en `getEmployeeName()`:**
```javascript
const getEmployeeName = (payroll) => {
  // Primero campos directos del payload
  if (payroll.employeeName && payroll.employeeSurname) {
    return `${payroll.employeeName} ${payroll.employeeSurname}`;
  }
  // Luego buscar en lista de empleados
  const employee = employees.find(e => e.id === payroll.employeeId);
  if (employee) {
    return `${employee.name} ${employee.surname}`;
  }
  // Fallback al perfil del usuario
  if (viewMode === 'mine' && profile?.name) {
    return `${profile.name} ${profile.surname}`;
  }
  return payroll.employeeId ? `Empleado #${payroll.employeeId}` : 'Freelance';
};
```

### 5. ExpenseList - Fecha con formato array

**Problema:** Mismo problema de fechas array en la lista de gastos.

**Solución:** Aplicar el mismo parser de fechas en `ExpenseList.jsx`.

### 6. Expenses - Usuarios no podían editar sus gastos

**Problema:** Solo admins podían editar gastos.

**Solución en `ExpenseServiceImpl.java`:**
```java
// Permitir edición si es admin O si es el propietario del gasto
if (!hasAdminRole && !expense.getExpenseUser().getId().equals(currentUser.getId())) {
    throw new UnauthorizedException("No tienes permisos para editar este gasto");
}
```

---

## 📊 RESUMEN DE COMMITS (Sesiones 16-17 Enero 2026)

```
6894a79 feat(auth): add user id, role id and role name to login response
cff403f feat(notifications): implement real-time notification system with WebSocket
bbefa1a feat(frontend): add notification bell component with store and WebSocket integration
5592f39 feat(expenses): enable expense edit for owners and fix field names in dashboard
4659f51 feat(payroll): add pagination, admin toggle, and fix employee name display
f2b82c2 docs(sessions): add session summaries for January 16-17, 2026
9ec7764 feat(controllers): add notifications triggers for expense and payroll CRUD operations
2a8b46e feat(admin): add positions CRUD page with search and modal form
29812c1 feat(admin): add employees CRUD page with position selection
4d0e5aa feat(admin): add users CRUD page with role and employee assignment
e3dd199 feat(admin): add payroll CRUD operations for admin users
28c51b9 feat(routing): add admin menu and routes for positions, employees and users
```

---

## 📁 Archivos Nuevos (Sesiones 16-17 Enero 2026)

### Backend
| Archivo | Descripción |
|---------|-------------|
| `WebSocketConfig.java` | Configuración STOMP/SockJS |
| `Notification.java` | Entidad de notificaciones |
| `NotificationType.java` | Enum de tipos de notificación |
| `NotificationDto.java` | DTO para respuestas |
| `NotificationDao.java` | Repositorio JPA |
| `INotificationService.java` | Interfaz del servicio |
| `NotificationServiceImpl.java` | Implementación del servicio |
| `NotificationController.java` | Controlador REST |

### Frontend
| Archivo | Descripción |
|---------|-------------|
| `NotificationBell.jsx` | Componente de campana con dropdown |
| `notificationStore.js` | Store Zustand para notificaciones |
| `websocket.js` | Cliente STOMP para WebSocket |
| `PositionsPage.jsx` | CRUD de cargos |
| `EmployeesPage.jsx` | CRUD de empleados |
| `UsersPage.jsx` | CRUD de usuarios |

### Dependencias
| Paquete | Versión | Uso |
|---------|---------|-----|
| `spring-boot-starter-websocket` | 3.3.4 | WebSocket backend |
| `@stomp/stompjs` | 7.0.0 | Cliente STOMP |
| `sockjs-client` | 1.6.1 | Fallback WebSocket |
| `@heroicons/react` | 2.2.0 | Iconos de notificaciones |

---

## ✅ Checklist Pre-Merge

- [x] Todos los entornos funcionando (DEV, TEST, PROD)
- [x] Bidirectional linking verificado en DB
- [x] Endpoints REST normalizados
- [x] Frontend responsive probado
- [x] Sin errores de compilación backend
- [x] Documentación actualizada
- [x] Commits con mensajes descriptivos
- [x] Sistema de notificaciones WebSocket funcionando
- [x] Panel CRUD de administración completo
- [x] Bugs de Dashboard y Payroll corregidos
- [x] Respuesta de login incluye roleId y roleName

---

**Autor:** @bytetech  
**Revisores sugeridos:** -  
**Labels:** `enhancement`, `security`, `frontend`, `refactor`, `websocket`, `admin-panel`
