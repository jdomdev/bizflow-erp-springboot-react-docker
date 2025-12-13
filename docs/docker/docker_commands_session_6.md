# Docker Command Guide

This guide explains all Docker commands used in Session 6, with context and usage examples.

## 1. List All Tables in PostgreSQL (inside container)
```bash
docker exec -it erp-db-container psql -U postgres -d erp_db -c "\dt"
```

## 2. List All Records in a Table
```bash
docker exec -it erp-db-container psql -U postgres -d erp_db -c "SELECT * FROM payroll ORDER BY employee_id, payroll_date;"
```

## 3. Remove Duplicates from Table
```bash
docker exec -it erp-db-container psql -U postgres -d erp_db -c "DELETE FROM payroll WHERE id IN (SELECT id FROM (SELECT id, ROW_NUMBER() OVER (PARTITION BY employee_id, payroll_date ORDER BY id DESC) AS rn FROM payroll) t WHERE t.rn > 1);"
```

## 4. Verify No Duplicates Remain
```bash
docker exec -it erp-db-container psql -U postgres -d erp_db -c "SELECT employee_id, payroll_date, COUNT(*) FROM payroll GROUP BY employee_id, payroll_date HAVING COUNT(*) > 1;"
```

## 5. Count Records in All Tables
```bash
docker exec -it erp-db-container psql -U postgres -d erp_db -c "SELECT 'employee' AS table, COUNT(*) FROM employee UNION ALL SELECT 'expense', COUNT(*) FROM expense UNION ALL SELECT 'expense_user', COUNT(*) FROM expense_user UNION ALL SELECT 'payroll', COUNT(*) FROM payroll UNION ALL SELECT 'position', COUNT(*) FROM position UNION ALL SELECT 'role', COUNT(*) FROM role UNION ALL SELECT 'user_role', COUNT(*) FROM user_role;"
```

## 6. Create a Database Backup with Date/Time Stamp
```bash
docker exec -t erp-db-container pg_dump -U postgres -d erp_db > backups/erpdb_backup_$(date +%Y_%m_%d_%H%M%S).sql
```

## 7. Verify Backup File
```bash
head -20 backups/erpdb_backup_YYYY_MM_DD_HHMMSS.sql
```

## 8. Commit Changes to Git
```bash
git add docs/sql/expenses_sample.sql
git commit -m "Update expenses_sample.sql: add 100 new invented expenses and switch to expense_user_id references"
```

---
Each command is designed for reproducibility and data integrity in a Dockerized development environment.

**Movido a:** docs/docker/docker_commands_session_6.md
