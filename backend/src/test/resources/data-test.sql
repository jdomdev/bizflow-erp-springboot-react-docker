-- Seed data for tests running with the H2 profile

-- Roles required by security tests
INSERT INTO role (id, name) VALUES (1, 'ROLE_ADMIN');
INSERT INTO role (id, name) VALUES (2, 'ROLE_USER');

-- Positions required by employee-related tests
INSERT INTO position (id, name, description, base_salary) VALUES (1, 'Project Manager', 'Leads projects and coordinates teams', 65000.00);
INSERT INTO position (id, name, description, base_salary) VALUES (2, 'Developer', 'Develops and maintains software applications', 55000.00);
INSERT INTO position (id, name, description, base_salary) VALUES (3, 'Tester', 'Ensures software quality through testing', 45000.00);

-- Expense users referenced by Expense tests
INSERT INTO expense_user (id, name, surname, email, password, employee_id) VALUES
  (58, 'Sylvester', 'Stewart', 'slystone@gmail.com', 'dummyPassword', NULL),
  (59, 'Alex', 'Turner', 'alex.turner@example.com', 'dummyPassword', NULL),
  (60, 'Juan', 'Pérez', 'juanperez@gmail.com', 'dummyPassword', NULL);

-- Align identity sequences after manual inserts
ALTER TABLE role ALTER COLUMN id RESTART WITH 3;
ALTER TABLE position ALTER COLUMN id RESTART WITH 4;
ALTER TABLE expense_user ALTER COLUMN id RESTART WITH 61;
