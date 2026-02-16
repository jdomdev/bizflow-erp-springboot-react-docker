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
    "name": "Ada",
    "surname": "Lovelace",
    "roleIds": [1],
    "roleDtos": [
      {
        "id": 1,
        "name": "ROLE_ADMIN"
      }
    ],
    "employeeId": 1,
    "employeeName": "Ada Lovelace",
    "employeePosition": "Senior Developer"
  }
]
```

## Obtener usuario

```http
GET /api/v1/user/{id}
Authorization: Bearer <token>
```

**Roles:** ADMIN, USER

## Crear usuario

```http
POST /api/v1/user/
Authorization: Bearer <token>
Content-Type: application/json

{
  "email": "nuevo.usuario@bizflowerp.com",
  "name": "Nuevo",
  "surname": "Usuario",
  "password": "password123",
  "roleIds": [2],
  "employeeId": null
}
```

**Roles:** ADMIN

### Response (201)

```json
{
  "id": 10,
  "email": "nuevo.usuario@bizflowerp.com",
  "name": "Nuevo",
  "surname": "Usuario",
  "roleIds": [2],
  "roleDtos": [
    {
      "id": 2,
      "name": "ROLE_USER"
    }
  ],
  "employeeId": null,
  "employeeName": null,
  "employeePosition": null
}
```

### Validaciones

| Campo | Regla |
|-------|-------|
| `email` | Requerido, único, formato válido |
| `name` | Requerido |
| `surname` | Requerido |
| `password` | Opcional (campo write-only) |
| `roleIds` | Lista de IDs de roles (ej: [1, 2, 3]) |

## Actualizar usuario

```http
PUT /api/v1/user/{id}
Authorization: Bearer <token>
Content-Type: application/json

{
  "email": "usuario.actualizado@bizflowerp.com",
  "name": "Usuario",
  "surname": "Actualizado",
  "roleIds": [3],
  "employeeId": 5
}
```

**Roles:** ADMIN

::: tip Contraseña opcional
La contraseña es opcional en la actualización. Si se envía, se actualizará. El campo `password` es write-only y no se devuelve en las respuestas.
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

## Obtener perfil del usuario autenticado

```http
GET /api/v1/user/profile
Authorization: Bearer <token>
```

**Roles:** Cualquier usuario autenticado

### Response (200)

```json
{
  "id": 5,
  "email": "usuario@bizflowerp.com",
  "name": "Juan",
  "surname": "Pérez",
  "roleIds": [2],
  "roleDtos": [
    {
      "id": 2,
      "name": "ROLE_USER"
    }
  ],
  "employeeId": 3,
  "employeeName": "Juan Pérez",
  "employeePosition": "Developer"
}
```

## Actualizar perfil del usuario autenticado

```http
PUT /api/v1/user/profile
Authorization: Bearer <token>
Content-Type: application/json

{
  "email": "nuevo.email@bizflowerp.com",
  "password": "nuevoPassword123"
}
```

**Roles:** Cualquier usuario autenticado

::: tip Campos opcionales
Solo se pueden actualizar `email` y `password` en el perfil propio. Los roles no se pueden cambiar mediante este endpoint.
:::

## Cerrar sesión

```http
POST /api/v1/user/logout
```

**Roles:** No requiere autenticación

### Response (200)

```json
{}
```

## Roles disponibles

| ID | Nombre | Descripción |
|----|--------|-------------|
| 1 | ROLE_ADMIN | Acceso completo |
| 2 | ROLE_USER | Solo recursos propios |
| 3 | ROLE_MANAGER | Supervisión con edición limitada |

## Estructura del modelo ExpenseUserDto

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `id` | Long | ID del usuario (auto-generado) |
| `email` | String | Email único del usuario (requerido) |
| `name` | String | Nombre del usuario (requerido) |
| `surname` | String | Apellido del usuario (requerido) |
| `password` | String | Contraseña (write-only, opcional en updates) |
| `roleIds` | List&lt;Long&gt; | Lista de IDs de roles asignados |
| `roleDtos` | List&lt;RoleDto&gt; | Lista de objetos Role completos (read-only) |
| `employeeId` | Long | ID del empleado asociado (opcional) |
| `employeeName` | String | Nombre completo del empleado (read-only) |
| `employeePosition` | String | Posición del empleado (read-only) |
