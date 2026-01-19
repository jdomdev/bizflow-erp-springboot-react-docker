-- =========================================================
-- Master bootstrap for DEV environment
-- Medium dataset: 80 employees, 60 users, 500 payrolls, 200 expenses
-- =========================================================

-- Common schema and reference data
\ir ../common/01_schema.sql
\ir ../common/02_positions.sql
\ir ../common/03_roles.sql

-- DEV-specific seed data
\ir 10_employees_dev.sql
\ir ../common/05_expense_admin_bootstrap.sql
\ir 15_users_dev.sql
\ir 20_payrolls_dev.sql
\ir 30_expenses_dev.sql

