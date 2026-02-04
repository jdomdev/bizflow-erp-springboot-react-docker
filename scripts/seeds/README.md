# Seeds via API System

Sistema profesional de seeding que respeta la lógica de negocio del Service layer
insertando datos a través de la API REST en lugar de SQL directo.

## Arquitectura

```
scripts/seeds/
├── README.md              # Esta documentación
├── seed_runner.py         # Script principal de seeding
├── requirements.txt       # Dependencias Python
├── Dockerfile             # Imagen del seeder
├── config/
│   └── environments.json  # Configuración por entorno
└── data/
    ├── test/              # Datos para TEST (mínimos)
    │   ├── employees.json
    │   ├── users.json.example  ⚠️ NO SE USA - solo referencia
    │   ├── payrolls.json
    │   └── expenses.json
    ├── dev/               # Datos para DEV (moderados)
    │   ├── employees.json
    │   ├── users.json.example  ⚠️ NO SE USA - solo referencia
    │   ├── payrolls.json
    │   └── expenses.json
    └── prod/              # Datos para PROD (completos)
        ├── employees.json
        ├── users.json.example  ⚠️ NO SE USA - solo referencia
        ├── payrolls.json
        └── expenses.json

scripts/secrets/           # ⚠️ GITIGNORED - contraseñas reales
└── users_with_passwords/
    ├── dev_users.json     # ✅ Usuarios DEV con contraseñas reales
    ├── test_users.json    # ✅ Usuarios TEST con contraseñas reales
    └── prod_users.json    # ✅ Usuarios PROD con contraseñas reales
```

## ⚠️ IMPORTANTE: Archivos de usuarios

Los archivos `users.json.example` en `data/*/` contienen passwords de placeholder
(`<SEED_PASSWORD_PLACEHOLDER>`) y **NO SE USAN** para seeding.

**Los usuarios se cargan desde `scripts/secrets/users_with_passwords/`** que:
- Está en `.gitignore` (nunca se sube al repositorio)
- Contiene las contraseñas reales para cada entorno
- Debe existir en tu máquina local para que el seeding de usuarios funcione

### Crear los archivos de usuarios con contraseñas

Si no tienes los archivos de secrets, copia los `.example` y reemplaza los placeholders:

```bash
mkdir -p scripts/secrets/users_with_passwords
cp scripts/seeds/data/dev/users.json.example scripts/secrets/users_with_passwords/dev_users.json
# Luego edita y reemplaza <SEED_PASSWORD_PLACEHOLDER> con contraseñas reales
```

## Ventajas sobre SQL directo

| Aspecto | SQL Directo | Seeds via API |
|---------|-------------|---------------|
| SSOT (Single Source of Truth) | ❌ Duplica lógica | ✅ Usa Service layer |
| Vinculación Employee↔User | ❌ Manual | ✅ Automática |
| Validaciones | ❌ Sin validar | ✅ DTO validation |
| BCrypt passwords | ❌ Pre-hash requerido | ✅ Hash automático |
| Auditoría | ❌ No aplicable | ✅ Logs del backend |

## Uso

### Ejecución manual
```bash
cd scripts/seeds
pip install -r requirements.txt
python seed_runner.py --env dev --api-url http://localhost:8080
```

### Con Docker Compose
```bash
docker-compose up seeder
```

## Variables de entorno

| Variable | Descripción | Default |
|----------|-------------|---------|
| `SEED_ENV` | Entorno a seedear (test/dev/prod) | dev |
| `API_URL` | URL base del backend | http://backend:8080 |
| `ADMIN_EMAIL` | Email del primer admin | admin@bizflowerp.com |
| `ADMIN_PASSWORD` | Password del primer admin | <PASSWORD> |
| `WAIT_SECONDS` | Segundos a esperar por backend | 60 |

## Flujo de seeding

1. **Wait for backend**: Espera a que el backend esté healthy
2. **Login as admin**: Obtiene JWT token del admin existente
3. **Seed employees**: POST a `/api/v1/employee/`
4. **Seed users**: POST a `/api/v1/auth/signup` (auto-vincula con employees por email)
5. **Seed payrolls**: POST a `/api/v1/payroll/`
6. **Seed expenses**: POST a `/api/v1/expense/`

## Notas importantes

- El admin inicial debe existir en la base de datos (creado por `common/05_expense_admin_bootstrap.sql`)
- Los emails de users deben coincidir con los de employees para auto-vinculación
- El seeder es idempotente: puede ejecutarse múltiples veces sin duplicar datos
