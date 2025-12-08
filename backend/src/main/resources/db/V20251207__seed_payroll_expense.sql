-- Corregir los nombres en expense_user: separar name y surname si están juntos
UPDATE expense_user SET name = split_part(email, '.', 1), surname = split_part(email, '.', 2)
WHERE name = surname;
-- V20251207__seed_payroll_expense.sql
-- Script para poblar payroll y expense con datos de ejemplo

-- 1. Nóminas: 5 meses para cada empleado (diciembre a agosto 2025)

INSERT INTO payroll (employee_id, expense_user_id, amount, payroll_date)
SELECT e.id, NULL, 2000 + (random() * 1000)::int, d
FROM employee e,
     (SELECT generate_series('2025-08-01'::date, '2025-12-01'::date, '1 month') AS d) meses;

-- 2. Notas de gasto: al menos 1 por usuario
INSERT INTO expense (expense_user_id, amount, note, expense_date)
SELECT u.id, 100 + (random() * 400)::int, 'Gasto de ejemplo', '2025-12-07'
FROM expense_user u;
