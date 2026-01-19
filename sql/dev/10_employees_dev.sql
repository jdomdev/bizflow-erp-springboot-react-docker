-- =========================================================
-- Employee catalog for DEV environment
-- 80 employees total
-- Categories: scientists, olympians, filmmakers, artists, philosophers, writers
-- =========================================================

-- ===========================================
-- SCIENTISTS (20)
-- ===========================================
INSERT INTO employee (name, surname, email, position_id, salary, hire_date, is_active) VALUES
-- Computing pioneers
('Ada', 'Lovelace', 'ada.lovelace@bizflowerp.com', 5, 9500.00, '2020-03-15', true),
('Alan', 'Turing', 'alan.turing@bizflowerp.com', 5, 9200.00, '2020-04-01', true),
('Grace', 'Hopper', 'grace.hopper@bizflowerp.com', 5, 8800.00, '2020-05-10', true),
('John', 'VonNeumann', 'john.vonneumann@bizflowerp.com', 4, 8500.00, '2020-06-15', true),
-- Physics giants
('Marie', 'Curie', 'marie.curie@bizflowerp.com', 4, 7800.00, '2021-01-10', true),
('Albert', 'Einstein', 'albert.einstein@bizflowerp.com', 4, 7500.00, '2021-02-15', true),
('Isaac', 'Newton', 'isaac.newton@bizflowerp.com', 3, 5800.00, '2021-03-20', true),
('Nikola', 'Tesla', 'nikola.tesla@bizflowerp.com', 3, 5600.00, '2021-04-25', true),
('Richard', 'Feynman', 'richard.feynman@bizflowerp.com', 3, 5400.00, '2021-05-01', true),
('Niels', 'Bohr', 'niels.bohr@bizflowerp.com', 3, 5300.00, '2021-06-10', true),
-- Chemistry and biology
('Rosalind', 'Franklin', 'rosalind.franklin@bizflowerp.com', 3, 5200.00, '2021-07-15', true),
('Dorothy', 'Hodgkin', 'dorothy.hodgkin@bizflowerp.com', 3, 5100.00, '2021-08-20', true),
('James', 'ClerkMaxwell', 'james.clerkmaxwell@bizflowerp.com', 2, 4800.00, '2022-01-10', true),
('Michael', 'Faraday', 'michael.faraday@bizflowerp.com', 2, 4700.00, '2022-02-15', true),
('Galileo', 'Galilei', 'galileo.galilei@bizflowerp.com', 2, 4600.00, '2022-03-20', true),
('Johannes', 'Kepler', 'johannes.kepler@bizflowerp.com', 2, 4500.00, '2022-04-25', true),
('Charles', 'Darwin', 'charles.darwin@bizflowerp.com', 2, 4400.00, '2022-05-01', true),
('Gregor', 'Mendel', 'gregor.mendel@bizflowerp.com', 1, 3800.00, '2023-01-10', true),
('Louis', 'Pasteur', 'louis.pasteur@bizflowerp.com', 1, 3700.00, '2023-02-15', true),
('Alexander', 'Fleming', 'alexander.fleming@bizflowerp.com', 1, 3600.00, '2023-03-20', true);

