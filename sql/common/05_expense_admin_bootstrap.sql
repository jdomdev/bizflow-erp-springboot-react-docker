-- ====================================================================
-- Bootstrap of initial ADMIN expense users shared by all environments
-- ====================================================================
-- Provides two admin accounts so automation scripts can authenticate
-- before seeding the rest of the dataset through the public API.
-- ====================================================================

INSERT INTO expense_user (id, email, name, surname, password, employee_id)
SELECT
    1,
    'ada.lovelace@bizflowerp.com',
    'Ada',
    'Lovelace',
    '$2a$10$s.FQKVY55oU0C1vssmBmJuSMgt1vu.osu4Vm2YvDSO8uaxSCRy8iy',
    e.id
FROM employee e
WHERE e.email = 'ada.lovelace@bizflowerp.com'
ON CONFLICT (id) DO NOTHING;

INSERT INTO expense_user (id, email, name, surname, password, employee_id)
SELECT
    2,
    'alan.turing@bizflowerp.com',
    'Alan',
    'Turing',
    '$2a$10$s436lU.EsWA44e7p4yirgO4/YdcbBNeMktKcwH4u/.fSSC8g1l8Pa',
    e.id
FROM employee e
WHERE e.email = 'alan.turing@bizflowerp.com'
ON CONFLICT (id) DO NOTHING;

INSERT INTO user_role (user_id, role_id) VALUES (1, 1) ON CONFLICT DO NOTHING;
INSERT INTO user_role (user_id, role_id) VALUES (1, 2) ON CONFLICT DO NOTHING;
INSERT INTO user_role (user_id, role_id) VALUES (2, 1) ON CONFLICT DO NOTHING;
INSERT INTO user_role (user_id, role_id) VALUES (2, 2) ON CONFLICT DO NOTHING;

SELECT setval(
    'expense_user_id_seq',
    GREATEST(2, COALESCE((SELECT MAX(id) FROM expense_user), 0)),
    true
);

-- Bidirectional linking for admin users: Update employee.expense_user_id
UPDATE employee e
SET expense_user_id = eu.id
FROM expense_user eu
WHERE eu.employee_id = e.id
  AND e.expense_user_id IS NULL;
