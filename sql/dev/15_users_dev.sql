-- =========================================================
-- User catalog for DEV environment
-- 60 users: 50 linked to employees + 10 freelance (user-only)
-- Password format: [rol 3 letters][surname 3 letters][3 digits]
-- Passwords are BCrypt hashed (cost 10)
-- =========================================================
-- NOTE: Ada Lovelace (id=1) and Alan Turing (id=2) are already 
-- created by 05_expense_admin_bootstrap.sql. We start from id=3.
-- =========================================================

-- ===========================================
-- ADMINS (2 additional - ids 3-4)
-- ===========================================
INSERT INTO expense_user (name, surname, email, password, employee_id) VALUES
('Grace', 'Hopper', 'grace.hopper@bizflowerp.com', '$2a$10$83QN6mpKw8TOC1Hdqi2zFu/lxGJUg2mKXGBi2/pFwfyd507VpGqPi', 3),
('John', 'VonNeumann', 'john.vonneumann@bizflowerp.com', '$2a$10$Ak8GeIgYHPFlSNSzeuVN4ew0ab2q1WjbKANP1sTEKyfABqSoa3kyC', 4);

-- ===========================================
-- MANAGERS (6)
-- ===========================================
INSERT INTO expense_user (name, surname, email, password, employee_id) VALUES
('Marie', 'Curie', 'marie.curie@bizflowerp.com', '$2a$10$qXDQ28Fp/ZYnu5GX/ykHDu5z/I1ymHFxgwLZVf5WLQIw0qD2Dg4SG', 5),
('Albert', 'Einstein', 'albert.einstein@bizflowerp.com', '$2a$10$mI4DAblqXn/PFEwA4yNOKevFw9vaMgpFGQg/QRYxr0M2T5Z0helRq', 6),
('Larisa', 'Latynina', 'larisa.latynina@bizflowerp.com', '$2a$10$Wm0eJ0E6h7GvJhb1qFW./un8e.A9zQisYYz8wlKpWrQvc8/r8UA2.', 27),
('Nadia', 'Comaneci', 'nadia.comaneci@bizflowerp.com', '$2a$10$LnlqDTujVlZFikgZ9..XT.7Aw2BQVP92PLDH.bpyx0qGCYWXEao9K', 38),
('Akira', 'Kurosawa', 'akira.kurosawa@bizflowerp.com', '$2a$10$/E2KEk1psN/CbJa5UEdJiOTA8OePp7ZEE7mvHgYHaZfsGfOSqqR9q', 41),
('Pablo', 'Picasso', 'pablo.picasso@bizflowerp.com', '$2a$10$JxDkjESFPMFC//BoqnBv/Ogsano7DdB.nr9aoTay0jpBRRbiY2Qe2', 56);

