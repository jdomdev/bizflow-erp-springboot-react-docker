-- ====================================================================
-- Bootstrap ADMIN users for DEV/TEST environments
-- ====================================================================
-- See scripts/secrets/ → See scripts/secrets/
-- ====================================================================

INSERT INTO expense_user (id, email, name, surname, password, employee_id)
VALUES (
    1,
    'ada.lovelace@bizflowerp.com',
    'Ada',
    'Lovelace',
    '$2b$10$GsdSf7CN39U3VKbe1cV9.eUOJ3dFM5N9o2Off81If1nqRiBKAGlUy',
    NULL
)
ON CONFLICT (id) DO NOTHING;

INSERT INTO expense_user (id, email, name, surname, password, employee_id)
VALUES (
    2,
    'alan.turing@bizflowerp.com',
    'Alan',
    'Turing',
    '$2b$10$qKM3arL3zZZCL14JnUplTOHbIQcWw/hSNHlmFgQh2aDSTtffED9Na',
    NULL
)
ON CONFLICT (id) DO NOTHING;

-- Assign ADMIN and USER roles
INSERT INTO user_role (user_id, role_id) VALUES (1, 1) ON CONFLICT DO NOTHING;
INSERT INTO user_role (user_id, role_id) VALUES (1, 2) ON CONFLICT DO NOTHING;
INSERT INTO user_role (user_id, role_id) VALUES (2, 1) ON CONFLICT DO NOTHING;
INSERT INTO user_role (user_id, role_id) VALUES (2, 2) ON CONFLICT DO NOTHING;

-- Reset sequence
SELECT setval('expense_user_id_seq', GREATEST(2, COALESCE((SELECT MAX(id) FROM expense_user), 0)), true);
