-- =========================================================
-- Master bootstrap for PROD environment
-- Schema and reference data ONLY - seed data via API seeder
-- =========================================================

-- Common schema and reference data
\ir ../common/01_schema.sql
\ir ../common/02_positions.sql
\ir ../common/03_roles.sql

-- Bootstrap admin users with PROD-strength passwords
\ir ../common/05_admin_bootstrap_prod.sql

-- =========================================================
-- NOTE: Seed data (employees, users, payrolls, expenses) is now
-- loaded via API seeder instead of SQL files.
-- 
-- Run: docker compose run --rm api-seeder-prod
-- 
-- PROD passwords use enhanced format: See scripts/secrets/
-- =========================================================