-- ===========================================
-- USERS linked to employees (40)
-- ===========================================
INSERT INTO expense_user (name, surname, email, password, employee_id) VALUES
('Isaac', 'Newton', 'isaac.newton@bizflowerp.com', '$2a$10$fJS/9ZUS7Gx6HZQ5BN07yOVdyoWzz5UGkms90C1ZWL7pGX39VicVO', 7),
('Nikola', 'Tesla', 'nikola.tesla@bizflowerp.com', '$2a$10$X.kOihGwa2wkc72HS7kvION60TZS/NIKknwTrfPmoYslE8OTFA4iy', 8),
('Richard', 'Feynman', 'richard.feynman@bizflowerp.com', '$2a$10$ZL7MtTX4yHDOMSjoq2JxEuaZk/tho0c.lMqsOqLJENeL8ggSUfgJm', 9),
('Niels', 'Bohr', 'niels.bohr@bizflowerp.com', '$2a$10$l0468FtZVj/KspgbUEZgsekZYlcVXT7F/8rFkQgHpj2HC1xz2yTAS', 10),
('Rosalind', 'Franklin', 'rosalind.franklin@bizflowerp.com', '$2a$10$MUaE.rtfbcH/kZ4TLqXE4uTKMshWlHMWD.TP3oNwt/QhNkEZk7PPC', 11),
('Dorothy', 'Hodgkin', 'dorothy.hodgkin@bizflowerp.com', '$2a$10$Lr/Mg00mpckYqtcgBPfiEeGHMF.g3jjM814UalMyFvdqW01CuW4gS', 12),
('James', 'ClerkMaxwell', 'james.clerkmaxwell@bizflowerp.com', '$2a$10$VYvf0sO57lWUupD39TQSJOp9vcuVxHAfuDukO3D9qS0cmZs1WTkRi', 13),
('Michael', 'Faraday', 'michael.faraday@bizflowerp.com', '$2a$10$TNF.lwoDMGcMr6AuXvdXteBK4oXaZ3jRbBCRgV1uUOi5dBKojLhOy', 14),
('Galileo', 'Galilei', 'galileo.galilei@bizflowerp.com', '$2a$10$puUPniPE8dkUq5Z9RaJrfO.M2MTwT5r4hP0HkBNMNUPqci/Uh8kOK', 15),
('Johannes', 'Kepler', 'johannes.kepler@bizflowerp.com', '$2a$10$RDI7bzjWz8MLdNZnlbCVGuEn9rvnsDTjre6mcn7.PSd8CkWyrP83e', 16),
('Charles', 'Darwin', 'charles.darwin@bizflowerp.com', '$2a$10$RUvZUHObbSdiFH2cO.6juebfH0pAgsT/eQ/h.QGgiqqiAKwyUt8mi', 17),
('Teofilo', 'Stevenson', 'teofilo.stevenson@bizflowerp.com', '$2a$10$5W7dnoR0IX9Kz55wIpD6u.Fr3v/FutemeI3w.SnojEXTpo1w3CRH.', 21),
('Felix', 'Savon', 'felix.savon@bizflowerp.com', '$2a$10$w7POV4SD/xG7vBb0pQ7UH.F9hl29DOfDPsq2AT0wmeXSXjynRMXCC', 22),
('Javier', 'Sotomayor', 'javier.sotomayor@bizflowerp.com', '$2a$10$/nJOdtUxLKtKjDK3lR7noO35efsL7A26IVCYH1D2jy29S2YZ8DKZa', 23),
('Nikolai', 'Andrianov', 'nikolai.andrianov@bizflowerp.com', '$2a$10$uoeHziRbaVkpBc91iUWJ5O5zBba.YzZh0IsnpoItJzz7dLkD4LVm2', 28),
('Alexander', 'Karelin', 'alexander.karelin@bizflowerp.com', '$2a$10$MTqHjMyS9pWp5VrFvg0oMewduXVXrb5dMKwAixzWb5DifB1/ksy.6', 29),
('Lev', 'Yashin', 'lev.yashin@bizflowerp.com', '$2a$10$SNxtEI/dQ/bJRmhgirWT0e59Src3di8.CDineinkzjmBuobTPgS5a', 30),
('Vitaly', 'Scherbo', 'vitaly.scherbo@bizflowerp.com', '$2a$10$QVRlNNnKADeATOOBnIcw6.BE96jDWMqhC3eaz8DCzHs8IJ.yCNzse', 35),
('Usain', 'Bolt', 'usain.bolt@bizflowerp.com', '$2a$10$iAg0tssjcykZge/TsH86huPzcyt2UYLU1fKQwpDOlS/R7tL1AavJy', 39),
('Carl', 'Lewis', 'carl.lewis@bizflowerp.com', '$2a$10$aLX5rczefkzv.sqPZnoPBe5fzHNVIZ7HX/LlHrk4N1gJSS0mK2SfS', 40),
('Andrei', 'Tarkovsky', 'andrei.tarkovsky@bizflowerp.com', '$2a$10$NQfoDTD1QFtVMxw.rPkgIe.W3Lw9XBcnXoFaH5DphNyc6dq158w8K', 42),
('Federico', 'Fellini', 'federico.fellini@bizflowerp.com', '$2a$10$duqPFjEVOAjkKwxF9VJYrubFdI5to7MS2f4thUotnwJkwrQrSHDEW', 43),
('Ingmar', 'Bergman', 'ingmar.bergman@bizflowerp.com', '$2a$10$zZvp3KCQQO3gJcqDmqng9OfxPlt.5R2naSzlOGpUh/mQwSL/hHjCK', 44),
('Stanley', 'Kubrick', 'stanley.kubrick@bizflowerp.com', '$2a$10$gvrtgrFxEpQ48d.uS.HpG.5INfmnekuz8Klvkh0KPQ3LMAAOEhzhm', 45),
('Alfred', 'Hitchcock', 'alfred.hitchcock@bizflowerp.com', '$2a$10$M47bM64VSE22Yw4iH0MV4OF2WP2bhoC82atLSQ4XFNWMbvxWHfqBK', 46),
('Francis', 'FordCoppola', 'francis.fordcoppola@bizflowerp.com', '$2a$10$k2ZsKhHgjS.qSBcpnN3Y5uoTgDhyB.gpWNLuI2JY.k3ck.CAl8i9.', 47),
('Vincent', 'VanGogh', 'vincent.vangogh@bizflowerp.com', '$2a$10$TFon0GnmbIILkjXMwByah.dDz36FDk9UD2mYXW/o1XVYWY6GvJ.YG', 57),
('Claude', 'Monet', 'claude.monet@bizflowerp.com', '$2a$10$38sTksUXm1wlBmz0QhVDhO9cbPBGkvUzPZL8oEifzB0Qfj8orYBoi', 58),
('Salvador', 'Dali', 'salvador.dali@bizflowerp.com', '$2a$10$PrDLRFO0Vw2hC2gRbI8ow.MdUrA0xjS094OOFR1OUW6ObSxnP9rGi', 59),
('Fiodor', 'Dostoievski', 'fiodor.dostoievski@bizflowerp.com', '$2a$10$6IR9X0BTNth6y56uamQs/OCQNdU9NXUVHb9X/6FbXt.6hdAeWhvEq', 64),
('Leon', 'Tolstoi', 'leon.tolstoi@bizflowerp.com', '$2a$10$UdHl6PoVjyARDxR85C8EaeHFFVAgwQSufNx0rCRmeGIR7OZ1xz9oa', 65),
('Gabriel', 'GarciaMarquez', 'gabriel.garciamarquez@bizflowerp.com', '$2a$10$zw3Ejy4JPRx5jFwE4hEbfefAH8nk0CR1uKluqYJGcE8JOVMlZUTTa', 66),
('Immanuel', 'Kant', 'immanuel.kant@bizflowerp.com', '$2a$10$FW.PYl0w4Lrd.K8gCMvQl.rChKIgHmPuQ0sHh3PHvzckwBuA0tpiu', 71),
('Friedrich', 'Nietzsche', 'friedrich.nietzsche@bizflowerp.com', '$2a$10$eP86VzCLJcfK9N144QULAul2ORUuIyrVX8bPrUzWbS5xaFJS/PAhG', 72),
('Ludwig', 'Wittgenstein', 'ludwig.wittgenstein@bizflowerp.com', '$2a$10$YxT6huPrV08QrixjuwAh2.3m9UO3Yjq./bfDTwtGDnVmsU9628uPi', 73),
('Martin', 'Heidegger', 'martin.heidegger@bizflowerp.com', '$2a$10$TH4NXCHQ15O0bk9oMFOZ1.cxMi7zoWK6h5VUZXzSpcyOuogo8bt2S', 74),
('JeanPaul', 'Sartre', 'jeanpaul.sartre@bizflowerp.com', '$2a$10$Tzzf/E8PXoh24vc7ve/Wquuzo/FU90TIGuTfpFBbhCEzB9yUP74Nu', 75),
('Simone', 'DeBeauvoir', 'simone.debeauvoir@bizflowerp.com', '$2a$10$/tcBgIDrQCVr1MX5DWSF/u0uk/5KOixLgQwu45BXnm2T89NW/MeDK', 76),
('Michel', 'Foucault', 'michel.foucault@bizflowerp.com', '$2a$10$lZCO5DVN0SYXN4ZimnZnCeVJFEs6ttcSkt9JQmOHHo6KQ0FTxZFaq', 77),
('Hannah', 'Arendt', 'hannah.arendt@bizflowerp.com', '$2a$10$78megVVz558DJIU2obiWPObMY1XSu2oWU7n/o8Md24NZ4.l/ceM.q', 80);