-- ===========================================
-- OLYMPIANS - Cuba, USSR, Russia, Belarus (20)
-- ===========================================
INSERT INTO employee (name, surname, email, position_id, salary, hire_date, is_active) VALUES
-- Cuban legends
('Teofilo', 'Stevenson', 'teofilo.stevenson@bizflowerp.com', 3, 3800.00, '2021-05-15', true),
('Felix', 'Savon', 'felix.savon@bizflowerp.com', 3, 3700.00, '2021-06-20', true),
('Javier', 'Sotomayor', 'javier.sotomayor@bizflowerp.com', 3, 3900.00, '2021-07-25', true),
('Ana', 'Fidelia', 'ana.fidelia@bizflowerp.com', 2, 3500.00, '2022-01-15', true),
('Mireya', 'Luis', 'mireya.luis@bizflowerp.com', 2, 3400.00, '2022-02-20', true),
('Alberto', 'Juantorena', 'alberto.juantorena@bizflowerp.com', 2, 3600.00, '2022-03-25', true),
-- USSR/Russia legends
('Larisa', 'Latynina', 'larisa.latynina@bizflowerp.com', 4, 4200.00, '2020-08-15', true),
('Nikolai', 'Andrianov', 'nikolai.andrianov@bizflowerp.com', 3, 4000.00, '2021-01-20', true),
('Alexander', 'Karelin', 'alexander.karelin@bizflowerp.com', 3, 4100.00, '2021-02-25', true),
('Lev', 'Yashin', 'lev.yashin@bizflowerp.com', 3, 3900.00, '2021-03-15', true),
('Vladislav', 'Tretiak', 'vladislav.tretiak@bizflowerp.com', 2, 3700.00, '2022-04-20', true),
('Irina', 'Rodnina', 'irina.rodnina@bizflowerp.com', 2, 3600.00, '2022-05-25', true),
('Alexei', 'Nemov', 'alexei.nemov@bizflowerp.com', 2, 3500.00, '2022-06-15', true),
('Elena', 'Isinbayeva', 'elena.isinbayeva@bizflowerp.com', 2, 3800.00, '2022-07-20', true),
-- Belarus
('Vitaly', 'Scherbo', 'vitaly.scherbo@bizflowerp.com', 3, 3900.00, '2021-08-25', true),
('Svetlana', 'Boginskaya', 'svetlana.boginskaya@bizflowerp.com', 2, 3600.00, '2022-08-15', true),
('Darya', 'Domracheva', 'darya.domracheva@bizflowerp.com', 2, 3700.00, '2022-09-20', true),
-- Other legends (Romania, USA for diversity)
('Nadia', 'Comaneci', 'nadia.comaneci@bizflowerp.com', 4, 4200.00, '2020-09-15', true),
('Usain', 'Bolt', 'usain.bolt@bizflowerp.com', 3, 4500.00, '2021-04-20', true),
('Carl', 'Lewis', 'carl.lewis@bizflowerp.com', 3, 4300.00, '2021-05-25', true);

-- ===========================================
-- FILMMAKERS (15)
-- ===========================================
INSERT INTO employee (name, surname, email, position_id, salary, hire_date, is_active) VALUES
('Akira', 'Kurosawa', 'akira.kurosawa@bizflowerp.com', 4, 6200.00, '2020-10-15', true),
('Andrei', 'Tarkovsky', 'andrei.tarkovsky@bizflowerp.com', 4, 5900.00, '2020-11-20', true),
('Federico', 'Fellini', 'federico.fellini@bizflowerp.com', 4, 5800.00, '2020-12-25', true),
('Ingmar', 'Bergman', 'ingmar.bergman@bizflowerp.com', 3, 5500.00, '2021-01-15', true),
('Stanley', 'Kubrick', 'stanley.kubrick@bizflowerp.com', 3, 5600.00, '2021-02-20', true),
('Alfred', 'Hitchcock', 'alfred.hitchcock@bizflowerp.com', 3, 5400.00, '2021-03-25', true),
('Francis', 'FordCoppola', 'francis.fordcoppola@bizflowerp.com', 3, 5300.00, '2021-04-15', true),
('Martin', 'Scorsese', 'martin.scorsese@bizflowerp.com', 2, 4800.00, '2022-01-20', true),
('Sergio', 'Leone', 'sergio.leone@bizflowerp.com', 2, 4700.00, '2022-02-25', true),
('Luis', 'Bunuel', 'luis.bunuel@bizflowerp.com', 2, 4600.00, '2022-03-15', true),
('Werner', 'Herzog', 'werner.herzog@bizflowerp.com', 2, 4500.00, '2022-04-20', true),
('Wim', 'Wenders', 'wim.wenders@bizflowerp.com', 2, 4400.00, '2022-05-25', true),
('Terrence', 'Malick', 'terrence.malick@bizflowerp.com', 1, 3900.00, '2023-01-15', true),
('David', 'Lynch', 'david.lynch@bizflowerp.com', 1, 3800.00, '2023-02-20', true),
('ParkChan', 'Wook', 'parkchan.wook@bizflowerp.com', 1, 3700.00, '2023-03-25', true);

