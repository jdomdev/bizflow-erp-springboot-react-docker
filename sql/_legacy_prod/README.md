# Legacy SQL Seeds

> ⚠️ **DEPRECATED**: These SQL seed files are legacy/fallback only.
> 
> The recommended approach is **Seeds via API** using `scripts/seeds/`.

## Why Legacy?

SQL INSERT statements bypass Java Service layer logic, meaning:
- No automatic employee ↔ user linking
- No business validations
- No audit trails

## Preferred Alternative

Use the API-based seeder in `scripts/seeds/`:

```bash
# Run seeder manually
cd scripts/seeds
python seed_runner.py --env dev --api-url http://localhost:8082 --wait

# Or via Docker Compose
docker compose --profile seed-dev up api-seeder-dev
```

## When to Use Legacy SQL

Only use these files when:
1. API seeder is not available
2. Debugging database initialization
3. Quick rollback/restore scenarios

## Files in this Directory

- `10_employees_*.sql` - Employee seed data
- `15_users_*.sql` - User seed data  
- `20_payrolls_*.sql` - Payroll seed data
- `30_expenses_*.sql` - Expense seed data