-- ===========================================
-- FREELANCE USERS (no employee, user-only) (10)
-- ===========================================
INSERT INTO expense_user (name, surname, email, password, employee_id) VALUES
('Platon', 'deAtenas', 'platon.deatenas@bizflowerp.com', '$2a$10$9T2luP1vfypONZMz7fLhi.ekmurtzlj4Tmn89t.ESP/uoyhkvmPDG', NULL),
('Aristoteles', 'deEstagira', 'aristoteles.deestagira@bizflowerp.com', '$2a$10$uaWGDeCD3AcIn3uro2UBQ.fD7iEPXWVg5E3mJkpf5FVUBOXqfdIJi', NULL),
('Socrates', 'deAtenas', 'socrates.deatenas@bizflowerp.com', '$2a$10$7vwxPMwY2cw19WC1gWEfpef2kSzXm2p8vUmwqkoUHEosUgvNZYYEu', NULL),
('Epicuro', 'deSamos', 'epicuro.desamos@bizflowerp.com', '$2a$10$NEIPbigNAgTu8J..0jFHN.wW4/jdqNlZ7ViAG2Zdu6Cw..n3.7y6q', NULL),
('Seneca', 'deCorduba', 'seneca.decorduba@bizflowerp.com', '$2a$10$mA/2E6HSKFRTkpva8dewJev8HJ7ZOi/R5gnQpIVanHtTRMtgfW0eC', NULL),
('Marco', 'Aurelio', 'marco.aurelio@bizflowerp.com', '$2a$10$.IJid/sePEs7zTFYOA/lU.ziolnD8h0I1eyd4JcbFZlfXDDmf/3Ge', NULL),
('Emile', 'Durkheim', 'emile.durkheim@bizflowerp.com', '$2a$10$6GVFti3N0zN5lKDbRQ7p9OwPkvWGrXNMLa0dXpbsh1wfP5jXLz.VS', NULL),
('Max', 'Weber', 'max.weber@bizflowerp.com', '$2a$10$7ABX/36f28tRZhprcXV9R.cUMZA2f22Kl5PtX0o5boKSf8NMRTUI2', NULL),
('Karl', 'Marx', 'karl.marx@bizflowerp.com', '$2a$10$ikiJZuOcEo4eUBtVWQ2wI.147KUwQsJOndN0GE6.HV6qSI7wcNqVq', NULL),
('Adam', 'Smith', 'adam.smith@bizflowerp.com', '$2a$10$sOB1/8Qfkvn5nQI92oqIue8l6PtWADLpVcoE23JkXeZhQafcd/fGO', NULL);

