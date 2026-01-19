-- =========================================================
-- User catalog for TEST environment
-- 20 users: 15 linked to employees + 5 freelance (user-only)
-- Password format: [rol 3 letters][surname 3 letters][3 digits]
-- Passwords are BCrypt hashed (cost 10)
-- =========================================================

-- ===========================================
-- LINKED USERS (have corresponding employee)
-- ===========================================

-- ADMINS (2)
INSERT INTO expense_user (name, surname, email, password, employee_id) VALUES
('Ada', 'Lovelace', 'ada.lovelace@bizflowerp.com', '$2a$10$d7wIIjI/4Ebe8W0pSoOz9e4RJWJ/sk2FLpcubp4ZFBm4ceF0qkYWy', 1),
('Alan', 'Turing', 'alan.turing@bizflowerp.com', '$2a$10$w7sOwNUnxcmQGwcIZDkXsu9zpHGAF.7ANqeXGfRi2kstvB2ChrBuG', 2);

-- MANAGERS (2)
INSERT INTO expense_user (name, surname, email, password, employee_id) VALUES
('Marie', 'Curie', 'marie.curie@bizflowerp.com', '$2a$10$XdTjiogQZG12ugDTdGmU/u6tca1SNUY6GVQ7gysPFFbcf.qC4IcUq', 3),
('Albert', 'Einstein', 'albert.einstein@bizflowerp.com', '$2a$10$5aXNQ0cAkknUkPOdsVkolOEA3bGImiNgCDLm45N5c844aI5ZjjBx6', 4);

-- USERS linked to employees (11)
INSERT INTO expense_user (name, surname, email, password, employee_id) VALUES
('Isaac', 'Newton', 'isaac.newton@bizflowerp.com', '$2a$10$D4wFBgPn//MzkPYi8nOI6eKrSad9.ym8Z8bhYYTaKbKKd8zNBAxPq', 5),
('Nikola', 'Tesla', 'nikola.tesla@bizflowerp.com', '$2a$10$jjXJZDcjD7FL8tGt8/Lpa.Xbz2U9Gk/VmG37iVoIkggUmu9ubuRTe', 6),
('Nadia', 'Comaneci', 'nadia.comaneci@bizflowerp.com', '$2a$10$4cGpTEmptIrB2Dm2jiVl3.7gL31VfJ.gW/V7fyT732Bwnpc.YUwga', 7),
('Usain', 'Bolt', 'usain.bolt@bizflowerp.com', '$2a$10$KeTg4YY51o7/BKVqu/KmwO.3X4UUugJ.AgKVZJk7uYm.dy4pkdlwG', 8),
('Carl', 'Lewis', 'carl.lewis@bizflowerp.com', '$2a$10$.PQTFutZ5fccesuTnRxgZOST2/wqdVIjcYeH3V4l3P7omZLMGvShK', 9),
('Teofilo', 'Stevenson', 'teofilo.stevenson@bizflowerp.com', '$2a$10$5ldwUSELzeA5IFYeKYO6GOhUzuNhjRuGqkWYw2WCcDf2zybgb9Ipu', 10),
('Akira', 'Kurosawa', 'akira.kurosawa@bizflowerp.com', '$2a$10$q82D3V.WB1d0Ao8U4UHl7.GdwbWEpkqWT5vJEpFTaYnBUrq05y2OO', 11),
('Andrei', 'Tarkovsky', 'andrei.tarkovsky@bizflowerp.com', '$2a$10$ua9zB4uGQuw1dvMA8o7A8OzVsYLQ3oS5LDjgJmyHgrKf2.1IZwb9O', 12),
('Pablo', 'Picasso', 'pablo.picasso@bizflowerp.com', '$2a$10$DvFjyLAx9mrFCC.QNVhHZeLlRXFvnd.Uw7Gg68ZoA6f5rwWn1oxau', 13),
('Fiodor', 'Dostoievski', 'fiodor.dostoievski@bizflowerp.com', '$2a$10$bBKA6CjWtRDbVWCP7iXD.eRcNAdYEcm7ewg.GeX6bVmCo/6/SOW7O', 14),
('Immanuel', 'Kant', 'immanuel.kant@bizflowerp.com', '$2a$10$r9O0OBsX6Y0cwYPVBG/VxO.W9auiBulpumri3yEjH/ylCImkyz5vO', 15);

