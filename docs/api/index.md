# API Reference

Bizflow ERP expone una API REST en `/api/v1/`.

## Base URL

```
http://localhost:8080/api/v1
```

## Autenticación

Todas las rutas (excepto login) requieren token JWT:

```http
Authorization: Bearer <token>
```

Ver [Autenticación](/api/auth) para más detalles.

## Endpoints Disponibles

### Autenticación

| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | `/auth/login` | Iniciar sesión |

### Gastos

| Método | Ruta | Descripción | Roles |
|--------|------|-------------|-------|
| GET | `/expenses` | Listar gastos | ADMIN, MANAGER |
| GET | `/expenses/search` | Buscar con paginación | ADMIN, MANAGER |
| GET | `/expenses/my` | Mis gastos | Todos |
| GET | `/expenses/{id}` | Obtener gasto | Dueño, ADMIN |
| POST | `/expenses` | Crear gasto | Todos |
| PUT | `/expenses/{id}` | Actualizar gasto | Dueño, ADMIN |
| DELETE | `/expenses/{id}` | Eliminar gasto | ADMIN |

### Empleados

| Método | Ruta | Descripción | Roles |
|--------|------|-------------|-------|
| GET | `/employee` | Listar empleados | ADMIN, MANAGER |
| GET | `/employee/{id}` | Obtener empleado | ADMIN, MANAGER |
| POST | `/employee` | Crear empleado | ADMIN |
| PUT | `/employee/{id}` | Actualizar empleado | ADMIN (o propio para MANAGER) |
| DELETE | `/employee/{id}` | Eliminar empleado | ADMIN |

### Nóminas

| Método | Ruta | Descripción | Roles |
|--------|------|-------------|-------|
| GET | `/payroll` | Listar nóminas | ADMIN, MANAGER |
| GET | `/payroll/search` | Buscar con paginación | ADMIN, MANAGER |
| GET | `/payroll/my` | Mis nóminas | Todos |
| GET | `/payroll/{id}` | Obtener nómina | ADMIN, Dueño |
| POST | `/payroll` | Crear nómina | ADMIN |
| PUT | `/payroll/{id}` | Actualizar nómina | ADMIN |
| DELETE | `/payroll/{id}` | Eliminar nómina | ADMIN |

### Cargos

| Método | Ruta | Descripción | Roles |
|--------|------|-------------|-------|
| GET | `/position` | Listar cargos | ADMIN, MANAGER |
| GET | `/position/{id}` | Obtener cargo | ADMIN, MANAGER |
| POST | `/position` | Crear cargo | ADMIN |
| PUT | `/position/{id}` | Actualizar cargo | ADMIN |
| DELETE | `/position/{id}` | Eliminar cargo | ADMIN |

### Usuarios

| Método | Ruta | Descripción | Roles |
|--------|------|-------------|-------|
| GET | `/user` | Listar usuarios | ADMIN |
| GET | `/user/{id}` | Obtener usuario | ADMIN |
| POST | `/user` | Crear usuario | ADMIN |
| PUT | `/user/{id}` | Actualizar usuario | ADMIN |
| DELETE | `/user/{id}` | Eliminar usuario | ADMIN |

## Códigos de Estado

| Código | Significado |
|--------|-------------|
| 200 | OK - Operación exitosa |
| 201 | Created - Recurso creado |
| 400 | Bad Request - Error de validación |
| 401 | Unauthorized - Token inválido o expirado |
| 403 | Forbidden - Sin permisos |
| 404 | Not Found - Recurso no encontrado |
| 500 | Server Error - Error interno |

## Paginación

Los endpoints `/search` soportan paginación:

```http
GET /api/v1/expenses/search?page=0&size=10&sortBy=expenseDate&sortDir=desc
```

### Parámetros

| Parámetro | Tipo | Default | Descripción |
|-----------|------|---------|-------------|
| `page` | number | 0 | Número de página (0-indexed) |
| `size` | number | 10 | Items por página |
| `sortBy` | string | id | Campo de ordenación |
| `sortDir` | string | asc | Dirección (asc/desc) |

### Response

```json
{
  "content": [...],
  "page": 0,
  "size": 10,
  "totalElements": 45,
  "totalPages": 5,
  "first": true,
  "last": false
}
```
