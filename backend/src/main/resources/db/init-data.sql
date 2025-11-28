-- Initial Data Insertion Script for Expense Note App
-- This script creates default roles for first-time setup
-- Execution: Happens automatically on application startup if using Spring JDBC initialization
-- Security Note: No admin user credentials are stored here - use the DataLoader or signup endpoint

-- ============================================================================
-- REQUIRED: Insert default roles (must exist for application to function)
-- ============================================================================
INSERT INTO role (name, description) 
VALUES ('ADMIN', 'Administrator with full permissions') 
ON CONFLICT DO NOTHING;

INSERT INTO role (name, description) 
VALUES ('USER', 'Regular user with standard permissions') 
ON CONFLICT DO NOTHING;

INSERT INTO role (name, description) 
VALUES ('MANAGER', 'Manager with team oversight permissions') 
ON CONFLICT DO NOTHING;

-- ============================================================================
-- OPTIONAL: Sample positions for testing
-- ============================================================================
INSERT INTO position (name, description)
VALUES ('Software Developer', 'Full-stack software development role')
ON CONFLICT DO NOTHING;

INSERT INTO position (name, description)
VALUES ('Project Manager', 'Project management and coordination')
ON CONFLICT DO NOTHING;

INSERT INTO position (name, description)
VALUES ('Designer', 'UI/UX design and prototyping')
ON CONFLICT DO NOTHING;

-- ============================================================================
-- FIRST-TIME ADMIN USER SETUP
-- ============================================================================
-- Option 1: Use the DataLoader bean (automatic on startup)
--          - Creates admin user with secure generated password
--          - Check application logs for temporary credentials
--
-- Option 2: Use the Signup Endpoint (no authentication required)
--          - POST /api/auth/signup with username, password, email
--          - Then assign ADMIN role via database or API
--
-- Option 3: Manual SQL insertion (development only)
--          - Uncomment the INSERT statements below
--          - CHANGE THE PASSWORD IMMEDIATELY IN PRODUCTION
--
-- For development/testing only (NEVER use in production with these credentials):
-- INSERT INTO "user" (username, password, email, enabled) 
-- VALUES ('admin', '${HASHED_PASSWORD}', 'admin@localhost', true)
-- ON CONFLICT DO NOTHING;
-- 
-- INSERT INTO user_role (user_id, role_id)
-- SELECT u.id, r.id FROM "user" u, role r
-- WHERE u.username = 'admin' AND r.name = 'ADMIN'
-- ON CONFLICT DO NOTHING;
