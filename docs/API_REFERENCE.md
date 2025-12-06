# API Reference - ExpenseNoteApp v1.1.0

Documentación completa de los endpoints REST API del sistema de gestión de gastos empresariales.

## 📋 Tabla de Contenidos

1. [Información General](#información-general)
2. [Autenticación](#autenticación)
3. [Endpoints de Autenticación](#endpoints-de-autenticación)
4. [Endpoints de Gastos](#endpoints-de-gastos)
5. [Endpoints de Empleados](#endpoints-de-empleados)
6. [Endpoints de Nómina](#endpoints-de-nómina)
7. [Endpoints de Puestos](#endpoints-de-puestos)
8. [Endpoints de Usuarios](#endpoints-de-usuarios)
9. [Endpoints de Roles](#endpoints-de-roles)
10. [Códigos de Respuesta](#códigos-de-respuesta)
11. [Swagger UI](#swagger-ui)

---

## 🌐 Información General

### Base URL
```
http://localhost:8080/api/v1
```

### Formato de Datos
- **Request**: `application/json`
- **Response**: `application/json`

### Versionamiento
La API utiliza versionamiento mediante URL path (`/api/v1/`).

### CORS
La API permite peticiones desde cualquier origen (`*`). En producción se recomienda restringir esto.

---

## 🔐 Autenticación

La API utiliza **JWT (JSON Web Tokens)** para autenticación.

### Header de Autenticación
```http
Authorization: Bearer <jwt_token>
```

### Roles Disponibles
- `ROLE_ADMIN`: Acceso completo al sistema
- `ROLE_USER`: Acceso limitado a recursos propios

---

## 🔑 Endpoints de Autenticación

### POST /auth/login
Iniciar sesión y obtener token JWT.

**Request Body:**
```json
{
  "email": "admin@example.com",
  "password": "admin123"
}
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "id": 1,
  "email": "admin@example.com",
  "roles": ["ROLE_ADMIN"]
}
```

**Errores:**
- `401 Unauthorized`: Credenciales inválidas

---

### POST /auth/signup
Registrar nuevo usuario.

**Request Body:**
```json
{
  "name": "John",
  "surname": "Doe",
  "email": "john.doe@example.com",
  "password": "securePassword123"
}
```

**Response (201 Created):**
```json
{
  "id": 5,
  "name": "John",
  "surname": "Doe",
  "email": "john.doe@example.com",
  "roles": ["ROLE_USER"]
}
```

**Errores:**
- `400 Bad Request`: Datos inválidos o email duplicado
- `500 Internal Server Error`: Error del servidor

---

## 💰 Endpoints de Gastos

Base path: `/api/v1/expense`

### GET /expense/
Obtener todos los gastos (solo ADMIN).

**Headers:**
```http
Authorization: Bearer <token>
```

**Permisos:** `ROLE_ADMIN`

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "amount": 150.50,
    "description": "Viaje de negocios",
    "date": "2024-11-15",
    "category": "Transporte",
    "status": "APPROVED",
    "employee": {
      "id": 3,
      "name": "Jane",
      "surname": "Smith"
    }
  }
]
```

---

### GET /expense/employee/{employeeId}
Obtener gastos de un empleado específico.

**Headers:**
```http
Authorization: Bearer <token>
```

**Permisos:** `ROLE_ADMIN`, `ROLE_USER` (solo sus propios gastos)

**Path Parameters:**
- `employeeId` (Long): ID del empleado

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "amount": 150.50,
    "description": "Viaje de negocios",
    "date": "2024-11-15",
    "category": "Transporte",
    "status": "PENDING"
  }
]
```

**Errores:**
- `403 Forbidden`: Usuario sin permisos para ver gastos de otro empleado
- `404 Not Found`: Empleado no encontrado

---

### GET /expense/{expenseId}
Obtener un gasto específico por ID.

**Headers:**
```http
Authorization: Bearer <token>
```

**Permisos:** `ROLE_ADMIN`, `ROLE_USER` (solo sus propios gastos)

**Path Parameters:**
- `expenseId` (Long): ID del gasto

**Response (200 OK):**
```json
{
  "id": 1,
  "amount": 150.50,
  "description": "Viaje de negocios",
  "date": "2024-11-15",
  "category": "Transporte",
  "status": "PENDING",
  "employee": {
    "id": 3,
    "name": "Jane",
    "surname": "Smith"
  }
}
```

---

### POST /expense/
Crear un nuevo gasto.

**Headers:**
```http
Authorization: Bearer <token>
```

**Permisos:** `ROLE_ADMIN`, `ROLE_USER`

**Request Body:**
```json
{
  "amount": 150.50,
  "description": "Viaje de negocios",
  "date": "2024-11-15",
  "category": "Transporte",
  "employeeId": 3
}
```

**Response (201 Created):**
```json
{
  "id": 10,
  "amount": 150.50,
  "description": "Viaje de negocios",
  "date": "2024-11-15",
  "category": "Transporte",
  "status": "PENDING",
  "employee": {
    "id": 3,
    "name": "Jane",
    "surname": "Smith"
  }
}
```

**Validaciones:**
- `amount`: Debe ser mayor a 0
- `description`: No puede estar vacío (max 500 caracteres)
- `date`: Debe ser una fecha válida
- `employeeId`: Debe existir en la base de datos

---

### PUT /expense/{expenseId}
Actualizar un gasto existente.

**Headers:**
```http
Authorization: Bearer <token>
```

**Permisos:** `ROLE_ADMIN`, `ROLE_USER` (solo sus propios gastos)

**Path Parameters:**
- `expenseId` (Long): ID del gasto

**Request Body:**
```json
{
  "amount": 175.00,
  "description": "Viaje de negocios actualizado",
  "date": "2024-11-15",
  "category": "Transporte"
}
```

**Response (200 OK):**
```json
{
  "id": 10,
  "amount": 175.00,
  "description": "Viaje de negocios actualizado",
  "date": "2024-11-15",
  "category": "Transporte",
  "status": "PENDING"
}
```

---

### DELETE /expense/{expenseId}
Eliminar un gasto.

**Headers:**
```http
Authorization: Bearer <token>
```

**Permisos:** `ROLE_ADMIN`, `ROLE_USER` (solo sus propios gastos)

**Path Parameters:**
- `expenseId` (Long): ID del gasto

**Response (200 OK):**
```json
{
  "message": "Expense deleted successfully"
}
```

---

### PUT /expense/{expenseId}/approve
Aprobar un gasto (solo ADMIN).

**Headers:**
```http
Authorization: Bearer <token>
```

**Permisos:** `ROLE_ADMIN`

**Path Parameters:**
- `expenseId` (Long): ID del gasto

**Response (200 OK):**
```json
{
  "id": 10,
  "status": "APPROVED",
  "approvedBy": "admin@example.com",
  "approvedAt": "2024-11-16T10:30:00"
}
```

---

### PUT /expense/{expenseId}/reject
Rechazar un gasto (solo ADMIN).

**Headers:**
```http
Authorization: Bearer <token>
```

**Permisos:** `ROLE_ADMIN`

**Path Parameters:**
- `expenseId` (Long): ID del gasto

**Request Body (opcional):**
```json
{
  "reason": "Falta documentación de soporte"
}
```

**Response (200 OK):**
```json
{
  "id": 10,
  "status": "REJECTED",
  "rejectedBy": "admin@example.com",
  "rejectedAt": "2024-11-16T10:35:00",
  "rejectionReason": "Falta documentación de soporte"
}
```

---

## 👥 Endpoints de Empleados

Base path: `/api/v1/employee`

### GET /employee/
Obtener todos los empleados.

**Headers:**
```http
Authorization: Bearer <token>
```

**Permisos:** `ROLE_ADMIN`, `ROLE_USER`

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "John",
    "surname": "Doe",
    "email": "john.doe@company.com",
    "birthDate": "1990-05-15",
    "startDate": "2020-01-10",
    "status": "ACTIVE",
    "position": {
      "id": 1,
      "name": "Software Developer"
    }
  }
]
```

---

### GET /employee/{employeeId}
Obtener un empleado por ID.

**Headers:**
```http
Authorization: Bearer <token>
```

**Permisos:** `ROLE_ADMIN`, `ROLE_USER`

**Path Parameters:**
- `employeeId` (Long): ID del empleado

**Response (200 OK):**
```json
{
  "id": 1,
  "name": "John",
  "surname": "Doe",
  "email": "john.doe@company.com",
  "birthDate": "1990-05-15",
  "startDate": "2020-01-10",
  "status": "ACTIVE",
  "position": {
    "id": 1,
    "name": "Software Developer",
    "salary": 75000.00
  }
}
```

---

### POST /employee/
Crear un nuevo empleado.

**Headers:**
```http
Authorization: Bearer <token>
```

**Permisos:** `ROLE_ADMIN`

**Request Body:**
```json
{
  "name": "Jane",
  "surname": "Smith",
  "email": "jane.smith@company.com",
  "birthDate": "1992-08-20",
  "startDate": "2024-11-01",
  "status": "ACTIVE",
  "positionId": 2
}
```

**Response (201 Created):**
```json
{
  "id": 5,
  "name": "Jane",
  "surname": "Smith",
  "email": "jane.smith@company.com",
  "birthDate": "1992-08-20",
  "startDate": "2024-11-01",
  "status": "ACTIVE",
  "position": {
    "id": 2,
    "name": "Project Manager"
  }
}
```

**Validaciones:**
- `name`: 3-128 caracteres
- `surname`: 2-255 caracteres
- `email`: Formato válido y único
- `birthDate`: Fecha pasada o presente
- `startDate`: Requerido
- `status`: ACTIVE, INACTIVE, o TERMINATED

---

### PUT /employee/{employeeId}
Actualizar un empleado existente.

**Headers:**
```http
Authorization: Bearer <token>
```

**Permisos:** `ROLE_ADMIN`

**Path Parameters:**
- `employeeId` (Long): ID del empleado

**Request Body:**
```json
{
  "name": "Jane",
  "surname": "Smith",
  "email": "jane.smith@company.com",
  "status": "INACTIVE"
}
```

**Response (200 OK):**
```json
{
  "id": 5,
  "name": "Jane",
  "surname": "Smith",
  "status": "INACTIVE"
}
```

---

### DELETE /employee/{employeeId}
Eliminar un empleado.

**Headers:**
```http
Authorization: Bearer <token>
```

**Permisos:** `ROLE_ADMIN`

**Path Parameters:**
- `employeeId` (Long): ID del empleado

**Response (200 OK):**
```json
{
  "message": "Employee deleted successfully"
}
```

---

## 💵 Endpoints de Nómina

Base path: `/api/v1/payroll`

### GET /payroll/
Obtener todas las nóminas.

**Headers:**
```http
Authorization: Bearer <token>
```

**Permisos:** `ROLE_ADMIN`

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "employee": {
      "id": 1,
      "name": "John",
      "surname": "Doe"
    },
    "amount": 5000.00,
    "period": "2024-11",
    "paymentDate": "2024-11-30",
    "status": "PAID"
  }
]
```

---

### GET /payroll/employee/{employeeId}
Obtener nóminas de un empleado específico.

**Headers:**
```http
Authorization: Bearer <token>
```

**Permisos:** `ROLE_ADMIN`, `ROLE_USER` (solo sus propias nóminas)

**Path Parameters:**
- `employeeId` (Long): ID del empleado

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "amount": 5000.00,
    "period": "2024-11",
    "paymentDate": "2024-11-30",
    "status": "PAID",
    "deductions": 500.00,
    "netAmount": 4500.00
  }
]
```

---

### POST /payroll/
Crear una nueva nómina.

**Headers:**
```http
Authorization: Bearer <token>
```

**Permisos:** `ROLE_ADMIN`

**Request Body:**
```json
{
  "employeeId": 1,
  "amount": 5000.00,
  "period": "2024-11",
  "paymentDate": "2024-11-30",
  "deductions": 500.00
}
```

**Response (201 Created):**
```json
{
  "id": 10,
  "employee": {
    "id": 1,
    "name": "John",
    "surname": "Doe"
  },
  "amount": 5000.00,
  "period": "2024-11",
  "paymentDate": "2024-11-30",
  "deductions": 500.00,
  "netAmount": 4500.00,
  "status": "PENDING"
}
```

---

## 📋 Endpoints de Puestos

Base path: `/api/v1/position`

### GET /position/
Obtener todos los puestos.

**Headers:**
```http
Authorization: Bearer <token>
```

**Permisos:** `ROLE_ADMIN`, `ROLE_USER`

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "Software Developer",
    "department": "IT",
    "salary": 75000.00,
    "description": "Desarrolla y mantiene aplicaciones"
  }
]
```

---

### POST /position/
Crear un nuevo puesto.

**Headers:**
```http
Authorization: Bearer <token>
```

**Permisos:** `ROLE_ADMIN`

**Request Body:**
```json
{
  "name": "Senior Developer",
  "department": "IT",
  "salary": 95000.00,
  "description": "Lidera proyectos de desarrollo"
}
```

**Response (201 Created):**
```json
{
  "id": 5,
  "name": "Senior Developer",
  "department": "IT",
  "salary": 95000.00,
  "description": "Lidera proyectos de desarrollo"
}
```

---

## 👤 Endpoints de Usuarios

Base path: `/api/v1/user`

### GET /user/
Obtener todos los usuarios.

**Headers:**
```http
Authorization: Bearer <token>
```

**Permisos:** `ROLE_ADMIN`

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "email": "admin@example.com",
    "roles": ["ROLE_ADMIN"],
    "enabled": true,
    "createdAt": "2024-01-01T00:00:00"
  }
]
```

---

### GET /user/{userId}
Obtener un usuario por ID.

**Headers:**
```http
Authorization: Bearer <token>
```

**Permisos:** `ROLE_ADMIN`

**Path Parameters:**
- `userId` (Long): ID del usuario

**Response (200 OK):**
```json
{
  "id": 1,
  "email": "admin@example.com",
  "roles": ["ROLE_ADMIN"],
  "enabled": true,
  "createdAt": "2024-01-01T00:00:00"
}
```

---

## 🔐 Endpoints de Roles

Base path: `/api/v1/role`

### GET /role/
Obtener todos los roles.

**Headers:**
```http
Authorization: Bearer <token>
```

**Permisos:** `ROLE_ADMIN`

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "ROLE_ADMIN",
    "description": "Administrator role with full access"
  },
  {
    "id": 2,
    "name": "ROLE_USER",
    "description": "Standard user role with limited access"
  }
]
```

---

### POST /role/assign
Asignar rol a un usuario.

**Headers:**
```http
Authorization: Bearer <token>
```

**Permisos:** `ROLE_ADMIN`

**Request Body:**
```json
{
  "userId": 5,
  "roleId": 2
}
```

**Response (200 OK):**
```json
{
  "message": "Role assigned successfully",
  "user": {
    "id": 5,
    "email": "user@example.com",
    "roles": ["ROLE_USER"]
  }
}
```

---

## 📊 Códigos de Respuesta

### Códigos de Éxito
- **200 OK**: Solicitud exitosa
- **201 Created**: Recurso creado exitosamente
- **204 No Content**: Solicitud exitosa sin contenido de respuesta

### Códigos de Error del Cliente
- **400 Bad Request**: Datos de entrada inválidos
- **401 Unauthorized**: Autenticación requerida o token inválido
- **403 Forbidden**: Sin permisos para acceder al recurso
- **404 Not Found**: Recurso no encontrado
- **409 Conflict**: Conflicto con el estado actual (ej: email duplicado)

### Códigos de Error del Servidor
- **500 Internal Server Error**: Error interno del servidor
- **503 Service Unavailable**: Servicio temporalmente no disponible

### Formato de Errores
```json
{
  "error": "Descripción del error",
  "message": "Mensaje detallado",
  "timestamp": "2024-11-16T10:30:00",
  "path": "/api/v1/expense/999"
}
```

---

## 📚 Swagger UI

### Acceso a la Interfaz Swagger

Una vez que el backend esté en ejecución, puedes acceder a la interfaz Swagger UI en:

```
http://localhost:8080/swagger-ui.html
```

O también en:

```
http://localhost:8080/swagger-ui/index.html
```

### Documentación OpenAPI JSON

El archivo JSON de la especificación OpenAPI está disponible en:

```
http://localhost:8080/v3/api-docs
```

### Características de Swagger UI

- **Exploración interactiva**: Prueba los endpoints directamente desde el navegador
- **Autenticación**: Usa el botón "Authorize" para incluir tu token JWT
- **Schemas**: Visualiza los modelos de datos y sus validaciones
- **Ejemplos**: Ve ejemplos de request/response para cada endpoint

### Cómo usar Swagger UI

1. Inicia el backend: `mvn spring-boot:run`
2. Abre tu navegador en `http://localhost:8080/swagger-ui.html`
3. Click en "Authorize" e ingresa tu token JWT (sin "Bearer")
4. Explora y prueba los endpoints

---

## 📝 Ejemplos de Uso

### Flujo Completo: Crear y Aprobar un Gasto

#### 1. Login
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@example.com","password":"admin123"}'
```

#### 2. Crear Gasto
```bash
curl -X POST http://localhost:8080/api/v1/expense/ \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -d '{
    "amount": 150.50,
    "description": "Viaje de negocios",
    "date": "2024-11-15",
    "category": "Transporte",
    "employeeId": 3
  }'
```

#### 3. Aprobar Gasto (Admin)
```bash
curl -X PUT http://localhost:8080/api/v1/expense/10/approve \
  -H "Authorization: Bearer ADMIN_TOKEN_HERE"
```

---

## 🔒 Seguridad y Mejores Prácticas

### Recomendaciones

1. **Tokens JWT**: 
   - Cambia el secret en producción
   - Configura un tiempo de expiración apropiado
   - Usa HTTPS en producción

2. **CORS**: 
   - Restringe los orígenes permitidos en producción
   - No uses `origins = "*"` en producción

3. **Rate Limiting**: 
   - Implementa rate limiting para prevenir abuso
   - Usa Spring Security para proteger endpoints sensibles

4. **Validación**: 
   - Siempre valida inputs en el backend
   - Usa `@Valid` en los controllers
   - Maneja excepciones apropiadamente

5. **Logging**: 
   - No logguees información sensible
   - Usa niveles de log apropiados
   - Monitorea logs en producción

---

## 📞 Soporte

Para más información:
- Ver [INDEX.md](./INDEX.md) para más documentación
- Ver [SECURITY.md](./SECURITY.md) para guías de seguridad
- Abrir un issue en GitHub para reportar bugs

---

**Última actualización**: Diciembre 2024  
**Versión API**: v1  
**Versión Documento**: 1.0.0
