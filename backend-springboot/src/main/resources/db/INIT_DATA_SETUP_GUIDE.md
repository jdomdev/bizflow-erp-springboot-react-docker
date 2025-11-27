# 🔐 Initial Data Setup Guide

**Application:** Expense Note App  
**Security Level:** High (No hardcoded credentials)  
**Last Updated:** 27 November 2025

---

## Overview

This guide explains how to set up initial data in the Expense Note App database, with emphasis on security best practices. **No admin user credentials are hardcoded** in code or SQL files.

---

## Automatic Initialization on Startup

When the application starts, the `DataLoader` component automatically:

✅ **Creates 3 default roles:**
- `ADMIN` - Full application permissions
- `USER` - Standard user permissions  
- `MANAGER` - Team management permissions

❌ **Does NOT create** any user accounts (for security reasons)

### Logs Output

```
========== Starting DataLoader ==========
No roles found. Creating default roles...
✓ ADMIN role created
✓ USER role created
✓ MANAGER role created
========== DataLoader Completed Successfully ==========

IMPORTANT SETUP INSTRUCTIONS
No users found in database. To create first admin user:
1. Use POST /api/auth/signup endpoint (no auth required)
2. Then assign ADMIN role via database
3. Or manually insert user with secure password
⚠️  NEVER hardcode admin credentials in code or files
```

---

## Creating First Admin User

Choose ONE of the following approaches:

### Option 1: Signup Endpoint (Recommended for Development)

**Endpoint:** `POST /api/auth/signup`

```bash
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "YourSecurePassword123!",
    "email": "admin@yourcompany.com"
  }'
```

**Response:**
```json
{
  "id": 1,
  "username": "admin",
  "email": "admin@yourcompany.com",
  "roles": ["USER"]
}
```

**Then assign ADMIN role via database:**

```sql
-- Get the user ID
SELECT id FROM "user" WHERE username = 'admin';
-- Result: id = 1

-- Get the ADMIN role ID
SELECT id FROM role WHERE name = 'ADMIN';
-- Result: id = 1

-- Assign ADMIN role to user
INSERT INTO user_role (user_id, role_id) VALUES (1, 1);
```

---

### Option 2: Database Direct Insert (Development Only)

**Create admin user with encrypted password:**

```bash
# Generate BCrypt hash for your password
# Using Java:
# new BCryptPasswordEncoder().encode("your_password_here")

# Or use online tool: https://bcrypt-generator.com/
# Example output: $2a$10$abcdefghijklmnopqrstuvwxyz...
```

**SQL Insert:**

```sql
-- Insert user with encrypted password
INSERT INTO "user" (username, password, email, enabled) 
VALUES (
  'admin',
  '$2a$10$abcdefghijklmnopqrstuvwxyz...',  -- Replace with your hash
  'admin@yourcompany.com',
  true
);

-- Get IDs
SELECT id FROM "user" WHERE username = 'admin';     -- e.g., id = 1
SELECT id FROM role WHERE name = 'ADMIN';            -- e.g., id = 1

-- Assign ADMIN role
INSERT INTO user_role (user_id, role_id) VALUES (1, 1);
```

---

### Option 3: Docker PostgreSQL Direct Access

```bash
# Connect to PostgreSQL container
docker-compose exec postgres psql -U postgres -d expense_db

# Inside psql prompt:
-- View current users
SELECT * FROM "user";

-- View roles
SELECT * FROM role;

-- Insert admin (see Option 2 for password hash)
INSERT INTO "user" (username, password, email, enabled) 
VALUES ('admin', '$2a$10$...', 'admin@company.com', true);

-- Assign ADMIN role
INSERT INTO user_role (user_id, role_id) 
SELECT u.id, r.id FROM "user" u, role r 
WHERE u.username = 'admin' AND r.name = 'ADMIN';

-- Verify
SELECT u.username, r.name FROM "user" u 
JOIN user_role ur ON u.id = ur.user_id 
JOIN role r ON ur.role_id = r.id;
```

---

## Creating Additional Users

### For End Users

Users can create their own accounts via signup endpoint:

```bash
POST /api/auth/signup
Content-Type: application/json

{
  "username": "john.doe",
  "password": "SecurePassword456!",
  "email": "john.doe@company.com"
}
```

Default role: `USER`

### For Managers/Admins

Only ADMIN users can create other managers or admins.

**Endpoint:** `POST /api/users` (requires ADMIN role)

```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer ${JWT_TOKEN}" \
  -d '{
    "username": "manager.jane",
    "password": "SecurePassword789!",
    "email": "jane@company.com",
    "roleNames": ["MANAGER"]
  }'
```

