# Secuencia de inicialización automatizada de la base de datos

## Estructura de carpetas y archivos
- [sql/common](../sql/common) agrupa artefactos compartidos: [sql/common/01_schema.sql](../sql/common/01_schema.sql) define tablas y secuencias base, [sql/common/02_positions.sql](../sql/common/02_positions.sql) y [sql/common/03_roles.sql](../sql/common/03_roles.sql) cargan catálogos, [sql/common/05_expense_admin_bootstrap.sql](../sql/common/05_expense_admin_bootstrap.sql) crea los administradores Ada y Alan.
- [sql/dev_prod](../sql/dev_prod) concentra datasets voluminosos para dev y prod: [sql/dev_prod/10_employees_full.sql](../sql/dev_prod/10_employees_full.sql) importa la plantilla completa de empleados, [sql/dev_prod/20_payrolls_full.sql](../sql/dev_prod/20_payrolls_full.sql) aporta el histórico de nóminas y [sql/dev_prod/30_expenses_extended.sql](../sql/dev_prod/30_expenses_extended.sql) conserva el dataset amplio de gastos para cargas puntuales.
- [sql/test](../sql/test) conserva únicamente [sql/test/00_master.sql](../sql/test/00_master.sql); este master reutiliza los datasets de [sql/common](../sql/common) y [sql/dev_prod](../sql/dev_prod) para alinear el entorno de pruebas con dev y prod. Los usuarios de gastos adicionales se generan mediante scripts que consumen la API.
- [sql/dev/00_master.sql](../sql/dev/00_master.sql), [sql/prod/00_master.sql](../sql/prod/00_master.sql) y [sql/test/00_master.sql](../sql/test/00_master.sql) orquestan la ejecución importando los archivos anteriores en un orden coherente.
- La raíz de [sql](../sql) conserva únicamente los entrypoints [sql/01_init_prod.sql](../sql/01_init_prod.sql), [sql/01_init_dev.sql](../sql/01_init_dev.sql) y [sql/01_init_test.sql](../sql/01_init_test.sql); todos los datasets viven en las carpetas anteriores.

## Orden de ejecución por entorno
- **Prod**: [sql/01_init_prod.sql](../sql/01_init_prod.sql) se monta en `/docker-entrypoint-initdb.d/01_init_prod.sql`, delega en [sql/prod/00_master.sql](../sql/prod/00_master.sql) y ejecuta esquema común → catálogos comunes → empleados completos → nóminas completas → bootstrap admin.
- **Dev**: [sql/01_init_dev.sql](../sql/01_init_dev.sql) sigue la misma secuencia a través de [sql/dev/00_master.sql](../sql/dev/00_master.sql).
- **Test**: [sql/01_init_test.sql](../sql/01_init_test.sql) invoca [sql/test/00_master.sql](../sql/test/00_master.sql), reutilizando el mismo orden de prod/dev (esquema → catálogos → empleados completos → nóminas completas → bootstrap admin). Tras el arranque se deben registrar los usuarios de gastos vía API para habilitar posteriores cargas de gastos.

## Registro de expense users vía API
- Una vez que el backend esté healthy, ejecuta `./scripts/register_users.sh` (o `./scripts/register_users_test.sh` para apuntar automáticamente al perfil test). El script consume `/api/v1/auth/signup`, crea los usuarios vinculados a los empleados existentes y deja las contraseñas hasheadas por la aplicación.
- Si un correo ya existe, el script informa la omisión (HTTP 409) y continúa; solo aborta ante códigos inesperados.
- En los perfiles `prod` y `dev` el compose lanza automáticamente los servicios `seed-expense-users-prod` y `seed-expense-users-dev`, que esperan al backend y ejecutan el script con la URL interna (`http://backend-*/:8080`). El script es idempotente: ignora respuestas 409 cuando los usuarios ya existen.

## Carga del dataset extendido de gastos
- [sql/dev_prod/30_expenses_extended.sql](../sql/dev_prod/30_expenses_extended.sql) queda fuera de los masters para evitar fallos por claves foráneas antes de tener usuarios de gastos. Ejecuta la carga manual únicamente cuando requieras el histórico completo.
- Con los expense users ya creados vía API, lanza el script desde la raíz del proyecto:
```bash
docker compose exec <db-container> psql -U postgres -d <dbname> -f /docker-entrypoint-initdb.d/dev_prod/30_expenses_extended.sql
```
- Sustituye `<db-container>` por el contenedor activo (por ejemplo `erp-test-db-container`) y `<dbname>` por la base destino.

## Consideraciones operativas
- Prod y dev conservan datos entre reinicios; los masters solo se ejecutan cuando el volumen está vacío.
- Test se recrea en cada arranque, por lo que la secuencia completa se repite automáticamente.
- Antes de ejecutar scripts que dependen de la API, como [scripts/init-expense-data.sh](../scripts/init-expense-data.sh), verifica que el backend esté healthy.
