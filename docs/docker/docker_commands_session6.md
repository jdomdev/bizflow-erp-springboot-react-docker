# Guía de comandos Docker - Sesión 6

Esta guía resume los comandos ejecutados durante la sesión 6 y explica el contexto de cada uno.

## 1. Listar todas las tablas en PostgreSQL (dentro del contenedor)
```bash
docker exec -it erp-db-container psql -U postgres -d erp_db -c "\dt"
```

## 2. Listar todos los registros de una tabla
```bash
docker exec -it erp-db-container psql -U postgres -d erp_db -c "SELECT * FROM payroll ORDER BY employee_id, payroll_date;"
```

## 3. Eliminar duplicados de la tabla `payroll`
```bash
docker exec -it erp-db-container psql -U postgres -d erp_db -c "DELETE FROM payroll WHERE id IN (SELECT id FROM (SELECT id, ROW_NUMBER() OVER (PARTITION BY employee_id, payroll_date ORDER BY id DESC) AS rn FROM payroll) t WHERE t.rn > 1);"
```

## 4. Verificar que no quedan duplicados
```bash
docker exec -it erp-db-container psql -U postgres -d erp_db -c "SELECT employee_id, payroll_date, COUNT(*) FROM payroll GROUP BY employee_id, payroll_date HAVING COUNT(*) > 1;"
```

## 5. Contar registros en todas las tablas
```bash
docker exec -it erp-db-container psql -U postgres -d erp_db -c "SELECT 'employee' AS table, COUNT(*) FROM employee UNION ALL SELECT 'expense', COUNT(*) FROM expense UNION ALL SELECT 'expense_user', COUNT(*) FROM expense_user UNION ALL SELECT 'payroll', COUNT(*) FROM payroll UNION ALL SELECT 'position', COUNT(*) FROM position UNION ALL SELECT 'role', COUNT(*) FROM role UNION ALL SELECT 'user_role', COUNT(*) FROM user_role;"
```

## 6. Crear un backup con sello de fecha y hora
```bash
docker exec -t erp-db-container pg_dump -U postgres -d erp_db > backups/erpdb_backup_$(date +%Y_%m_%d_%H%M%S).sql
```

## 7. Verificar el backup generado
```bash
head -20 backups/erpdb_backup_YYYY_MM_DD_HHMMSS.sql
```

## 8. Confirmar cambios en Git
```bash
git add docs/sql/expenses_sample.sql
git commit -m "Update expenses_sample.sql: add 100 new invented expenses and switch to expense_user_id references"
```

---
Cada comando está orientado a mantener la reproducibilidad y la integridad de datos en el entorno Dockerizado del proyecto.
