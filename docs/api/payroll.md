# API de Nóminas

Endpoints para gestión de nóminas.

## Listar nóminas

```http
GET /api/v1/payroll
Authorization: Bearer <token>
```

**Roles:** ADMIN, MANAGER

## Búsqueda paginada

```http
GET /api/v1/payroll/search?page=0&size=10&sortBy=payrollDate&sortDirection=desc
Authorization: Bearer <token>
```

**Roles:** ADMIN, MANAGER, USER

### Parámetros

| Parámetro | Tipo | Default | Descripción |
|-----------|------|---------|-------------|
| `page` | number | 0 | Número de página |
| `size` | number | 10 | Items por página |
| `sortBy` | string | payrollDate | Campo de ordenación |
| `sortDirection` | string | desc | Dirección (asc/desc) |
| `search` | string | - | Búsqueda general |
| `employeeId` | number | - | Filtro por empleado |
| `userId` | number | - | Filtro por usuario (expenseUserId) |
| `minAmount` | number | - | Importe mínimo |
| `maxAmount` | number | - | Importe máximo |
| `startDate` | datetime | - | Fecha inicial (ISO 8601) |
| `endDate` | datetime | - | Fecha final (ISO 8601) |

### Response (200)

```json
{
  "content": [
    {
      "id": 1,
      "amount": 4700.00,
      "payrollDate": "2026-01-31T00:00:00",
      "employeeId": 1,
      "expenseUserId": null
    }
  ],
  "page": 0,
  "size": 10,
  "totalElements": 12,
  "totalPages": 2
}
```

## Mis nóminas

```http
GET /api/v1/payroll/my
Authorization: Bearer <token>
```

**Roles:** Todos (devuelve nóminas del empleado vinculado al usuario)

## Obtener nómina por ID

```http
GET /api/v1/payroll/{payrollId}
Authorization: Bearer <token>
```

**Roles:** ADMIN, MANAGER, USER

## Listar nóminas por empleado

```http
GET /api/v1/payroll/employee/{employeeId}
Authorization: Bearer <token>
```

**Roles:** ADMIN, MANAGER, USER

## Listar nóminas por usuario

```http
GET /api/v1/payroll/user/{expenseUserId}
Authorization: Bearer <token>
```

**Roles:** ADMIN, MANAGER, USER

## Crear nómina

```http
POST /api/v1/payroll/
Authorization: Bearer <token>
Content-Type: application/json

{
  "employeeId": 1,
  "payrollDate": "2026-02-28T00:00:00",
  "amount": 4700.00
}
```

**Roles:** ADMIN

::: tip Campos opcionales
También puedes especificar `expenseUserId` en lugar de `employeeId` para nóminas de freelancers o usuarios sin empleado asociado.
:::

## Actualizar nómina

```http
PUT /api/v1/payroll/{id}
Authorization: Bearer <token>
Content-Type: application/json

{
  "amount": 5000.00,
  "payrollDate": "2026-02-28T00:00:00"
}
```

**Roles:** ADMIN

::: info Notificación automática
Al crear, actualizar o eliminar una nómina, el empleado o usuario asociado recibe una notificación automática.
:::

## Eliminar nómina

```http
DELETE /api/v1/payroll/{id}
Authorization: Bearer <token>
```

**Roles:** ADMIN
