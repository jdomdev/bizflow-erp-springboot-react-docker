# ⚠️ DEPRECATED - TEST Legacy SQL Seeds

**These SQL files are deprecated and should NOT be used directly.**

## Why are these legacy?

1. **Schema Mismatch**: These files use an outdated schema (e.g., `payroll.salary`, `payroll.pay_date`) that no longer matches the current database schema

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
docker compose run --rm api-seeder-test
```

## Files in this directory

- `10_employees_test.sql` - 20 employees (wrong schema)
- `15_users_test.sql` - 20 users (duplicates Ada/Alan)
- `20_payrolls_test.sql` - 80 payrolls (wrong schema: salary, pay_date columns)
- `30_expenses_test.sql` - 40 expenses

## Reference

- API Seeder: `scripts/seeds/seed_runner.py`
- Test Data: `scripts/seeds/data/test/`
