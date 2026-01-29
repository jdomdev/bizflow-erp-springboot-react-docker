# Pull Request: feature/seed-data-refactor

**Fecha**: 29 de Enero de 2026, 21:49  
**Rama origen**: `feature/seed-data-refactor`  
**Rama destino**: `dev`  
**Autor**: bytetech  

---

## Resumen Ejecutivo

Esta Pull Request implementa un sistema completo de gestión de credenciales determinístico para todos los entornos (DEV, TEST, PROD), resuelve problemas de compatibilidad de hashes BCrypt entre Python y Spring Boot, establece la infraestructura Docker necesaria para el acceso seguro a secrets, e implementa la vinculación bidireccional entre entidades User y Employee en el backend.

---

## Índice

1. [Sistema de Generación de Credenciales](#1-sistema-de-generación-de-credenciales)
2. [Compatibilidad BCrypt Python-Spring Boot](#2-compatibilidad-bcrypt-python-spring-boot)
3. [Infraestructura Docker para Secrets](#3-infraestructura-docker-para-secrets)
4. [Vinculación Bidireccional User-Employee](#4-vinculación-bidireccional-user-employee)
5. [Refactorización de Seeds](#5-refactorización-de-seeds)
6. [Targets de Makefile](#6-targets-de-makefile)
7. [Documentación](#7-documentación)
8. [Archivos Modificados](#8-archivos-modificados)
9. [Commits Incluidos](#9-commits-incluidos)
10. [Testing y Verificación](#10-testing-y-verificación)
11. [Próximos Pasos](#11-próximos-pasos)

---

## 1. Sistema de Generación de Credenciales

### 1.1 Problema Original

El sistema anterior requería mantener manualmente las contraseñas de usuarios en múltiples archivos:
- Archivos JSON de seeds por entorno
- Archivos `.env.*` con `ADMIN_PASSWORD`
- Archivos SQL de bootstrap con hashes BCrypt
- Documentación de credenciales

Esto generaba inconsistencias frecuentes y errores de autenticación difíciles de diagnosticar.

### 1.2 Solución Implementada

Se creó un script Python (`scripts/secrets/generate_user_credentials.py`) que actúa como **única fuente de verdad** para la generación de credenciales:

- **Generación determinística**: Las contraseñas se generan mediante una fórmula reproducible basada en el email del usuario, su rol y un salt específico por entorno.
- **Múltiples outputs**: El script genera automáticamente:
  - Archivos JSON con credenciales para cada entorno
  - Actualización de `ADMIN_PASSWORD` en archivos `.env.*`
- **Seguridad**: El script y sus outputs están en `scripts/secrets/`, que está incluido en `.gitignore`.

### 1.3 Arquitectura de Salts

| Entorno | Salt | Archivo SQL Bootstrap |
|---------|------|----------------------|
| DEV | Salt compartido | `05_admin_bootstrap_dev_test.sql` |
| TEST | Salt compartido (mismo que DEV) | `05_admin_bootstrap_dev_test.sql` |
| PROD | Salt diferente | `05_admin_bootstrap_prod.sql` |

**Nota importante**: DEV y TEST comparten el mismo salt porque ambos utilizan el mismo archivo SQL de bootstrap. PROD tiene su propio salt para mayor seguridad.

### 1.4 Uso del Sistema

```bash
# Generar credenciales para todos los entornos
python3 scripts/secrets/generate_user_credentials.py --generate

# Generar solo para un entorno específico
python3 scripts/secrets/generate_user_credentials.py --generate --env dev

# Ver la fórmula de generación (sin exponer valores)
python3 scripts/secrets/generate_user_credentials.py --show-formula

# Verificar que las credenciales existentes coinciden con la fórmula
python3 scripts/secrets/generate_user_credentials.py --verify
```

---

## 2. Compatibilidad BCrypt Python-Spring Boot

### 2.1 Problema Detectado

La librería `bcrypt` de Python genera hashes con prefijo `$2b$`, mientras que Spring Boot's `BCryptPasswordEncoder` espera el prefijo `$2a$`. Aunque ambos son funcionalmente idénticos (mismo algoritmo, mismo proceso de verificación), Spring Boot rechazaba los hashes con prefijo incorrecto.

### 2.2 Solución Implementada

Se creó el script `scripts/utils/generate_password_hashes.py` que:

1. Genera hashes BCrypt usando la librería Python
2. Convierte automáticamente el prefijo `$2b$` a `$2a$`
3. Genera los archivos SQL de bootstrap con hashes compatibles

```python
hash_bytes = bcrypt.hashpw(password.encode(), bcrypt.gensalt(10))
hash_str = hash_bytes.decode()

# Conversión de prefijo para compatibilidad Spring Boot
if hash_str.startswith("$2b$"):
    hash_str = "$2a$" + hash_str[4:]
```

### 2.3 Justificación Técnica

Los prefijos `$2a$` y `$2b$` representan versiones del algoritmo BCrypt:
- `$2a$`: Versión original, ampliamente soportada
- `$2b$`: Versión de OpenBSD que corrige un bug de manejo de caracteres no-ASCII

Para contraseñas ASCII estándar (como las generadas por nuestro sistema), ambas versiones producen resultados idénticos. La conversión de prefijo es segura y no afecta la verificación de contraseñas.

---

## 3. Infraestructura Docker para Secrets

### 3.1 Problema Original

El seeder de API (`api-seeder-*`) no podía acceder a los archivos JSON de credenciales generados, ya que estos se encuentran en `scripts/secrets/` que no estaba montado en los contenedores.

### 3.2 Solución Implementada

Se añadió un volumen en `docker-compose.yml` para el servicio `x-api-seeder-common`:

```yaml
x-api-seeder-common: &api-seeder-common
  volumes:
    - ./scripts/seeds:/app:ro
    - ./scripts/secrets:/secrets:ro  # ← Nuevo volumen
```

Esto permite que el seeder acceda a:
- `/secrets/users_with_passwords/dev_users.json`
- `/secrets/users_with_passwords/test_users.json`
- `/secrets/users_with_passwords/prod_users.json`

### 3.3 Consideraciones de Seguridad

- El volumen está montado como **read-only** (`:ro`)
- Solo los contenedores de seeder tienen acceso
- Los archivos de secrets están en `.gitignore`

---

## 4. Vinculación Bidireccional User-Employee

### 4.1 Problema Original

La entidad `User` tenía una referencia a `Employee` (via `employee_id`), pero cuando se creaba o actualizaba un `Employee`, no se establecía la vinculación inversa. Esto causaba que funcionalidades como "Mis Nóminas" no funcionaran para usuarios que tenían empleados asociados.

**Ejemplo**: Ada Lovelace tenía un usuario y un empleado con el mismo email, pero el campo `employee_id` del usuario era `null`.

### 4.2 Solución Implementada

Se modificó `EmployeeServiceImpl.java` para establecer la vinculación bidireccional automáticamente:

```java
@Autowired
@Lazy
private IUserDao userDao;

private void linkEmployeeToUser(Employee employee) {
    if (employee.getEmail() != null) {
        userDao.findByEmail(employee.getEmail()).ifPresent(user -> {
            if (user.getEmployee() == null || 
                !user.getEmployee().getId().equals(employee.getId())) {
                user.setEmployee(employee);
                userDao.save(user);
                log.info("Linked employee {} to user {}", 
                    employee.getId(), user.getId());
            }
        });
    }
}
```

### 4.3 Casos de Uso Cubiertos

1. **Creación de Employee**: Al guardar un nuevo empleado, se busca un usuario con el mismo email y se vincula automáticamente.
2. **Actualización de Employee**: Al actualizar un empleado, se re-evalúa la vinculación (útil si cambió el email).
3. **Cambio de email**: Si se cambia el email de un empleado, se desvincula del usuario anterior y se vincula al nuevo (si existe).

### 4.4 Uso de @Lazy

Se utilizó `@Lazy` en la inyección de `IUserDao` para evitar dependencias circulares:
- `UserService` depende de `EmployeeService`
- `EmployeeService` ahora depende de `UserDao`

Sin `@Lazy`, Spring detectaría una dependencia circular durante la inicialización.

---

## 5. Refactorización de Seeds

### 5.1 Cambios Estructurales

Los archivos de usuarios se movieron de archivos JSON trackeados a templates:

**Antes**:
```
scripts/seeds/data/
├── dev/users.json      (con passwords - trackeado en git)
├── test/users.json     (con passwords - trackeado en git)
└── prod/users.json     (con passwords - trackeado en git)
```

**Después**:
```
scripts/seeds/data/
├── dev/users.json.example      (sin passwords - trackeado en git)
├── test/users.json.example     (sin passwords - trackeado en git)
└── prod/users.json.example     (sin passwords - trackeado en git)

scripts/secrets/users_with_passwords/
├── dev_users.json      (con passwords - NO trackeado)
├── test_users.json     (con passwords - NO trackeado)
└── prod_users.json     (con passwords - NO trackeado)
```

### 5.2 Actualización del Seeder

El `seed_runner.py` fue actualizado para buscar credenciales en el siguiente orden:
1. `/secrets/users_with_passwords/{env}_users.json` (volumen Docker)
2. `./data/{env}/users.json` (fallback local)

---

## 6. Targets de Makefile

### 6.1 Nuevos Targets Añadidos

```makefile
# Genera credenciales determinísticas para todos los entornos
generate-credentials:
	python3 scripts/secrets/generate_user_credentials.py --generate

# Muestra la fórmula de generación de contraseñas
show-password-formula:
	python3 scripts/secrets/generate_user_credentials.py --show-formula

# Regenera credenciales + hashes SQL (pipeline completo)
regenerate-all-credentials:
	python3 scripts/secrets/generate_user_credentials.py --generate
	python3 scripts/utils/generate_password_hashes.py --generate

# Verifica que los hashes SQL coinciden con los secrets
verify-sql-hashes:
	python3 scripts/utils/generate_password_hashes.py --verify
```

### 6.2 Modificación de Targets Existentes

Los targets `recreate-*` ahora incluyen regeneración automática de SQL bootstrap:

```makefile
recreate-dev: regenerate-sql-bootstrap
	@echo "==> Recreando entorno DEV desde cero..."
```

---

## 7. Documentación

### 7.1 Nuevos Archivos de Documentación

| Archivo | Propósito |
|---------|-----------|
| `docs/credentials_system.md` | Documentación del sistema de credenciales (sin exponer fórmula) |
| `docs/sessions/2026-01/2026-01-27-6-summary-2359.md` | Resumen de sesión: implementación inicial |
| `docs/sessions/2026-01/2026-01-29-7-summary-0008.md` | Resumen de sesión: verificación de entornos |

### 7.2 Contenido de credentials_system.md

El documento explica:
- Arquitectura del sistema de credenciales
- Flujo de generación
- Requisitos de BCrypt
- Comandos de Makefile disponibles
- Guía de troubleshooting

**Importante**: El documento NO expone la fórmula de generación ni los salts utilizados.

---

## 8. Archivos Modificados

### 8.1 Archivos Nuevos

| Archivo | Propósito |
|---------|-----------|
| `scripts/secrets/generate_user_credentials.py` | Generador de credenciales determinístico |
| `scripts/utils/generate_password_hashes.py` | Generador de hashes BCrypt con prefijo `$2a$` |
| `docs/credentials_system.md` | Documentación del sistema |
| `docs/sessions/2026-01/2026-01-27-6-summary-2359.md` | Resumen de sesión |
| `docs/sessions/2026-01/2026-01-29-7-summary-0008.md` | Resumen de sesión |
| `scripts/seeds/data/*/users.json.example` | Templates de usuarios sin passwords |

### 8.2 Archivos Modificados

| Archivo | Cambios |
|---------|---------|
| `Makefile` | Nuevos targets de credenciales |
| `docker-compose.yml` | Volumen `/secrets:ro` en seeders |
| `backend/.../EmployeeServiceImpl.java` | Vinculación bidireccional User↔Employee |
| `sql/common/05_admin_bootstrap_dev_test.sql` | Hashes regenerados con `$2a$` |
| `sql/common/05_admin_bootstrap_prod.sql` | Hashes regenerados con `$2a$` |
| `scripts/seeds/seed_runner.py` | Soporte para cargar desde `/secrets` |
| `frontend/src/services/api.js` | URL relativa para proxy Vite |

### 8.3 Archivos Eliminados (del tracking de git)

| Archivo | Motivo |
|---------|--------|
| `scripts/seeds/data/dev/users.json` | Movido a secrets (gitignored) |
| `scripts/seeds/data/test/users.json` | Movido a secrets (gitignored) |
| `scripts/seeds/data/prod/users.json` | Movido a secrets (gitignored) |

---

## 9. Commits Incluidos

| Hash | Tipo | Descripción |
|------|------|-------------|
| `4e381b3` | feat(security) | Add BCrypt hash generator with $2a$ prefix conversion |
| `5a54756` | chore(sql) | Regenerate admin bootstrap hashes with $2a$ prefix |
| `9da16f4` | feat(docker) | Add secrets volume mount for API seeder |
| `2351a70` | feat(backend) | Add bidirectional User-Employee linking |
| `92324da` | feat(makefile) | Add credential generation targets |
| `7c46ad5` | refactor(seeds) | Move user credentials to gitignored secrets |
| `da73876` | docs | Add credentials system documentation |
| `c6ea64b` | docs | Add session summary 2026-01-27 credentials and linking |
| `8600a6a` | fix(frontend) | Use relative API URL for Vite proxy compatibility |
| `6606bd6` | docs | Add session summary 2026-01-29 environment verification |

**Total**: 10 commits

---

## 10. Testing y Verificación

### 10.1 Entornos Recreados y Verificados

Los tres entornos fueron recreados desde cero y verificados:

```bash
make recreate-dev   # Puerto 8082
make recreate-test  # Puerto 8083
make recreate-prod  # Puerto 8181
```

### 10.2 Conteo de Tablas

| Tabla | DEV | TEST | PROD |
|-------|-----|------|------|
| role | 3 | 3 | 3 |
| position | 51 | 51 | 51 |
| expense_user | 82 | 50 | 218 |
| employee | 80 | 20 | 250 |
| payroll | 300 | 80 | 1200 |
| expense | 120 | 40 | 480 |

### 10.3 Verificación de Login

| Entorno | Puerto | Resultado |
|---------|--------|-----------|
| DEV | 8082 | ✅ HTTP 200 |
| TEST | 8083 | ✅ HTTP 200 |
| PROD | 8181 | ✅ HTTP 200 |

### 10.4 Verificación de Vinculación User↔Employee

Después de la implementación, el usuario Ada Lovelace tiene correctamente vinculado su `employee_id`:

```sql
SELECT id, email, employee_id FROM expense_user WHERE email = 'ada.lovelace@bizflowerp.com';
-- Resultado: id=1, employee_id=1 ✅
```

---

## 11. Próximos Pasos

### 11.1 Rama `feat/frontend-refactor-2`

Pendiente de implementar en una rama separada:
- Mostrar información del usuario creador en cada gasto
- Otros ajustes de frontend identificados durante testing con Vite

### 11.2 Rama `docs/update-readme`

Después de mergear las ramas de features:
- Actualizar README principal con nueva documentación
- Incluir instrucciones del sistema de credenciales
- Documentar nuevos comandos de Makefile

---

## Checklist de Revisión

- [x] Código compila sin errores
- [x] Tests de integración pasan (login en 3 entornos)
- [x] Documentación actualizada
- [x] No se exponen credenciales en código trackeado
- [x] Archivos sensibles en `.gitignore`
- [x] Commits siguen convención Angular
- [x] Sin conflictos con rama `dev`

---

## Instrucciones para el Revisor

1. **Verificar que no hay credenciales expuestas**: 
   ```bash
   git diff dev..feature/seed-data-refactor | grep -E "password|secret|salt"
   ```

2. **Probar generación de credenciales** (requiere archivos locales):
   ```bash
   python3 scripts/secrets/generate_user_credentials.py --generate
   ```

3. **Verificar login tras recrear entorno**:
   ```bash
   make recreate-dev
   make show-password-formula  # Para ver las credenciales
   curl -X POST http://localhost:8082/api/v1/auth/login -H "Content-Type: application/json" -d '{"email": "ada.lovelace@bizflowerp.com", "password": "<password>"}'
   ```

---

*Documento generado: 29 de Enero de 2026, 21:49*
