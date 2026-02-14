# API de Usuarios

Endpoints para gestión de usuarios. Solo accesible por ADMIN.

## Listar usuarios

```http
GET /api/v1/user
Authorization: Bearer <token>
```

**Roles:** ADMIN

### Response (200)

```json
[
  {
    "id": 1,
    "email": "ada.lovelace@bizflowerp.com",
    "roleId": 1,
    "roleName": "Administrador",
    "employeeId": 1,
    "employeeName": "Ada Lovelace"
  }
]
```

## Obtener usuario

```http
GET /api/v1/user/{id}
Authorization: Bearer <token>
```

**Roles:** ADMIN

## Crear usuario

```http
POST /api/v1/user
Authorization: Bearer <token>
Content-Type: application/json

{
  "email": "nuevo.usuario@bizflowerp.com",
  "password": "password123",
  "roleId": 2,
  "employeeId": null
}
```

**Roles:** ADMIN

### Response (201)

```json
{
  "id": 10,
  "email": "nuevo.usuario@bizflowerp.com",
  "roleId": 2,
  "roleName": "Usuario",
  "employeeId": null,
  "employeeName": null
}
```

### Validaciones

| Campo | Regla |
|-------|-------|
| `email` | Único, formato válido |
| `password` | Mínimo 8 caracteres |
| `roleId` | 1 (ADMIN), 2 (USER), o 3 (MANAGER) |

## Actualizar usuario

```http
PUT /api/v1/user/{id}
Authorization: Bearer <token>
Content-Type: application/json

{
  "email": "usuario.actualizado@bizflowerp.com",
  "roleId": 3,
  "employeeId": 5
}
```

**Roles:** ADMIN

::: tip Contraseña opcional
La contraseña es opcional en la actualización. Si no se envía, se mantiene la actual.
:::

## Eliminar usuario

```http
DELETE /api/v1/user/{id}
Authorization: Bearer <token>
```

**Roles:** ADMIN

::: warning Restricción
No se puede eliminar el usuario actualmente autenticado.
:::

## Roles disponibles

| ID | Nombre | Descripción |
|----|--------|-------------|
| 1 | Administrador | Acceso completo |
| 2 | Usuario | Solo recursos propios |
| 3 | Manager | Supervisión con edición limitada |
