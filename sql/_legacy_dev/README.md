# ⚠️ DEPRECATED - DEV Legacy SQL Seeds

**These SQL files are deprecated and should NOT be used directly.**

## Why are these legacy?

1. **Schema Mismatch**: These files use an outdated schema:
   - `employee` table: uses `salary`, `hire_date`, `is_active` columns that don't exist
   - `payroll` table: uses `salary`, `pay_date`, `start_date`, `end_date`, etc. that don't exist

2. **Bypasses Business Logic**: SQL INSERT bypasses the Java Service layer, which means:
   - Auto-linking between `Employee` and `ExpenseUser` doesn't happen
   - Validation rules in services are not applied
   - Business logic inconsistencies may occur

## What to use instead?

Use the **API Seeder** which:
- Uses the correct schema via REST API
- Triggers all Service layer logic (including auto-linking)
- Validates data properly

```bash
# After backend is healthy:
docker compose run --rm api-seeder-dev
```

## Files in this directory

- `10_employees_dev.sql` - 80 employees (wrong schema: salary, hire_date, is_active)
- `15_users_dev.sql` - 60 users (duplicates Ada/Alan)
- `20_payrolls_dev.sql` - 500 payrolls (wrong schema)
- `30_expenses_dev.sql` - 200 expenses

## Reference

- API Seeder: `scripts/seeds/seed_runner.py`
- Dev Data: `scripts/seeds/data/dev/`
