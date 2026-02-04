#!/usr/bin/env python3
"""
Generate SQL bootstrap files with BCrypt hashes from secrets.

This script reads user data from scripts/secrets/users_with_passwords/
and GENERATES the SQL bootstrap files with correct BCrypt hashes.

The SQL files are OUTPUT, not source - they should be regenerated
whenever passwords change in secrets.

Usage:
    python3 scripts/utils/generate_password_hashes.py --generate
    python3 scripts/utils/generate_password_hashes.py --verify
    python3 scripts/utils/generate_password_hashes.py --dry-run

Options:
    --generate    Generate SQL bootstrap files from secrets (overwrites existing)
    --verify      Verify current SQL hashes match the secrets passwords
    --dry-run     Show what would be generated without writing files

Security:
    - Passwords only exist in gitignored secrets/ directory
    - Generated SQL contains only BCrypt hashes (safe to commit)
    - Single source of truth: secrets files
"""

import argparse
import json
import re
import sys
from datetime import datetime
from pathlib import Path

try:
    import bcrypt
except ImportError:
    print("Error: bcrypt not installed. Run: pip install bcrypt")
    sys.exit(1)

# Paths
SCRIPT_DIR = Path(__file__).parent
PROJECT_ROOT = SCRIPT_DIR.parent.parent
SECRETS_DIR = PROJECT_ROOT / "scripts" / "secrets" / "users_with_passwords"
SQL_DIR = PROJECT_ROOT / "sql" / "common"

# Configuration: Which users to bootstrap in SQL (by email)
# These users are created directly in SQL so admins can login before API seeding
BOOTSTRAP_USERS_EMAILS = [
    "ada.lovelace@bizflowerp.com",
    "alan.turing@bizflowerp.com",
]

# SQL files configuration
SQL_CONFIG = {
    "dev_test": {
        "secrets_file": SECRETS_DIR / "dev_users.json",
        "sql_file": SQL_DIR / "05_admin_bootstrap_dev_test.sql",
        "description": "DEV/TEST environments",
    },
    "prod": {
        "secrets_file": SECRETS_DIR / "prod_users.json",
        "sql_file": SQL_DIR / "05_admin_bootstrap_prod.sql",
        "description": "PROD environment",
    },
}


def load_secrets(secrets_file: Path) -> list[dict]:
    """Load all users from secrets file."""
    if not secrets_file.exists():
        print(f"Error: Secrets file not found: {secrets_file}")
        return []
    
    with open(secrets_file, 'r') as f:
        return json.load(f)


def get_bootstrap_users(all_users: list[dict]) -> list[dict]:
    """Filter users to only bootstrap users in BOOTSTRAP_USERS_EMAILS order.
    
    Important: Returns users in the explicit order of BOOTSTRAP_USERS_EMAILS,
    not the order from the JSON file. This ensures stable ID assignment
    when using enumerate(..., start=1).
    """
    # Build a mapping from email to user for quick lookup
    email_to_user = {u.get("email"): u for u in all_users}
    
    # Return users in BOOTSTRAP_USERS_EMAILS order (not JSON order)
    return [email_to_user[email] for email in BOOTSTRAP_USERS_EMAILS 
            if email in email_to_user]


def generate_hash(password: str) -> str:
    """Generate BCrypt hash for password.
    
    Note: Python bcrypt generates $2b$ prefix, but Spring Boot's
    BCryptPasswordEncoder expects $2a$. They are functionally equivalent,
    so we convert the prefix for compatibility.
    """
    hash_bytes = bcrypt.hashpw(password.encode(), bcrypt.gensalt(10))
    hash_str = hash_bytes.decode()
    # Convert $2b$ to $2a$ for Spring Boot compatibility
    if hash_str.startswith("$2b$"):
        hash_str = "$2a$" + hash_str[4:]
    return hash_str


def verify_hash(password: str, hash_str: str) -> bool:
    """Verify password matches hash."""
    try:
        return bcrypt.checkpw(password.encode(), hash_str.encode())
    except Exception:
        return False