-- ===========================================
-- ROLE ASSIGNMENTS
-- ===========================================
-- NOTE: Admins (user_id 1-2) already have roles from 05_expense_admin_bootstrap.sql

-- Additional Admins (user_id 3-4) -> role_id 1 = ADMIN
INSERT INTO user_role (user_id, role_id) VALUES (3, 1), (4, 1);

-- Managers (user_id 5-10) -> role_id 3 = MANAGER
INSERT INTO user_role (user_id, role_id) VALUES (5, 3), (6, 3), (7, 3), (8, 3), (9, 3), (10, 3);

-- Regular Users (user_id 11-60) -> role_id 2 = USER
INSERT INTO user_role (user_id, role_id) VALUES 
(11, 2), (12, 2), (13, 2), (14, 2), (15, 2), (16, 2), (17, 2), (18, 2), (19, 2), (20, 2),
(21, 2), (22, 2), (23, 2), (24, 2), (25, 2), (26, 2), (27, 2), (28, 2), (29, 2), (30, 2),
(31, 2), (32, 2), (33, 2), (34, 2), (35, 2), (36, 2), (37, 2), (38, 2), (39, 2), (40, 2),
(41, 2), (42, 2), (43, 2), (44, 2), (45, 2), (46, 2), (47, 2), (48, 2), (49, 2), (50, 2),
(51, 2), (52, 2), (53, 2), (54, 2), (55, 2), (56, 2), (57, 2), (58, 2), (59, 2), (60, 2);

