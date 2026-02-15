# API de Autenticación

Endpoints para autenticación y gestión de sesiones.

## Login

Autenticar un usuario y obtener token JWT.

### Request

```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "email": "ada.lovelace@bizflowerp.com",
  "password": "password123"
}
```

### Response - Éxito (200)

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": 1,
    "email": "ada.lovelace@bizflowerp.com",
    "roleId": 1,
    "roleName": "Administrador",
    "employeeId": 1
  }
}
```

### Response - Credenciales inválidas (401)

```json
{
  "error": "Credenciales inválidas"
}
```

## Uso del Token

Incluir el token en el header `Authorization` de todas las peticiones:

```http
GET /api/v1/expenses
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

## Estructura del Token JWT

El token contiene:

```json
{
  "sub": "ada.lovelace@bizflowerp.com",
  "userId": 1,
  "roleId": 1,
  "roleName": "Administrador",
  "iat": 1707900000,
  "exp": 1707986400
}
```

| Campo | Descripción |
|-------|-------------|
| `sub` | Email del usuario |
| `userId` | ID del usuario |
| `roleId` | ID del rol (1=ADMIN, 2=USER, 3=MANAGER) |
| `roleName` | Nombre del rol |
| `iat` | Timestamp de emisión |
| `exp` | Timestamp de expiración |

## Expiración

El token expira después de **24 horas**. Cuando expire, el usuario debe volver a autenticarse.

### Response - Token expirado (401)

```json
{
  "error": "Token expirado",
  "code": "TOKEN_EXPIRED"
}
```

## Logout

El logout se maneja en el cliente eliminando el token almacenado. No hay endpoint de logout en el servidor.

```javascript
// Frontend
localStorage.removeItem('token')
localStorage.removeItem('user')
```
