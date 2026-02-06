-- ======================================================
-- Common schema definition shared across all environments
-- ======================================================

CREATE TABLE IF NOT EXISTS position (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(128) NOT NULL,
    description VARCHAR(500),
    base_salary DECIMAL(10, 2)
);

CREATE TABLE IF NOT EXISTS employee (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(128) NOT NULL,
    surname VARCHAR(255) NOT NULL,
    birth_date TIMESTAMP NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    position_id BIGINT NOT NULL REFERENCES position(id),
    expense_user_id BIGINT UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS role (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(25) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS expense_user (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(50) NOT NULL,
    password VARCHAR(64) NOT NULL,
    surname VARCHAR(128) NOT NULL,
    employee_id BIGINT UNIQUE,
    CONSTRAINT fk_expense_user_employee FOREIGN KEY (employee_id) REFERENCES employee(id)
);

-- Add deferred FK for bidirectional employee <-> expense_user relationship
ALTER TABLE employee ADD CONSTRAINT fk_employee_expense_user
    FOREIGN KEY (expense_user_id) REFERENCES expense_user(id);

CREATE TABLE IF NOT EXISTS user_role (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    CONSTRAINT fk_user_role_user FOREIGN KEY (user_id) REFERENCES expense_user(id),
    CONSTRAINT fk_user_role_role FOREIGN KEY (role_id) REFERENCES role(id)
);

CREATE TABLE IF NOT EXISTS payroll (
    id BIGSERIAL PRIMARY KEY,
    amount DOUBLE PRECISION NOT NULL,
    payroll_date TIMESTAMP NOT NULL,
    employee_id BIGINT,
    expense_user_id BIGINT,
    CONSTRAINT fk_payroll_employee FOREIGN KEY (employee_id) REFERENCES employee(id),
    CONSTRAINT fk_payroll_expense_user FOREIGN KEY (expense_user_id) REFERENCES expense_user(id)
);

CREATE TABLE IF NOT EXISTS expense (
    id BIGSERIAL PRIMARY KEY,
    amount DOUBLE PRECISION NOT NULL,
    concept VARCHAR(128) NOT NULL,
    expense_date TIMESTAMP NOT NULL,
    note VARCHAR(255) NOT NULL,
    expense_user_id BIGINT NOT NULL,
    CONSTRAINT fk_expense_expense_user FOREIGN KEY (expense_user_id) REFERENCES expense_user(id)
);

CREATE TABLE IF NOT EXISTS notifications (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP(6) NOT NULL,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    message VARCHAR(500) NOT NULL,
    read_at TIMESTAMP(6),
    reference_id BIGINT,
    reference_type VARCHAR(50),
    title VARCHAR(128) NOT NULL,
    type VARCHAR(50) NOT NULL CHECK (type IN (
        'EXPENSE_CREATED', 'EXPENSE_UPDATED', 'EXPENSE_APPROVED', 
        'EXPENSE_REJECTED', 'EXPENSE_DELETED', 'PAYROLL_GENERATED', 
        'PAYROLL_AVAILABLE', 'PAYROLL_REMINDER', 'EMPLOYEE_LINKED', 
        'EMPLOYEE_UNLINKED', 'USER_REGISTERED', 'USER_ROLE_CHANGED', 
        'SYSTEM_ALERT', 'BUDGET_EXCEEDED', 'INFO', 'WARNING'
    )),
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_notifications_user FOREIGN KEY (user_id) REFERENCES expense_user(id)
);
