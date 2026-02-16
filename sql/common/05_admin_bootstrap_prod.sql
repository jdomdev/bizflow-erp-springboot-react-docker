-- ====================================================================
-- Bootstrap ADMIN users for PROD environment
-- ====================================================================
-- AUTO-GENERATED FILE - DO NOT EDIT MANUALLY
-- Generated: 2026-01-27 23:53:12
-- Source: scripts/secrets/users_with_passwords/
-- Regenerate: python3 scripts/utils/generate_password_hashes.py --generate
-- ====================================================================

INSERT INTO expense_user (id, email, name, surname, password, employee_id)
VALUES (
    1,
    'ada.lovelace@bizflowerp.com',
    'Ada',
    'Lovelace',
    '$2a$10$ym8o2IgVEUuFxdrdHRKyr..elZRWqP15rb.PKywfKySONo4CjnmSC',
    NULL
)
ON CONFLICT (id) DO NOTHING;

INSERT INTO expense_user (id, email, name, surname, password, employee_id)
VALUES (
    2,
    'alan.turing@bizflowerp.com',
    'Alan',
    'Turing',
    '$2a$10$lMuiY9.ALc5ryUSGzz9URen6GUUEQ11xNxwdXdTFfhkrSq7LewqbG',
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
