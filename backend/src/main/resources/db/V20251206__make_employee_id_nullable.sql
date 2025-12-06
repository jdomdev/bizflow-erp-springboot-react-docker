-- V20251206__make_employee_id_nullable.sql
-- Migration: Make employee_id nullable in expense_user, add column if missing

DO $$
BEGIN
    -- Add column if it does not exist
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name='expense_user' AND column_name='employee_id'
    ) THEN
        ALTER TABLE expense_user ADD COLUMN employee_id BIGINT;
    END IF;
    -- Make column nullable
    ALTER TABLE expense_user ALTER COLUMN employee_id DROP NOT NULL;
    -- Add foreign key if not exists
    DO $$
    BEGIN
        IF NOT EXISTS (
            SELECT 1 FROM information_schema.table_constraints tc
            JOIN information_schema.key_column_usage kcu
            ON tc.constraint_name = kcu.constraint_name
            WHERE tc.table_name = 'expense_user'
            AND tc.constraint_type = 'FOREIGN KEY'
            AND kcu.column_name = 'employee_id'
        ) THEN
            ALTER TABLE expense_user ADD CONSTRAINT fk_expense_user_employee FOREIGN KEY (employee_id) REFERENCES employee(id);
        END IF;
    END $$;
END $$;