def generate_sql_content(users: list[dict], description: str) -> str:
    """Generate complete SQL file content for bootstrap users."""
    timestamp = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    
    lines = [
        "-- ====================================================================",
        f"-- Bootstrap ADMIN users for {description}",
        "-- ====================================================================",
        "-- AUTO-GENERATED FILE - DO NOT EDIT MANUALLY",
        f"-- Generated: {timestamp}",
        "-- Source: scripts/secrets/users_with_passwords/",
        "-- Regenerate: python3 scripts/utils/generate_password_hashes.py --generate",
        "-- ====================================================================",
        "",
    ]
    
    # Generate INSERT for each user
    for idx, user in enumerate(users, start=1):
        email = user["email"]
        name = user["name"]
        surname = user["surname"]
        password = user["password"]
        password_hash = generate_hash(password)
        
        lines.extend([
            f"INSERT INTO expense_user (id, email, name, surname, password, employee_id)",
            f"VALUES (",
            f"    {idx},",
            f"    '{email}',",
            f"    '{name}',",
            f"    '{surname}',",
            f"    '{password_hash}',",
            f"    NULL",
            f")",
            f"ON CONFLICT (id) DO NOTHING;",
            "",
        ])
    
    # Generate role assignments (ADMIN=1, USER=2 for all bootstrap users)
    lines.append("-- Assign ADMIN and USER roles")
    for idx in range(1, len(users) + 1):
        lines.append(f"INSERT INTO user_role (user_id, role_id) VALUES ({idx}, 1) ON CONFLICT DO NOTHING;")
        lines.append(f"INSERT INTO user_role (user_id, role_id) VALUES ({idx}, 2) ON CONFLICT DO NOTHING;")
    
    # Reset sequence
    max_id = len(users)
    lines.extend([
        "",
        "-- Reset sequence",
        f"SELECT setval('expense_user_id_seq', GREATEST({max_id}, COALESCE((SELECT MAX(id) FROM expense_user), 0)), true);",
        "",
    ])
    
    return "\n".join(lines)


def extract_hashes_from_sql(sql_file: Path) -> dict[str, str]:
    """Extract email->hash mapping from existing SQL file."""
    if not sql_file.exists():
        return {}
    
    content = sql_file.read_text()
    hashes = {}
    
    # Pattern: 'email', ... 'hash' pattern in INSERT statements
    # Match the email and the BCrypt hash
    pattern = r"'([^']+@bizflowerp\.com)'[^$]*'(\$2[aby]\$\d+\$[^']+)'"
    
    for match in re.finditer(pattern, content):
        email = match.group(1)
        hash_val = match.group(2)
        hashes[email] = hash_val
    
    return hashes


def verify_sql_hashes(config: dict, env_name: str) -> bool:
    """Verify that SQL file hashes match secrets passwords."""
    secrets_file = config["secrets_file"]
    sql_file = config["sql_file"]
    description = config["description"]
    
    print(f"\n{'='*60}")
    print(f"Verifying: {description}")
    print(f"SQL file: {sql_file.name}")
    print(f"{'='*60}")
    
    all_users = load_secrets(secrets_file)
    if not all_users:
        print(f"  ⚠️  No users loaded from {secrets_file.name}")
        return False
    
    bootstrap_users = get_bootstrap_users(all_users)
    if not bootstrap_users:
        print(f"  ⚠️  No bootstrap users found")
        return False
    
    current_hashes = extract_hashes_from_sql(sql_file)
    if not current_hashes:
        print(f"  ⚠️  No hashes found in SQL file (file may not exist)")
        return False
    
    all_valid = True
    for user in bootstrap_users:
        email = user["email"]
        password = user["password"]
        current_hash = current_hashes.get(email)
        
        if not current_hash:
            print(f"  ❌ {email}: Not found in SQL file")
            all_valid = False
            continue
        
        if verify_hash(password, current_hash):
            print(f"  ✅ {email}: Hash matches password")
        else:
            print(f"  ❌ {email}: Hash does NOT match password")
            all_valid = False
    
    return all_valid


