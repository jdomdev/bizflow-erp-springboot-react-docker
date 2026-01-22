# Legacy SQL Seeds (Dev/Prod Shared)

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

## Known Issues

- `10_employees_full.sql` uses columns that don't exist in current schema
- Files may have incorrect employee_id references

## When to Use Legacy SQL

Only use these files when:
1. API seeder is not available
2. Debugging database initialization
3. Quick rollback/restore scenarios
