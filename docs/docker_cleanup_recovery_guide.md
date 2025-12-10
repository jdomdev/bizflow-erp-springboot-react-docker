# Docker Cleanup & Recovery Guide

This guide provides step-by-step commands for cleaning up Docker containers, volumes, orphan images, and rebuilding containers and database backups.

## 1. Stop and Remove All Containers
```bash
docker ps -aq | xargs -r docker stop
docker ps -aq | xargs -r docker rm
```

## 2. Remove All Volumes
```bash
docker volume ls -q | xargs -r docker volume rm
```

## 3. Remove Orphaned Images
```bash
docker images -f "dangling=true" -q | xargs -r docker rmi
```

## 4. Rebuild Containers and Volumes
```bash
docker-compose down -v
docker-compose up --build -d
```

## 5. Restore Database from Backup
```bash
docker cp backups/erpdb_backup_YYYY_MM_DD_HHMMSS.sql erp-db-container:/tmp/
docker exec -it erp-db-container psql -U postgres -d erp_db -f /tmp/erpdb_backup_YYYY_MM_DD_HHMMSS.sql
```

## Full Recovery Workflow
1. Stop and remove all containers
2. Remove all volumes
3. Remove orphaned images
4. Rebuild containers and volumes
5. Restore database from backup

---
Replace `YYYY_MM_DD_HHMMSS` with your backup's actual datetime stamp.