-- ===========================================
-- FREELANCE USERS (no employee, user-only)
-- ===========================================
INSERT INTO expense_user (name, surname, email, password, employee_id) VALUES
('Platon', 'deAtenas', 'platon.deatenas@bizflowerp.com', '$2a$10$jixr.LdmZ6OKXj3XAjY8Q.3.ZSRcSgNja/nOLYuVnzkwGF.yly.qO', NULL),
('Aristoteles', 'deEstagira', 'aristoteles.deestagira@bizflowerp.com', '$2a$10$ervDTTuUFZ9rSZ1QE/uJoe5RHAAAKGWX98mLIfefmyyYoXVdgZgfi', NULL),
('Emile', 'Durkheim', 'emile.durkheim@bizflowerp.com', '$2a$10$HPmmU9g.dgmy28jXAi0EgeqI.p52534tn2BkYQNCoIM5Jrv1CKkl.', NULL),
('Max', 'Weber', 'max.weber@bizflowerp.com', '$2a$10$nNAgsjoPZDzhWmONqa4bd.9aJJ.o4x.jn2R8.SLaUIrOfNGs16i9u', NULL),
('Hannah', 'Arendt', 'hannah.arendt@bizflowerp.com', '$2a$10$lIMlAiQCOJ9xwGVOuhHDhefgKFQmUQQ6jjzrDBVtkoLVQkX43h.Ru', NULL);

-- ===========================================
-- ROLE ASSIGNMENTS
-- ===========================================

-- Admins (user_id 1-2) -> role_id 1 = ADMIN
INSERT INTO user_role (user_id, role_id) VALUES (1, 1), (2, 1);

-- Managers (user_id 3-4) -> role_id 3 = MANAGER
INSERT INTO user_role (user_id, role_id) VALUES (3, 3), (4, 3);

-- Regular Users (user_id 5-20) -> role_id 2 = USER
INSERT INTO user_role (user_id, role_id) VALUES 
(5, 2), (6, 2), (7, 2), (8, 2), (9, 2), (10, 2),
(11, 2), (12, 2), (13, 2), (14, 2), (15, 2),
(16, 2), (17, 2), (18, 2), (19, 2), (20, 2);

-- ===========================================
-- UPDATE employee.expense_user_id for bidirectional linking
-- ===========================================
UPDATE employee SET expense_user_id = 1 WHERE email = 'ada.lovelace@bizflowerp.com';
UPDATE employee SET expense_user_id = 2 WHERE email = 'alan.turing@bizflowerp.com';
UPDATE employee SET expense_user_id = 3 WHERE email = 'marie.curie@bizflowerp.com';
UPDATE employee SET expense_user_id = 4 WHERE email = 'albert.einstein@bizflowerp.com';
UPDATE employee SET expense_user_id = 5 WHERE email = 'isaac.newton@bizflowerp.com';
UPDATE employee SET expense_user_id = 6 WHERE email = 'nikola.tesla@bizflowerp.com';
UPDATE employee SET expense_user_id = 7 WHERE email = 'nadia.comaneci@bizflowerp.com';
UPDATE employee SET expense_user_id = 8 WHERE email = 'usain.bolt@bizflowerp.com';
UPDATE employee SET expense_user_id = 9 WHERE email = 'carl.lewis@bizflowerp.com';
UPDATE employee SET expense_user_id = 10 WHERE email = 'teofilo.stevenson@bizflowerp.com';
UPDATE employee SET expense_user_id = 11 WHERE email = 'akira.kurosawa@bizflowerp.com';
UPDATE employee SET expense_user_id = 12 WHERE email = 'andrei.tarkovsky@bizflowerp.com';
UPDATE employee SET expense_user_id = 13 WHERE email = 'pablo.picasso@bizflowerp.com';
UPDATE employee SET expense_user_id = 14 WHERE email = 'fiodor.dostoievski@bizflowerp.com';
UPDATE employee SET expense_user_id = 15 WHERE email = 'immanuel.kant@bizflowerp.com';
