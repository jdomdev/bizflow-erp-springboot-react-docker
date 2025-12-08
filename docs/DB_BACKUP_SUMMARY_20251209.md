# Resumen de Backup y Estado de la Base de Datos (2025-12-09)

## Proceso de Backup
- Se realizó un backup de la base de datos PostgreSQL `erp_db` usando el comando:
  ```bash
  docker exec erp-db-container pg_dump -U postgres -d erp_db > backups/erpdb_backup_20251209_001606.sql
  ```
- El archivo generado tiene un timestamp en el nombre para trazabilidad y orden.
- **Importante:** Los backups no se guardan en el repositorio por política de seguridad y buenas prácticas. Se recomienda almacenarlos en un sistema externo seguro.

## Esquema y Estado de Tablas
- Tablas principales:
  - `employee`: Estructura creada, sin datos.
  - `expense`: Estructura creada, sin datos.
  - `expense_user`: 46 usuarios registrados, todos con nombre, apellido, email y password.
  - `payroll`: Estructura creada, sin datos.
  - `position`: 102 posiciones registradas.
  - `role`: 3 roles registrados (`ADMIN`, `USER`, `MANAGER`).
  - `user_role`: 46 usuarios con rol USER, Alan Turing y Ada Lovelace con rol ADMIN.

## Buenas Prácticas
- **No versionar backups**: Mantener los archivos de backup fuera del control de versiones.
- **Backup frecuente**: Realizar backups antes de operaciones críticas (limpieza de volúmenes, migraciones, etc).
- **Restauración**: Para restaurar, usar el comando:
  ```bash
  cat backups/erpdb_backup_YYYYMMDD_HHMMSS.sql | docker exec -i erp-db-container psql -U postgres -d erp_db
  ```

## Observaciones
- El backup refleja el estado actual: usuarios y roles poblados, tablas de empleados, gastos y nómina vacías.
- El backup se generó antes de una limpieza profunda de Docker para evitar pérdida de datos.

---
**Fecha y hora de operación:** 2025-12-09 00:16
