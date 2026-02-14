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
    "firstName": "Ada",
    "lastName": "Lovelace",
    "email": "ada.lovelace@bizflowerp.com",
    "phone": "+34 600 000 001",
    "positionId": 1,
    "positionName": "CEO",
    "department": "Dirección",
    "userId": 1
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
  "firstName": "Grace",
  "lastName": "Hopper",
  "email": "grace.hopper@bizflowerp.com",
  "phone": "+34 600 000 010",
  "positionId": 2,
  "userId": null
}
```

**Roles:** ADMIN

## Actualizar empleado

```http
PUT /api/v1/employee/{id}
Authorization: Bearer <token>
Content-Type: application/json

{
  "firstName": "Grace",
  "lastName": "Hopper",
  "email": "grace.hopper@bizflowerp.com",
  "phone": "+34 600 000 010",
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