-- ===========================================
-- UPDATE employee.expense_user_id for bidirectional linking
-- ===========================================
UPDATE employee SET expense_user_id = 1 WHERE email = 'ada.lovelace@bizflowerp.com';
UPDATE employee SET expense_user_id = 2 WHERE email = 'alan.turing@bizflowerp.com';
UPDATE employee SET expense_user_id = 3 WHERE email = 'grace.hopper@bizflowerp.com';
UPDATE employee SET expense_user_id = 4 WHERE email = 'john.vonneumann@bizflowerp.com';
UPDATE employee SET expense_user_id = 5 WHERE email = 'marie.curie@bizflowerp.com';
UPDATE employee SET expense_user_id = 6 WHERE email = 'albert.einstein@bizflowerp.com';
UPDATE employee SET expense_user_id = 7 WHERE email = 'larisa.latynina@bizflowerp.com';
UPDATE employee SET expense_user_id = 8 WHERE email = 'nadia.comaneci@bizflowerp.com';
UPDATE employee SET expense_user_id = 9 WHERE email = 'akira.kurosawa@bizflowerp.com';
UPDATE employee SET expense_user_id = 10 WHERE email = 'pablo.picasso@bizflowerp.com';
UPDATE employee SET expense_user_id = 11 WHERE email = 'isaac.newton@bizflowerp.com';
UPDATE employee SET expense_user_id = 12 WHERE email = 'nikola.tesla@bizflowerp.com';
UPDATE employee SET expense_user_id = 13 WHERE email = 'richard.feynman@bizflowerp.com';
UPDATE employee SET expense_user_id = 14 WHERE email = 'niels.bohr@bizflowerp.com';
UPDATE employee SET expense_user_id = 15 WHERE email = 'rosalind.franklin@bizflowerp.com';
UPDATE employee SET expense_user_id = 16 WHERE email = 'dorothy.hodgkin@bizflowerp.com';
UPDATE employee SET expense_user_id = 17 WHERE email = 'james.clerkmaxwell@bizflowerp.com';
UPDATE employee SET expense_user_id = 18 WHERE email = 'michael.faraday@bizflowerp.com';
UPDATE employee SET expense_user_id = 19 WHERE email = 'galileo.galilei@bizflowerp.com';
UPDATE employee SET expense_user_id = 20 WHERE email = 'johannes.kepler@bizflowerp.com';
UPDATE employee SET expense_user_id = 21 WHERE email = 'charles.darwin@bizflowerp.com';
UPDATE employee SET expense_user_id = 22 WHERE email = 'teofilo.stevenson@bizflowerp.com';
UPDATE employee SET expense_user_id = 23 WHERE email = 'felix.savon@bizflowerp.com';
UPDATE employee SET expense_user_id = 24 WHERE email = 'javier.sotomayor@bizflowerp.com';
UPDATE employee SET expense_user_id = 25 WHERE email = 'nikolai.andrianov@bizflowerp.com';
UPDATE employee SET expense_user_id = 26 WHERE email = 'alexander.karelin@bizflowerp.com';
UPDATE employee SET expense_user_id = 27 WHERE email = 'lev.yashin@bizflowerp.com';
UPDATE employee SET expense_user_id = 28 WHERE email = 'vitaly.scherbo@bizflowerp.com';
UPDATE employee SET expense_user_id = 29 WHERE email = 'usain.bolt@bizflowerp.com';
UPDATE employee SET expense_user_id = 30 WHERE email = 'carl.lewis@bizflowerp.com';
UPDATE employee SET expense_user_id = 31 WHERE email = 'andrei.tarkovsky@bizflowerp.com';
UPDATE employee SET expense_user_id = 32 WHERE email = 'federico.fellini@bizflowerp.com';
UPDATE employee SET expense_user_id = 33 WHERE email = 'ingmar.bergman@bizflowerp.com';
UPDATE employee SET expense_user_id = 34 WHERE email = 'stanley.kubrick@bizflowerp.com';
UPDATE employee SET expense_user_id = 35 WHERE email = 'alfred.hitchcock@bizflowerp.com';
UPDATE employee SET expense_user_id = 36 WHERE email = 'francis.fordcoppola@bizflowerp.com';
UPDATE employee SET expense_user_id = 37 WHERE email = 'vincent.vangogh@bizflowerp.com';
UPDATE employee SET expense_user_id = 38 WHERE email = 'claude.monet@bizflowerp.com';
UPDATE employee SET expense_user_id = 39 WHERE email = 'salvador.dali@bizflowerp.com';
UPDATE employee SET expense_user_id = 40 WHERE email = 'fiodor.dostoievski@bizflowerp.com';
UPDATE employee SET expense_user_id = 41 WHERE email = 'leon.tolstoi@bizflowerp.com';
UPDATE employee SET expense_user_id = 42 WHERE email = 'gabriel.garciamarquez@bizflowerp.com';
UPDATE employee SET expense_user_id = 43 WHERE email = 'immanuel.kant@bizflowerp.com';
UPDATE employee SET expense_user_id = 44 WHERE email = 'friedrich.nietzsche@bizflowerp.com';
UPDATE employee SET expense_user_id = 45 WHERE email = 'ludwig.wittgenstein@bizflowerp.com';
UPDATE employee SET expense_user_id = 46 WHERE email = 'martin.heidegger@bizflowerp.com';
UPDATE employee SET expense_user_id = 47 WHERE email = 'jeanpaul.sartre@bizflowerp.com';
UPDATE employee SET expense_user_id = 48 WHERE email = 'simone.debeauvoir@bizflowerp.com';
UPDATE employee SET expense_user_id = 49 WHERE email = 'michel.foucault@bizflowerp.com';
UPDATE employee SET expense_user_id = 50 WHERE email = 'hannah.arendt@bizflowerp.com';
