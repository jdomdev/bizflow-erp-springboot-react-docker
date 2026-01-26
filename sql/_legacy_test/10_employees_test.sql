-- =========================================================
-- Employee catalog for TEST environment
-- 20 employees: scientists, olympians, filmmakers, artists, philosophers
-- Some are linked to users, some are employee-only
-- =========================================================

INSERT INTO employee (name, surname, email, birth_date, position_id) VALUES
-- Empleados que también serán usuarios (1-15)
('Ada', 'Lovelace', 'ada.lovelace@bizflowerp.com', '1815-12-10 00:00:00', 1),
('Alan', 'Turing', 'alan.turing@bizflowerp.com', '1912-06-23 00:00:00', 2),
('Marie', 'Curie', 'marie.curie@bizflowerp.com', '1867-11-07 00:00:00', 3),
('Albert', 'Einstein', 'albert.einstein@bizflowerp.com', '1879-03-14 00:00:00', 4),
('Isaac', 'Newton', 'isaac.newton@bizflowerp.com', '1643-01-04 00:00:00', 5),
('Nikola', 'Tesla', 'nikola.tesla@bizflowerp.com', '1856-07-10 00:00:00', 6),
('Nadia', 'Comaneci', 'nadia.comaneci@bizflowerp.com', '1961-11-12 00:00:00', 7),
('Usain', 'Bolt', 'usain.bolt@bizflowerp.com', '1986-08-21 00:00:00', 8),
('Carl', 'Lewis', 'carl.lewis@bizflowerp.com', '1961-07-01 00:00:00', 9),
('Teofilo', 'Stevenson', 'teofilo.stevenson@bizflowerp.com', '1952-03-29 00:00:00', 10),
('Akira', 'Kurosawa', 'akira.kurosawa@bizflowerp.com', '1910-03-23 00:00:00', 11),
('Andrei', 'Tarkovsky', 'andrei.tarkovsky@bizflowerp.com', '1932-04-04 00:00:00', 12),
('Pablo', 'Picasso', 'pablo.picasso@bizflowerp.com', '1881-10-25 00:00:00', 13),
('Fiodor', 'Dostoievski', 'fiodor.dostoievski@bizflowerp.com', '1821-11-11 00:00:00', 14),
('Immanuel', 'Kant', 'immanuel.kant@bizflowerp.com', '1724-04-22 00:00:00', 15),
-- Empleados que NO serán usuarios (solo empleados, 16-20)
('Dorothy', 'Hodgkin', 'dorothy.hodgkin@bizflowerp.com', '1910-05-12 00:00:00', 16),
('James', 'ClerkMaxwell', 'james.clerkmaxwell@bizflowerp.com', '1831-06-13 00:00:00', 17),
('Ludwig', 'Wittgenstein', 'ludwig.wittgenstein@bizflowerp.com', '1889-04-26 00:00:00', 18),
('Antoni', 'Gaudi', 'antoni.gaudi@bizflowerp.com', '1852-06-25 00:00:00', 19),
('Auguste', 'Rodin', 'auguste.rodin@bizflowerp.com', '1840-11-12 00:00:00', 20);
