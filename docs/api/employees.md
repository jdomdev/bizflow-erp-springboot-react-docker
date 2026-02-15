# API de Empleados

Endpoints para gestión de empleados.

## Listar empleados

```http
GET /api/v1/employee
Authorization: Bearer <token>
```

**Roles:** ADMIN, MANAGER

### Response (200)

```json
[
  {
    "id": 1,
    "name": "Ada",
    "surname": "Lovelace",
    "birthDate": "1815-12-10T00:00:00",
    "email": "ada.lovelace@bizflowerp.com",
    "positionId": 1,
    "expenseUserId": 1,
    "expenseUserName": "Ada Lovelace",
    "expenseUserEmail": "ada.lovelace@bizflowerp.com"
  }
]
```

## Obtener empleado

```http
GET /api/v1/employee/{id}
Authorization: Bearer <token>
```

## Crear empleado

```http
POST /api/v1/employee
Authorization: Bearer <token>
Content-Type: application/json

{
  "name": "Grace",
  "surname": "Hopper",
  "birthDate": "1906-12-09T00:00:00",
  "email": "grace.hopper@bizflowerp.com",
  "positionId": 2
}
```

**Roles:** ADMIN

## Actualizar empleado

```http
PUT /api/v1/employee/{id}
Authorization: Bearer <token>
Content-Type: application/json

{
  "name": "Grace",
  "surname": "Hopper",
  "birthDate": "1906-12-09T00:00:00",
  "email": "grace.hopper@bizflowerp.com",
  "positionId": 3
}
```

**Roles:** ADMIN, o MANAGER editando su propio empleado vinculado

## Eliminar empleado

```http
DELETE /api/v1/employee/{id}
Authorization: Bearer <token>
```

**Roles:** ADMIN
