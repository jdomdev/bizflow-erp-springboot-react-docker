-- V20251207__expense_payroll_user_migration.sql
-- 1. Eliminar employee_id de expense y añadir expense_user_id
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name='expense' AND column_name='employee_id'
    ) THEN
        ALTER TABLE expense DROP COLUMN employee_id;
    END IF;
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name='expense' AND column_name='expense_user_id'
    ) THEN
        ALTER TABLE expense ADD COLUMN expense_user_id BIGINT NOT NULL;
        ALTER TABLE expense ADD CONSTRAINT fk_expense_user_expense FOREIGN KEY (expense_user_id) REFERENCES expense_user(id);
    END IF;
END $$;

-- 2. payroll: añadir expense_user_id nullable y employee_id nullable
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name='payroll' AND column_name='employee_id'
    ) THEN
        ALTER TABLE payroll ADD COLUMN employee_id BIGINT;
    END IF;
    ALTER TABLE payroll ALTER COLUMN employee_id DROP NOT NULL;
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name='payroll' AND column_name='expense_user_id'
    ) THEN
        ALTER TABLE payroll ADD COLUMN expense_user_id BIGINT;
        ALTER TABLE payroll ADD CONSTRAINT fk_payroll_expense_user FOREIGN KEY (expense_user_id) REFERENCES expense_user(id);
    END IF;
END $$;

-- 3. Borrar datos de payroll
TRUNCATE TABLE payroll RESTART IDENTITY CASCADE;