def generate_sql_file(config: dict, env_name: str, dry_run: bool = False) -> bool:
    """Generate SQL file from secrets."""
    secrets_file = config["secrets_file"]
    sql_file = config["sql_file"]
    description = config["description"]
    
    print(f"\n{'='*60}")
    print(f"Generating: {description}")
    print(f"Source: {secrets_file.name}")
    print(f"Output: {sql_file.name}")
    print(f"{'='*60}")
    
    all_users = load_secrets(secrets_file)
    if not all_users:
        print(f"  ❌ Failed to load users from {secrets_file.name}")
        return False
    
    bootstrap_users = get_bootstrap_users(all_users)
    if not bootstrap_users:
        print(f"  ❌ No bootstrap users found matching: {BOOTSTRAP_USERS_EMAILS}")
        return False
    
    # Validate all required bootstrap emails are present
    required_emails = set(BOOTSTRAP_USERS_EMAILS)
    found_emails = {user["email"] for user in bootstrap_users}
    missing_emails = sorted(required_emails - found_emails)
    if missing_emails:
        print(f"  ❌ Missing required bootstrap user emails in {secrets_file.name}:")
        for email in missing_emails:
            print(f"     - {email}")
        print("  Aborting generation to avoid incomplete bootstrap SQL.")
        return False
    
    print(f"  📋 Found {len(bootstrap_users)} bootstrap users:")
    for user in bootstrap_users:
        print(f"     - {user['email']}")
    
    sql_content = generate_sql_content(bootstrap_users, description)
    
    if dry_run:
        print(f"\n  [DRY-RUN] Would write to {sql_file.name}:")
        print("-" * 40)
        for line in sql_content.split("\n")[:25]:
            print(f"  {line}")
        print("  ...")
        return True
    
    # Ensure directory exists
    sql_file.parent.mkdir(parents=True, exist_ok=True)
    
    # Write SQL file
    sql_file.write_text(sql_content)
    print(f"  ✅ Generated {sql_file.name}")
    
    return True


def main():
    parser = argparse.ArgumentParser(
        description="Generate SQL bootstrap files from secrets",
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog="""
Examples:
  %(prog)s --generate     Generate SQL files from secrets
  %(prog)s --verify       Check if current SQL matches secrets
  %(prog)s --dry-run      Preview what would be generated
        """
    )
    parser.add_argument("--generate", action="store_true", 
                       help="Generate SQL bootstrap files from secrets")
    parser.add_argument("--verify", action="store_true", 
                       help="Verify current SQL hashes match secrets")
    parser.add_argument("--dry-run", action="store_true", 
                       help="Show what would be generated without writing")
    args = parser.parse_args()
    
    if not any([args.generate, args.verify, args.dry_run]):
        parser.print_help()
        print("\n⚠️  No action specified. Use --generate, --verify, or --dry-run")
        sys.exit(1)
    
    if not SECRETS_DIR.exists():
        print(f"Error: Secrets directory not found: {SECRETS_DIR}")
        print("Create password files first. See scripts/seeds/README.md")
        sys.exit(1)
    
    success = True
    
    if args.verify:
        for env_name, config in SQL_CONFIG.items():
            if not verify_sql_hashes(config, env_name):
                success = False
        
        print(f"\n{'='*60}")
        if success:
            print("✅ All SQL hashes match secrets passwords")
        else:
            print("❌ Some hashes don't match. Run --generate to fix.")
        sys.exit(0 if success else 1)
    
    if args.generate or args.dry_run:
        for env_name, config in SQL_CONFIG.items():
            if not generate_sql_file(config, env_name, dry_run=args.dry_run):
                success = False
        
        print(f"\n{'='*60}")
        if args.dry_run:
            print("🔍 Dry-run complete. No files modified.")
        elif success:
            print("✅ SQL bootstrap files generated successfully")
            print("\nNext steps:")
            print("  1. Review generated files in sql/common/")
            print("  2. Commit changes: git add sql/common/05_admin_bootstrap_*.sql")
            print("  3. Recreate DB: make down-dev && make up-dev")
        else:
            print("❌ Some files failed to generate")
        
        sys.exit(0 if success else 1)


if __name__ == "__main__":
    main()
