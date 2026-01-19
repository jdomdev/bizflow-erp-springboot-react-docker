-- =========================================================
-- Expense records for TEST environment
-- ~40 expenses distributed among users
-- =========================================================

-- ===========================================
-- EXPENSES Q1 2024
-- ===========================================

-- JANUARY 2024
INSERT INTO expense (user_id, category, description, amount, expense_date, status) VALUES
(1, 'TRAVEL', 'Conference travel to London', 850.00, '2024-01-10', 'APPROVED'),
(2, 'EQUIPMENT', 'Ergonomic keyboard and mouse', 245.00, '2024-01-12', 'APPROVED'),
(3, 'EDUCATION', 'Physics seminar registration', 320.00, '2024-01-15', 'APPROVED'),
(4, 'OFFICE_SUPPLIES', 'Whiteboard and markers for lab', 89.50, '2024-01-18', 'APPROVED'),
(5, 'TRAVEL', 'Train tickets to Cambridge', 156.00, '2024-01-20', 'APPROVED'),
(6, 'EQUIPMENT', 'Oscilloscope accessories', 430.00, '2024-01-22', 'PENDING'),
(7, 'TRAVEL', 'Competition travel expenses', 520.00, '2024-01-25', 'APPROVED'),
(8, 'MEALS', 'Team dinner after training', 185.00, '2024-01-28', 'APPROVED');

-- FEBRUARY 2024
INSERT INTO expense (user_id, category, description, amount, expense_date, status) VALUES
(9, 'TRAVEL', 'Athletics meet transportation', 380.00, '2024-02-02', 'APPROVED'),
(10, 'EQUIPMENT', 'Boxing training gear', 275.00, '2024-02-05', 'APPROVED'),
(11, 'EQUIPMENT', 'Film editing software license', 599.00, '2024-02-08', 'APPROVED'),
(12, 'TRAVEL', 'Film festival attendance', 720.00, '2024-02-12', 'PENDING'),
(13, 'OFFICE_SUPPLIES', 'Art supplies for studio', 340.00, '2024-02-15', 'APPROVED'),
(14, 'EDUCATION', 'Literature workshop fee', 180.00, '2024-02-18', 'APPROVED'),
(15, 'TRAVEL', 'Philosophy conference Berlin', 650.00, '2024-02-22', 'APPROVED'),
(1, 'EQUIPMENT', 'Monitor stand and laptop dock', 289.00, '2024-02-25', 'APPROVED');

-- MARCH 2024
INSERT INTO expense (user_id, category, description, amount, expense_date, status) VALUES
(2, 'EDUCATION', 'AI and Machine Learning course', 450.00, '2024-03-01', 'APPROVED'),
(3, 'TRAVEL', 'Research collaboration visit', 890.00, '2024-03-05', 'APPROVED'),
(4, 'OFFICE_SUPPLIES', 'Laboratory notebooks and pens', 65.00, '2024-03-08', 'APPROVED'),
(5, 'MEALS', 'Client meeting lunch', 95.00, '2024-03-10', 'APPROVED'),
(6, 'EQUIPMENT', 'Voltage regulator equipment', 315.00, '2024-03-12', 'APPROVED'),
(7, 'TRAVEL', 'Olympic qualifier trip', 1200.00, '2024-03-15', 'APPROVED'),
(8, 'EDUCATION', 'Sports psychology seminar', 220.00, '2024-03-18', 'PENDING'),
(16, 'EDUCATION', 'Philosophy conference registration', 280.00, '2024-03-20', 'APPROVED');

-- ===========================================
-- EXPENSES Q2 2024 (April only for TEST)
-- ===========================================

INSERT INTO expense (user_id, category, description, amount, expense_date, status) VALUES
(17, 'EDUCATION', 'Sociology research workshop', 195.00, '2024-04-02', 'APPROVED'),
(18, 'TRAVEL', 'Academic conference Paris', 780.00, '2024-04-05', 'PENDING'),
(19, 'OFFICE_SUPPLIES', 'Books and journals subscription', 145.00, '2024-04-08', 'APPROVED'),
(20, 'TRAVEL', 'Political science symposium', 420.00, '2024-04-12', 'APPROVED'),
(1, 'MEALS', 'Team building dinner', 350.00, '2024-04-15', 'APPROVED'),
(2, 'TRAVEL', 'Client site visit Manchester', 290.00, '2024-04-18', 'APPROVED'),
(3, 'EQUIPMENT', 'Laboratory measurement tools', 520.00, '2024-04-22', 'APPROVED'),
(4, 'EDUCATION', 'Quantum physics online course', 380.00, '2024-04-25', 'APPROVED'),
(5, 'OFFICE_SUPPLIES', 'Printer cartridges and paper', 78.50, '2024-04-28', 'APPROVED'),
(9, 'MEALS', 'Sponsor meeting dinner', 165.00, '2024-04-30', 'REJECTED'),
(10, 'EQUIPMENT', 'Training resistance bands', 85.00, '2024-04-30', 'APPROVED'),
(11, 'TRAVEL', 'Cannes film festival preview', 950.00, '2024-04-30', 'PENDING');
