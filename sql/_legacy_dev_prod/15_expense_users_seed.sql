-- ======================================================================
-- Expense users bootstrap replicated for DB initialization
-- ======================================================================
-- Ensures sample expense users exist before loading expense dataset so
-- automatic environment provisioning via docker-compose works without
-- relying on the REST seeding container. Password hashes were generated
-- with bcrypt using cost 10 to match Spring Security defaults.
-- ======================================================================

INSERT INTO expense_user (id, email, name, surname, password, employee_id) VALUES (3, 'grace.hopper@bizflowerp.com', 'Grace', 'Hopper', '$2a$10$irGUHVN.fi0udUcsAP32X.7n/07MEnkI8k1IhSOslh0i1ItdwA.Km', 7) ON CONFLICT (id) DO NOTHING;
INSERT INTO expense_user (id, email, name, surname, password, employee_id) VALUES (4, 'katherine.johnson@bizflowerp.com', 'Katherine', 'Johnson', '$2a$10$8nNFd7lixHXtztoXE75rteW/OD4fknWx1vCaYasq1kt.rOgohVnra', 8) ON CONFLICT (id) DO NOTHING;
INSERT INTO expense_user (id, email, name, surname, password, employee_id) VALUES (5, 'tim.bernerslee@bizflowerp.com', 'Tim', 'Berners-Lee', '$2a$10$vCmwpSpF7t/R.8tpWTZ74eO0TiI484dzK4w20XHlEeKB34DCMH/t.', 9) ON CONFLICT (id) DO NOTHING;
INSERT INTO expense_user (id, email, name, surname, password, employee_id) VALUES (6, 'linus.torvalds@bizflowerp.com', 'Linus', 'Torvalds', '$2a$10$zEgRe.yKc0q6lVE8GGc4Ouma1jQZ0UTSrFnlIFEVhh8OMirahXfOK', 10) ON CONFLICT (id) DO NOTHING;
INSERT INTO expense_user (id, email, name, surname, password, employee_id) VALUES (7, 'margaret.hamilton@bizflowerp.com', 'Margaret', 'Hamilton', '$2a$10$x1mFCzSTaSbInhDp0SiBh.iQ0snOD8MqdwbfmAn7v4cTy4P0OJ5aa', 11) ON CONFLICT (id) DO NOTHING;
INSERT INTO expense_user (id, email, name, surname, password, employee_id) VALUES (8, 'john.vonneumann@bizflowerp.com', 'John', 'von Neumann', '$2a$10$aISRrkdMVF1xOjk.PtlB0.wJOY/Kj5EGsyNaCtMXfshNWzzDaGc9q', 12) ON CONFLICT (id) DO NOTHING;
INSERT INTO expense_user (id, email, name, surname, password, employee_id) VALUES (9, 'dennis.ritchie@bizflowerp.com', 'Dennis', 'Ritchie', '$2a$10$/nCWX3Y5mEkmMoBIEO3Tyu3nnGJuOgNNR9cr7zAn6t8CjF5dCUYs6', 13) ON CONFLICT (id) DO NOTHING;
INSERT INTO expense_user (id, email, name, surname, password, employee_id) VALUES (10, 'donald.knuth@bizflowerp.com', 'Donald', 'Knuth', '$2a$10$IakA/8hq//7FIfdZQ8AZme7Qf2GJ06J6xNEYdvOYP/dSvQuiIUwtO', 14) ON CONFLICT (id) DO NOTHING;
INSERT INTO expense_user (id, email, name, surname, password, employee_id) VALUES (11, 'marie.curie@bizflowerp.com', 'Marie', 'Curie', '$2a$10$8.fByI4TaHvPM4dLG0vI2OJdOzZrjJNVWQlrsjtyViDR/c0PjFnFi', 15) ON CONFLICT (id) DO NOTHING;
INSERT INTO expense_user (id, email, name, surname, password, employee_id) VALUES (12, 'albert.einstein@bizflowerp.com', 'Albert', 'Einstein', '$2a$10$OPf6E5Y3DGiZAXRJNKAXZekQKBYVgDUs0w4f/EX/9kWCc.Zg4cWN.', 16) ON CONFLICT (id) DO NOTHING;
INSERT INTO expense_user (id, email, name, surname, password, employee_id) VALUES (13, 'isaac.newton@bizflowerp.com', 'Isaac', 'Newton', '$2a$10$29Xe6oTz9IkpMmFRU2K4KOz0VWbU6N9gq8XeUbvg3d7LTsitnG0x6', 17) ON CONFLICT (id) DO NOTHING;
INSERT INTO expense_user (id, email, name, surname, password, employee_id) VALUES (14, 'stephen.hawking@bizflowerp.com', 'Stephen', 'Hawking', '$2a$10$kW0JYOK6D3YdZ7ArGE/2zu1xzuZRPJNlTX5hgcXoLiNv09Qsi9Gvq', 18) ON CONFLICT (id) DO NOTHING;
INSERT INTO expense_user (id, email, name, surname, password, employee_id) VALUES (15, 'richard.feynman@bizflowerp.com', 'Richard', 'Feynman', '$2a$10$MaNyn.6WOpx6s3J31t30D.pWk4vblejV3s5QIBfUPzqGg739Y5Bcy', 19) ON CONFLICT (id) DO NOTHING;
INSERT INTO expense_user (id, email, name, surname, password, employee_id) VALUES (16, 'niels.bohr@bizflowerp.com', 'Niels', 'Bohr', '$2a$10$ZxBONegEphPjCJeqTGma1uo/s3XjNCmxIQDDyCqJHpcI5M6.vovNW', 20) ON CONFLICT (id) DO NOTHING;
INSERT INTO expense_user (id, email, name, surname, password, employee_id) VALUES (17, 'galileo.galilei@bizflowerp.com', 'Galileo', 'Galilei', '$2a$10$rYMSLp02fX53qliemFsYA.18j9aWWXfVtZPtxU5WuWNItU6HIFcnW', 21) ON CONFLICT (id) DO NOTHING;
INSERT INTO expense_user (id, email, name, surname, password, employee_id) VALUES (18, 'nikola.tesla@bizflowerp.com', 'Nikola', 'Tesla', '$2a$10$TbQiR2kZ3bcXP4LDgupvS.dDTtNUwDgq6aKus202iRnP1/Eg2rdVG', 22) ON CONFLICT (id) DO NOTHING;
INSERT INTO expense_user (id, email, name, surname, password, employee_id) VALUES (19, 'rosalind.franklin@bizflowerp.com', 'Rosalind', 'Franklin', '$2a$10$YlY3YYbhhV/rwjBA1TCaB.ef.jDbwWdHd5KlgHVQa6fELyJk1xnhS', 23) ON CONFLICT (id) DO NOTHING;
INSERT INTO expense_user (id, email, name, surname, password, employee_id) VALUES (20, 'carl.sagan@bizflowerp.com', 'Carl', 'Sagan', '$2a$10$t1tgwzg9Cxk2rAkcQr.NZOcYpeGledl57ywfdGlW/LhjYCZuAuyIe', 24) ON CONFLICT (id) DO NOTHING;
INSERT INTO expense_user (id, email, name, surname, password, employee_id) VALUES (21, 'michael.jordan@bizflowerp.com', 'Michael', 'Jordan', '$2a$10$dsPONaescABfvIwL0iApn.I7lcWGPNoBTV7OjyVRkpGBwa3KGM55.', 25) ON CONFLICT (id) DO NOTHING;
INSERT INTO expense_user (id, email, name, surname, password, employee_id) VALUES (22, 'serena.williams@bizflowerp.com', 'Serena', 'Williams', '$2a$10$avNzABuG5D5GUEYoy6QIbOZr4WJdG3NezcYoUjPz4x/qwgiDookLS', 26) ON CONFLICT (id) DO NOTHING;
INSERT INTO expense_user (id, email, name, surname, password, employee_id) VALUES (23, 'usain.bolt@bizflowerp.com', 'Usain', 'Bolt', '$2a$10$5KBGkx/bu7NhGq80HCnyfuFr.80kVPz7AJ8xf4J41ozRWG/UKGWoq', 27) ON CONFLICT (id) DO NOTHING;
INSERT INTO expense_user (id, email, name, surname, password, employee_id) VALUES (24, 'simone.biles@bizflowerp.com', 'Simone', 'Biles', '$2a$10$n8wEko9QWrBNVfA.GdqTuO5Zr7hOmBh5evoKW/yRZnYHYa52ZH.l6', 28) ON CONFLICT (id) DO NOTHING;
INSERT INTO expense_user (id, email, name, surname, password, employee_id) VALUES (25, 'lionel.messi@bizflowerp.com', 'Lionel', 'Messi', '$2a$10$lTnDUNeTJND8PK5TIxV7demtTnFeZJtHf51jbTOXuVb7uScGWvYrm', 29) ON CONFLICT (id) DO NOTHING;
INSERT INTO expense_user (id, email, name, surname, password, employee_id) VALUES (26, 'cristiano.ronaldo@bizflowerp.com', 'Cristiano', 'Ronaldo', '$2a$10$acTRhlDXD3F3qZQ.1X2viekueFF/jDe2VLTFwGlwVnxB7JMWT8aVy', 30) ON CONFLICT (id) DO NOTHING;
INSERT INTO expense_user (id, email, name, surname, password, employee_id) VALUES (27, 'roger.federer@bizflowerp.com', 'Roger', 'Federer', '$2a$10$G/WyLA0th0cQqjdwEIMU9eCM.Ha6qUB2SlHMe3r5WuXX6uYGxTWl6', 31) ON CONFLICT (id) DO NOTHING;
INSERT INTO expense_user (id, email, name, surname, password, employee_id) VALUES (28, 'rafael.nadal@bizflowerp.com', 'Rafael', 'Nadal', '$2a$10$OQSBxCDtI/pTchtJ3Tju0eYfGZHTxlLLadrOEpGrks4zm/S1lBy2e', 32) ON CONFLICT (id) DO NOTHING;
INSERT INTO expense_user (id, email, name, surname, password, employee_id) VALUES (29, 'pele.nascimento@bizflowerp.com', 'Pele', 'Nascimento', '$2a$10$5zm7r2BDWtKag4rzSarU2uQMn/Kl1YR5v2ggjZMG0qerIWJk8kIM2', 33) ON CONFLICT (id) DO NOTHING;
INSERT INTO expense_user (id, email, name, surname, password, employee_id) VALUES (30, 'diego.maradona@bizflowerp.com', 'Diego', 'Maradona', '$2a$10$qhC1Pn/qX9S0FMCKD86ohOQOrPBo2HHAUCiwkjxclb1Am9OS0uSC.', 34) ON CONFLICT (id) DO NOTHING;
INSERT INTO expense_user (id, email, name, surname, password, employee_id) VALUES (31, 'simone.weil@bizflowerp.com', 'Simone', 'Weil', '$2a$10$pelkgB0x4Qps0D4.TGsDtuh5HHywezjMPY7/u.Wv7bl9ue5pXysvS', 35) ON CONFLICT (id) DO NOTHING;
INSERT INTO expense_user (id, email, name, surname, password, employee_id) VALUES (32, 'florence.nightingale@bizflowerp.com', 'Florence', 'Nightingale', '$2a$10$jXZzdzdUQaTQf3cR7arngufHN9W.fg5qkDnag8NFaHFbDqBNicgkC', 36) ON CONFLICT (id) DO NOTHING;
INSERT INTO expense_user (id, email, name, surname, password, employee_id) VALUES (33, 'jane.goodall@bizflowerp.com', 'Jane', 'Goodall', '$2a$10$/fiZ3l1Z9lOkZ9j3IYt1p.dES4E51BsiycTfjf2BvARBNf/YjdJ0C', 37) ON CONFLICT (id) DO NOTHING;
INSERT INTO expense_user (id, email, name, surname, password, employee_id) VALUES (34, 'sally.ride@bizflowerp.com', 'Sally', 'Ride', '$2a$10$9Qi85oWCuja5eL0x40RD/unHsz4TXvlP4m.SSKbrFgnFxblLGhHTC', 38) ON CONFLICT (id) DO NOTHING;
INSERT INTO expense_user (id, email, name, surname, password, employee_id) VALUES (35, 'neil.armstrong@bizflowerp.com', 'Neil', 'Armstrong', '$2a$10$Me8xCZCHH7mwYxYhC8dqUev5i.mc2sZjsASslawn3y3jhxIrkcNaG', 39) ON CONFLICT (id) DO NOTHING;
INSERT INTO expense_user (id, email, name, surname, password, employee_id) VALUES (36, 'yuri.gagarin@bizflowerp.com', 'Yuri', 'Gagarin', '$2a$10$L4EOy.PA8/YWLYxQlFFsve.FRG1ltNXGOGoH0XbDIEE1xavp1tJOm', 40) ON CONFLICT (id) DO NOTHING;
INSERT INTO expense_user (id, email, name, surname, password, employee_id) VALUES (37, 'valentina.tereshkova@bizflowerp.com', 'Valentina', 'Tereshkova', '$2a$10$oMozPs2a8G9oEntDDNN0LOpfFSQ.eT3D02MfDrmYLLRYU49tHvXFa', 41) ON CONFLICT (id) DO NOTHING;
INSERT INTO expense_user (id, email, name, surname, password, employee_id) VALUES (38, 'amelia.earhart@bizflowerp.com', 'Amelia', 'Earhart', '$2a$10$7kFve11SlU7D5YTWuFcszuRa98ceZTTzUAQ4LW6J5zVqcKYvCHjii', 42) ON CONFLICT (id) DO NOTHING;
INSERT INTO expense_user (id, email, name, surname, password, employee_id) VALUES (39, 'steve.jobs@bizflowerp.com', 'Steve', 'Jobs', '$2a$10$2n6kXkwKwn4iIqj2cFfKgOd3kui/bxNvDJRXhHbkvZDGVmSCZ8d7a', 43) ON CONFLICT (id) DO NOTHING;
INSERT INTO expense_user (id, email, name, surname, password, employee_id) VALUES (40, 'bill.gates@bizflowerp.com', 'Bill', 'Gates', '$2a$10$7wcQng5cl.WZQJ.s9v2RMePcMUii2y1c8d.xsuFFDhDsMkXboMw5K', 44) ON CONFLICT (id) DO NOTHING;
INSERT INTO expense_user (id, email, name, surname, password, employee_id) VALUES (41, 'mark.zuckerberg@bizflowerp.com', 'Mark', 'Zuckerberg', '$2a$10$NuAQVVZlvjuoBf2qqDyJz.mKQd98OUsQNqvmlymdfrPwJFcVJL/Jq', 45) ON CONFLICT (id) DO NOTHING;
INSERT INTO expense_user (id, email, name, surname, password, employee_id) VALUES (42, 'jeff.bezos@bizflowerp.com', 'Jeff', 'Bezos', '$2a$10$SXU77qZbsHsS4aW/XqV9a.fCWuX6YJIHmoyt0tUkjaVtWtOJMxemG', 46) ON CONFLICT (id) DO NOTHING;
INSERT INTO expense_user (id, email, name, surname, password, employee_id) VALUES (43, 'elon.musk@bizflowerp.com', 'Elon', 'Musk', '$2a$10$Mn1wOJbX4LVYR7amunwwTOq.PAAyiopymgrSZ4lo8kZzSiHD8gYAG', 47) ON CONFLICT (id) DO NOTHING;
INSERT INTO expense_user (id, email, name, surname, password, employee_id) VALUES (44, 'larry.page@bizflowerp.com', 'Larry', 'Page', '$2a$10$GQHe6Xfr12ee3VcwMD2ezOjhzN0OaGDpoCj0OzqRNHw4laMiOWCmC', 48) ON CONFLICT (id) DO NOTHING;
INSERT INTO expense_user (id, email, name, surname, password, employee_id) VALUES (45, 'sergey.brin@bizflowerp.com', 'Sergey', 'Brin', '$2a$10$0IFzbJSQXi2b2rTT6mCfOuN/UWkkodgSdpgrW5/7b6qTRPgYlKfV2', 49) ON CONFLICT (id) DO NOTHING;
INSERT INTO expense_user (id, email, name, surname, password, employee_id) VALUES (46, 'sheryl.sandberg@bizflowerp.com', 'Sheryl', 'Sandberg', '$2a$10$02LehwLhf7zBaKDKaigfiOVbh7qZNk0FmfYWTDR4ANPsuGethveHO', 50) ON CONFLICT (id) DO NOTHING;

-- Assign MANAGER role to users 3-12
INSERT INTO user_role (user_id, role_id)
SELECT eu.id, r.id
FROM expense_user eu
JOIN role r ON r.name = 'MANAGER'
LEFT JOIN user_role ur ON ur.user_id = eu.id AND ur.role_id = r.id
WHERE eu.id BETWEEN 3 AND 12
    AND ur.user_id IS NULL;

-- Ensure every seeded expense user gets the default USER role while avoiding duplicates
INSERT INTO user_role (user_id, role_id)
SELECT eu.id, r.id
FROM expense_user eu
JOIN role r ON r.name = 'USER'
LEFT JOIN user_role ur ON ur.user_id = eu.id AND ur.role_id = r.id
WHERE eu.id >= 3
    AND ur.user_id IS NULL;

SELECT setval(
    'expense_user_id_seq',
    GREATEST(46, COALESCE((SELECT MAX(id) FROM expense_user), 0)),
    true
);

-- Bidirectional linking: Update employee.expense_user_id to match expense_user.employee_id
UPDATE employee e
SET expense_user_id = eu.id
FROM expense_user eu
WHERE eu.employee_id = e.id
  AND e.expense_user_id IS NULL;
