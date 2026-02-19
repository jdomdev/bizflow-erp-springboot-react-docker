# Configuración de Credenciales

Esta guía explica cómo configurar los archivos de credenciales necesarios para ejecutar Bizflow ERP.

## ¿Por qué necesito configurar credenciales?

Por motivos de seguridad, las contraseñas de los usuarios **no se incluyen en el repositorio**. La carpeta `scripts/secrets/` está ignorada en `.gitignore`.

Antes de ejecutar la aplicación por primera vez, debes crear los archivos de credenciales siguiendo estos pasos.

## Estructura requerida

```
scripts/secrets/
└── users_with_passwords/
    ├── dev_users.json     # Credenciales para entorno desarrollo
    ├── test_users.json    # Credenciales para entorno testing (iguales a dev)
    └── prod_users.json    # Credenciales para producción (DEBEN SER DIFERENTES)
```

## Pasos de configuración

> 💡 **Windows**: Todos los comandos de esta guía funcionan en WSL2 (Ubuntu). Si usas Windows, ejecuta los comandos desde una terminal WSL2.

### 1. Crear el directorio de secrets

```bash
mkdir -p scripts/secrets/users_with_passwords
```

### 2. Copiar las plantillas

```bash
# Copiar plantilla para desarrollo
cp scripts/seeds/data/dev/users.json.example scripts/secrets/users_with_passwords/dev_users.json

# Copiar plantilla para testing (mismas credenciales que dev)
cp scripts/seeds/data/test/users.json.example scripts/secrets/users_with_passwords/test_users.json

# Copiar plantilla para producción
cp scripts/seeds/data/prod/users.json.example scripts/secrets/users_with_passwords/prod_users.json
```

### 3. Generar contraseñas

Edita cada archivo y reemplaza `<SEED_PASSWORD_PLACEHOLDER>` con contraseñas reales.

#### Formato recomendado de contraseñas

Las contraseñas deben cumplir estos requisitos:
- Mínimo 8 caracteres
- Al menos una letra mayúscula
- Al menos una letra minúscula
- Al menos un número
- Al menos un carácter especial (@, #, $, %, &, *, !)

#### Ejemplo de generación automática

```bash
# Generar contraseña aleatoria segura
openssl rand -base64 12 | tr -d '\n' | head -c 12
# Resultado: (cadena aleatoria de 12 caracteres)
```

#### Script de ayuda (opcional)

```bash
# Reemplazar todos los placeholders con una contraseña temporal
# NOTA: Solo para desarrollo, genera la misma contraseña para todos
# Funciona en Linux y macOS (el .bak crea backup que puedes borrar)
sed -i.bak 's/<SEED_PASSWORD_PLACEHOLDER>/TU_PASSWORD_TEMPORAL/g' scripts/secrets/users_with_passwords/dev_users.json
sed -i.bak 's/<SEED_PASSWORD_PLACEHOLDER>/TU_PASSWORD_TEMPORAL/g' scripts/secrets/users_with_passwords/test_users.json
rm -f scripts/secrets/users_with_passwords/*.bak  # Opcional: borrar backups
```

## ⚠️ Importante: Producción

Para el entorno de **producción** (`prod_users.json`):

1. **NUNCA** uses las mismas contraseñas que en desarrollo
2. Genera contraseñas únicas y seguras para cada usuario
3. Almacena las contraseñas en un gestor de contraseñas seguro
4. Considera usar un sistema de gestión de secretos (HashiCorp Vault, AWS Secrets Manager, etc.)

## Estructura del archivo JSON

Cada archivo debe ser un array de objetos con la siguiente estructura:

```json
[
  {
    "name": "Ada",
    "surname": "Lovelace",
    "email": "ada.lovelace@bizflowerp.com",
    "password": "<TU_PASSWORD_SEGURA>"
  },
  {
    "name": "Alan",
    "surname": "Turing",
    "email": "alan.turing@bizflowerp.com",
    "password": "<OTRA_PASSWORD_SEGURA>"
  }
]
```

## Verificación

Una vez configurados, puedes verificar que los archivos existen:

```bash
ls -la scripts/secrets/users_with_passwords/
# Deberías ver: dev_users.json, test_users.json, prod_users.json
```

## Usuarios predeterminados por rol

El sistema incluye usuarios con diferentes roles para testing:

| Rol | Ejemplo de email | Descripción |
|-----|------------------|-------------|
| ADMIN | ada.lovelace@bizflowerp.com | Acceso completo al sistema |
| MANAGER | nikola.tesla@bizflowerp.com | Gestión de empleados y recursos |
| USER | ken.thompson@bizflowerp.com | Usuario estándar |

## Siguiente paso

Una vez configuradas las credenciales, continúa con la [Guía de Instalación](../README.md#-quick-start).

## Documentación relacionada

- [Sistema de Seeds](../../scripts/seeds/README.md) - Cómo funciona el sistema de seeding
- [Guía de Desarrollo](../guides/DEVELOPMENT_GUIDELINES.md) - Guía completa para desarrolladores
