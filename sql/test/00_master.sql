-- =========================================================
-- Master bootstrap for TEST environment
-- Smallest dataset: 20 employees, 20 users, 80 payrolls, 40 expenses
-- =========================================================

-- Common schema and reference data
\ir ../common/01_schema.sql
\ir ../common/02_positions.sql
\ir ../common/03_roles.sql

-- TEST-specific seed data
\ir 10_employees_test.sql
\ir ../common/05_expense_admin_bootstrap.sql
\ir 15_users_test.sql
\ir 20_payrolls_test.sql
\ir 30_expenses_test.sql