---

## Security Best Practices

### ✅ DO
- ✅ Use environment variables for passwords
- ✅ Hash passwords with BCrypt (min 10 rounds)
- ✅ Rotate admin passwords regularly
- ✅ Use HTTPS in production
- ✅ Log all user creation events
- ✅ Require strong passwords (8+ chars, mix case, numbers)

### ❌ DON'T
- ❌ Hardcode passwords in code
- ❌ Store passwords in SQL files
- ❌ Use default passwords in production
- ❌ Share admin credentials via email/chat
- ❌ Commit credentials to git
- ❌ Use same password for multiple accounts

---

## Production Setup

### Environment Variables

```bash
# Set secure admin credentials
export ADMIN_USERNAME="your_admin_user"
export ADMIN_PASSWORD="your_secure_password"
export ADMIN_EMAIL="admin@yourcompany.com"

# Application will use these during initialization
java -jar app.jar
```

### Password Requirements

```
Minimum Length:     12 characters
Must Include:       Uppercase letter (A-Z)
Must Include:       Lowercase letter (a-z)
Must Include:       Number (0-9)
Must Include:       Special character (!@#$%^&*)

Example:  MySecure@Pass2024!
```

### Database Backup Before Changes

```bash
# Backup current database
docker-compose exec postgres pg_dump -U postgres expense_db > backup.sql

# Restore if needed
docker-compose exec postgres psql -U postgres expense_db < backup.sql
```

---

## Troubleshooting

### Issue: "ADMIN role not found"

**Cause:** DataLoader didn't run or failed  
**Solution:**
```bash
# Check logs
docker-compose logs backend

# Manually create roles
docker-compose exec postgres psql -U postgres -d expense_db
INSERT INTO role (name, description) VALUES ('ADMIN', 'Admin role');
```

### Issue: "Cannot login with admin credentials"

**Cause:** User not created or password mismatch  
**Solution:**
```sql
-- Verify user exists
SELECT * FROM "user" WHERE username = 'admin';

-- Verify role is assigned
SELECT u.username, r.name FROM "user" u
JOIN user_role ur ON u.id = ur.user_id
JOIN role r ON ur.role_id = r.id
WHERE u.username = 'admin';

-- Verify user is enabled
SELECT * FROM "user" WHERE username = 'admin' AND enabled = true;
```

### Issue: "Foreign key constraint violation"

**Cause:** Role doesn't exist when assigning to user  
**Solution:**
```sql
-- Create missing role
INSERT INTO role (name, description) VALUES ('ADMIN', 'Administrator');

-- Retry user_role insert
INSERT INTO user_role (user_id, role_id)
SELECT u.id, r.id FROM "user" u, role r
WHERE u.username = 'admin' AND r.name = 'ADMIN';
```

---

## Database Schema

```sql
-- Roles table
CREATE TABLE role (
  id SERIAL PRIMARY KEY,
  name VARCHAR(50) UNIQUE NOT NULL,
  description TEXT
);

-- Users table
CREATE TABLE "user" (
  id SERIAL PRIMARY KEY,
  username VARCHAR(50) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL,
  email VARCHAR(100),
  enabled BOOLEAN DEFAULT true,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- User roles junction table
CREATE TABLE user_role (
  user_id INTEGER REFERENCES "user"(id),
  role_id INTEGER REFERENCES role(id),
  PRIMARY KEY (user_id, role_id)
);
```

---

## Quick Reference Commands

```bash
# Check roles exist
docker-compose exec postgres psql -U postgres -d expense_db \
  -c "SELECT * FROM role;"

# Check users exist
docker-compose exec postgres psql -U postgres -d expense_db \
  -c "SELECT username, email, enabled FROM \"user\";"

# Check user roles
docker-compose exec postgres psql -U postgres -d expense_db \
  -c "SELECT u.username, r.name FROM \"user\" u 
       JOIN user_role ur ON u.id = ur.user_id 
       JOIN role r ON ur.role_id = r.id;"

# Restart backend (re-runs DataLoader)
docker-compose restart backend

# View application logs
docker-compose logs -f backend
```

---

## Support

For issues or questions about initial setup:

1. Check application logs: `docker-compose logs backend`
2. Verify database state: `docker-compose exec postgres psql`
3. Consult EXCEPTION_HANDLING_PLAN.md for error handling
4. Review SIGNUP_ISSUE_ANALYSIS.md for signup details

---

**Document:** Initial Data Setup Guide  
**Security Level:** 🔐 High  
**Last Updated:** 2025-11-27  
**Status:** Production Ready