-- ===========================================
-- ARTISTS AND WRITERS (15)
-- ===========================================
INSERT INTO employee (name, surname, email, position_id, salary, hire_date, is_active) VALUES
-- Visual artists
('Pablo', 'Picasso', 'pablo.picasso@bizflowerp.com', 4, 7200.00, '2020-06-15', true),
('Vincent', 'VanGogh', 'vincent.vangogh@bizflowerp.com', 3, 5500.00, '2021-07-20', true),
('Claude', 'Monet', 'claude.monet@bizflowerp.com', 3, 5400.00, '2021-08-25', true),
('Salvador', 'Dali', 'salvador.dali@bizflowerp.com', 3, 5300.00, '2021-09-15', true),
('Frida', 'Kahlo', 'frida.kahlo@bizflowerp.com', 2, 4800.00, '2022-06-20', true),
('Diego', 'Rivera', 'diego.rivera@bizflowerp.com', 2, 4700.00, '2022-07-25', true),
('Auguste', 'Rodin', 'auguste.rodin@bizflowerp.com', 1, 3900.00, '2023-04-15', true),
('Antoni', 'Gaudi', 'antoni.gaudi@bizflowerp.com', 2, 6000.00, '2022-08-15', true),
-- Writers
('Fiodor', 'Dostoievski', 'fiodor.dostoievski@bizflowerp.com', 3, 5500.00, '2021-10-20', true),
('Leon', 'Tolstoi', 'leon.tolstoi@bizflowerp.com', 3, 5400.00, '2021-11-25', true),
('Gabriel', 'GarciaMarquez', 'gabriel.garciamarquez@bizflowerp.com', 3, 5600.00, '2021-12-15', true),
('Jorge', 'LuisBorges', 'jorge.luisborges@bizflowerp.com', 2, 4900.00, '2022-09-20', true),
('Franz', 'Kafka', 'franz.kafka@bizflowerp.com', 2, 4800.00, '2022-10-25', true),
('Albert', 'Camus', 'albert.camus@bizflowerp.com', 2, 4700.00, '2022-11-15', true),
('Marcel', 'Proust', 'marcel.proust@bizflowerp.com', 1, 4000.00, '2023-05-20', true);

-- ===========================================
-- PHILOSOPHERS (10)
-- ===========================================
INSERT INTO employee (name, surname, email, position_id, salary, hire_date, is_active) VALUES
('Immanuel', 'Kant', 'immanuel.kant@bizflowerp.com', 4, 6800.00, '2020-07-15', true),
('Friedrich', 'Nietzsche', 'friedrich.nietzsche@bizflowerp.com', 3, 5200.00, '2021-08-20', true),
('Ludwig', 'Wittgenstein', 'ludwig.wittgenstein@bizflowerp.com', 2, 5400.00, '2022-09-25', true),
('Martin', 'Heidegger', 'martin.heidegger@bizflowerp.com', 2, 5100.00, '2022-10-15', true),
('JeanPaul', 'Sartre', 'jeanpaul.sartre@bizflowerp.com', 2, 5000.00, '2022-11-20', true),
('Simone', 'DeBeauvoir', 'simone.debeauvoir@bizflowerp.com', 2, 4900.00, '2022-12-25', true),
('Michel', 'Foucault', 'michel.foucault@bizflowerp.com', 2, 4800.00, '2023-01-15', true),
('Jacques', 'Derrida', 'jacques.derrida@bizflowerp.com', 1, 4200.00, '2023-06-20', true),
('Gilles', 'Deleuze', 'gilles.deleuze@bizflowerp.com', 1, 4100.00, '2023-07-25', true),
('Hannah', 'Arendt', 'hannah.arendt@bizflowerp.com', 1, 4300.00, '2023-08-15', true);
