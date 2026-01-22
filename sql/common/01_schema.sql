-- ======================================================
-- Common schema definition shared across all environments
-- ======================================================

CREATE TABLE IF NOT EXISTS position (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(128) NOT NULL
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
