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
GET /api/v1/payroll/search?page=0&size=10&sortBy=payrollDate&sortDir=desc
Authorization: Bearer <token>
```

### Parámetros

| Parámetro | Tipo | Default | Descripción |
|-----------|------|---------|-------------|
| `page` | number | 0 | Número de página |
| `size` | number | 10 | Items por página |
| `sortBy` | string | payrollDate | Campo de ordenación |
| `sortDir` | string | desc | Dirección |
| `employeeId` | number | - | Filtro por empleado |
| `startDate` | date | - | Fecha inicial |
| `endDate` | date | - | Fecha final |

### Response (200)

```json
{
  "content": [
    {
      "id": 1,
      "employeeId": 1,
      "employeeName": "Ada Lovelace",
      "payrollDate": "2026-01-31",
      "baseSalary": 5000.00,
      "deductions": 500.00,
      "bonuses": 200.00,
      "netSalary": 4700.00
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

## Crear nómina

```http
POST /api/v1/payroll
Authorization: Bearer <token>
Content-Type: application/json

{
  "employeeId": 1,
  "payrollDate": "2026-02-28",
  "baseSalary": 5000.00,
  "deductions": 500.00,
  "bonuses": 300.00
}
```

**Roles:** ADMIN

::: info Notificación automática
Al crear una nómina, el empleado recibe una notificación WebSocket automática.
:::

## Actualizar nómina

```http
PUT /api/v1/payroll/{id}
Authorization: Bearer <token>
```

**Roles:** ADMIN

## Eliminar nómina

```http
DELETE /api/v1/payroll/{id}
Authorization: Bearer <token>
```

**Roles:** ADMIN
