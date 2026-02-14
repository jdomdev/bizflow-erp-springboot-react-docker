# API de Gastos

Endpoints para gestión de gastos.

## Listar todos los gastos

Obtiene todos los gastos. Solo ADMIN y MANAGER.

```http
GET /api/v1/expenses
Authorization: Bearer <token>
```

### Response (200)

```json
[
  {
    "id": 1,
    "description": "Material de oficina",
    "amount": 150.00,
    "category": "SUPPLIES",
    "expenseDate": "2026-02-10",
    "creatorId": 1,
    "creatorName": "Ada Lovelace"
  }
]
```

## Búsqueda paginada

Busca gastos con filtros y paginación del lado del servidor.

```http
GET /api/v1/expenses/search?page=0&size=10&sortBy=expenseDate&sortDir=desc
Authorization: Bearer <token>
```

### Parámetros de consulta

| Parámetro | Tipo | Default | Descripción |
|-----------|------|---------|-------------|
| `page` | number | 0 | Número de página |
| `size` | number | 10 | Items por página |
| `sortBy` | string | expenseDate | Campo de ordenación |
| `sortDir` | string | desc | Dirección (asc/desc) |
| `category` | string | - | Filtro por categoría |
| `startDate` | date | - | Fecha inicial |
| `endDate` | date | - | Fecha final |

### Response (200)

```json
{
  "content": [
    {
      "id": 1,
      "description": "Material de oficina",
      "amount": 150.00,
      "category": "SUPPLIES",
      "expenseDate": "2026-02-10",
      "creatorId": 1,
      "creatorName": "Ada Lovelace"
    }
  ],
  "page": 0,
  "size": 10,
  "totalElements": 45,
  "totalPages": 5,
  "first": true,
  "last": false
}
```

## Mis gastos

Obtiene los gastos del usuario autenticado.

```http
GET /api/v1/expenses/my
Authorization: Bearer <token>
```

## Obtener gasto por ID

```http
GET /api/v1/expenses/{id}
Authorization: Bearer <token>
```

### Response (200)

```json
{
  "id": 1,
  "description": "Material de oficina",
  "amount": 150.00,
  "category": "SUPPLIES",
  "expenseDate": "2026-02-10",
  "creatorId": 1,
  "creatorName": "Ada Lovelace"
}
```

### Response (404)

```json
{
  "error": "Gasto no encontrado"
}
```

## Crear gasto

```http
POST /api/v1/expenses
Authorization: Bearer <token>
Content-Type: application/json

{
  "description": "Almuerzo con cliente",
  "amount": 85.50,
  "category": "MEALS",
  "expenseDate": "2026-02-14"
}
```

### Response (201)

```json
{
  "id": 46,
  "description": "Almuerzo con cliente",
  "amount": 85.50,
  "category": "MEALS",
  "expenseDate": "2026-02-14",
  "creatorId": 1,
  "creatorName": "Ada Lovelace"
}
```

## Actualizar gasto

Solo el creador o ADMIN pueden actualizar.

```http
PUT /api/v1/expenses/{id}
Authorization: Bearer <token>
Content-Type: application/json

{
  "description": "Almuerzo con cliente - actualizado",
  "amount": 90.00,
  "category": "MEALS",
  "expenseDate": "2026-02-14"
}
```

### Response (200)

```json
{
  "id": 46,
  "description": "Almuerzo con cliente - actualizado",
  "amount": 90.00,
  "category": "MEALS",
  "expenseDate": "2026-02-14",
  "creatorId": 1,
  "creatorName": "Ada Lovelace"
}
```

### Response (403)

```json
{
  "error": "No tienes permisos para editar este gasto"
}
```

## Eliminar gasto

Solo ADMIN puede eliminar.

```http
DELETE /api/v1/expenses/{id}
Authorization: Bearer <token>
```

### Response (204)

No content.

### Response (403)

```json
{
  "error": "Solo los administradores pueden eliminar gastos"
}
```

## Categorías disponibles

| Valor | Descripción |
|-------|-------------|
| `TRAVEL` | Viajes |
| `MEALS` | Comidas |
| `SUPPLIES` | Suministros |
| `EQUIPMENT` | Equipamiento |
| `SOFTWARE` | Software |
| `SERVICES` | Servicios |
| `OTHER` | Otros |
