# API de Cargos

Endpoints para gestión de cargos (positions).

## Listar cargos

```http
GET /api/v1/position
Authorization: Bearer <token>
```

**Roles:** ADMIN, MANAGER

### Response (200)

```json
[
  {
    "id": 1,
    "name": "CEO",
    "description": "Director Ejecutivo",
    "baseSalary": 8000.00
  },
  {
    "id": 2,
    "name": "CTO",
    "description": "Director de Tecnología",
    "baseSalary": 7000.00
  }
]
```

## Obtener cargo

```http
GET /api/v1/position/{id}
Authorization: Bearer <token>
```

**Roles:** ADMIN, MANAGER

## Crear cargo

```http
POST /api/v1/position
Authorization: Bearer <token>
Content-Type: application/json

{
  "name": "Data Scientist",
  "description": "Analista de datos y machine learning",
  "baseSalary": 4500.00
}
```

**Roles:** ADMIN

### Response (201)

```json
{
  "id": 15,
  "name": "Data Scientist",
  "description": "Analista de datos y machine learning",
  "baseSalary": 4500.00
}
```

## Actualizar cargo

```http
PUT /api/v1/position/{id}
Authorization: Bearer <token>
Content-Type: application/json

{
  "name": "Senior Data Scientist",
  "description": "Analista de datos senior y machine learning",
  "baseSalary": 5500.00
}
```

**Roles:** ADMIN

## Eliminar cargo

```http
DELETE /api/v1/position/{id}
Authorization: Bearer <token>
```

**Roles:** ADMIN

::: warning Cargos en uso
No se puede eliminar un cargo que tenga empleados asignados.
:::
