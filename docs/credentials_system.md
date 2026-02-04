# User Credentials System

Sistema de generación y gestión de credenciales de usuarios para Bizflow ERP.

## 📋 Resumen

Las credenciales se generan con un **script de generación determinística** ubicado en 
`scripts/secrets/` (gitignored por seguridad). Este script:

- Produce passwords consistentes y reproducibles
- Genera archivos JSON con credenciales para cada entorno
- Actualiza los archivos `.env.*` con el ADMIN_PASSWORD

## 📁 Estructura de Archivos

```
scripts/
├── secrets/                          # ⚠️ GITIGNORED - Contiene secrets
│   ├── generate_user_credentials.py  # 🔐 Script de generación (CONFIDENCIAL)
│   ├── README.md                     # Documentación interna
│   └── users_with_passwords/
│       ├── dev_users.json            # Passwords DEV en texto plano
│       ├── test_users.json           # Passwords TEST en texto plano
│       └── prod_users.json           # Passwords PROD en texto plano
├── utils/
│   └── generate_password_hashes.py   # Genera hashes BCrypt para SQL
└── seeds/
    └── data/
        └── */employees.json          # Datos de empleados (sin passwords)

sql/common/
├── 05_admin_bootstrap_dev_test.sql   # ✅ GENERADO - Hashes BCrypt (seguros)
└── 05_admin_bootstrap_prod.sql       # ✅ GENERADO - Hashes BCrypt (seguros)
```

## 🔄 Flujo de Datos

```
┌─────────────────────────────────────────────────────────────────────────────┐
│  1. SCRIPT DE GENERACIÓN (gitignored)                                       │
│     scripts/secrets/generate_user_credentials.py                            │
│     - Contiene fórmula determinística (CONFIDENCIAL)                        │
│     - Contiene salts por entorno (CONFIDENCIAL)                             │
└──────────────────────────────┬──────────────────────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│  2. ARCHIVOS DE SECRETS (gitignored)                                        │
│     scripts/secrets/users_with_passwords/*.json                             │
│     - Passwords en texto plano                                              │
│     - NUNCA se suben al repositorio                                         │
└──────────────────────────────┬──────────────────────────────────────────────┘
                               │
              ┌────────────────┴────────────────┐
              ▼                                 ▼
┌─────────────────────────────────┐  ┌─────────────────────────────────┐
│  3A. HASHES SQL (en git)        │  │  3B. API SEEDER (runtime)       │
│  generate_password_hashes.py    │  │  Envía passwords al API         │
│  → 05_admin_bootstrap_*.sql     │  │  API hashea con BCrypt          │
│  - Solo hashes BCrypt ($2a$)    │  │  → Usuarios no-admin en BD      │
│  - Seguros de commitear         │  │                                 │
└─────────────────────────────────┘  └─────────────────────────────────┘
              │                                 │
              ▼                                 ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│  4. BASE DE DATOS                                                           │
│     - Usuarios admin via SQL bootstrap (Ada, Alan)                          │
│     - Resto de usuarios via API seeder                                      │
│     - TODOS almacenados como hashes BCrypt                                  │
└─────────────────────────────────────────────────────────────────────────────┘
```

## 🛠️ Comandos de Makefile

```bash
# Genera passwords para todos los entornos (requiere script en secrets/)
make generate-credentials

# Genera hashes BCrypt para SQL bootstrap
make generate-sql-hashes

# Hace ambos en secuencia
make regenerate-all-credentials

# Verifica que los hashes SQL coinciden con secrets
make verify-sql-hashes
```

## 🚀 Setup Inicial (nuevo desarrollador)

Si eres nuevo en el proyecto, necesitas obtener el directorio `scripts/secrets/`
de forma segura (no está en git):

1. **Opción A**: Solicita el archivo al equipo de desarrollo
2. **Opción B**: Restaura desde backup seguro
3. **Opción C**: Genera nuevos secrets (coordinando con el equipo)

Una vez tengas `scripts/secrets/`:

```bash
# 1. Generar los archivos de secrets (gitignored)
make generate-credentials

# 2. Levantar el entorno
make up-dev
```

## ⚠️ Seguridad

| Archivo | Contiene | ¿En Git? |
|---------|----------|----------|
| `scripts/secrets/*` | Fórmula, salts, passwords | ❌ **NO** (gitignored) |
| `generate_password_hashes.py` | Solo lee y hashea | ✅ Sí |
| `05_admin_bootstrap_*.sql` | Hashes BCrypt | ✅ Sí (seguros) |
| `.env.*` | ADMIN_PASSWORD | ❌ **NO** (gitignored) |

### ¿Por qué los hashes BCrypt son seguros?

Los hashes BCrypt son seguros de commitear porque:
- Son **irreversibles** (one-way hash)
- Incluyen **salt único** por hash
- Son el **estándar de la industria**
- Tienen **cost factor** que los hace lentos de forzar

### ¿Cómo fluyen los passwords de usuarios no-admin?

```
[generate_user_credentials.py]  ──▶  [JSON en scripts/secrets/]
                                              │
                                              ▼ (leído por docker)
                                     [API Seeder container]
                                              │
                                              ▼ (HTTP POST /register)
                                     [Spring Boot API]
                                              │
                                              ▼ (BCrypt.encode())
                                     [BD: solo hash almacenado]
```

Los passwords en texto plano **solo existen**:
1. En los archivos JSON de `scripts/secrets/` (gitignored)
2. En memoria durante el proceso de seeding
3. **NUNCA** en la base de datos ni en git

## 📝 Notas

- Los passwords de PROD son diferentes a DEV/TEST (diferentes salts)
- Al recrear la BD, los admins (Ada, Alan) se crean vía SQL bootstrap
- El resto de usuarios se crean vía API seeder
- Ambos mecanismos usan los mismos passwords de secrets
- El script de generación está en `scripts/secrets/` (gitignored) por seguridad
