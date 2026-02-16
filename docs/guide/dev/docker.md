# Docker

Guía completa para trabajar con Docker en Bizflow ERP.

## Arquitectura de Contenedores

```
┌────────────────────────────────────────────────────────┐
│                    Docker Compose                       │
├────────────────────────────────────────────────────────┤
│                                                        │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐ │
│  │ frontend-dev │  │ backend-dev  │  │   db-dev     │ │
│  │   (nginx)    │  │  (tomcat)    │  │ (postgres)   │ │
│  │   :3000      │  │    :8080     │  │   :5433      │ │
│  └──────────────┘  └──────────────┘  └──────────────┘ │
│                                                        │
│  ┌──────────────┐                                      │
│  │   pgadmin    │                                      │
│  │    :5050     │                                      │
│  └──────────────┘                                      │
└────────────────────────────────────────────────────────┘
```

## Perfiles

| Perfil | Servicios | Uso |
|--------|-----------|-----|
| `dev` | frontend-dev, backend-dev, db-dev, pgadmin | Desarrollo |
| `test` | backend-test, db-test | Testing automatizado |
| `prod` | frontend-prod, backend-prod, db-prod | Producción |

## Comandos básicos

### Iniciar servicios

```bash
# Con Make
make dev
make test
make prod

# Con Docker Compose
docker compose --profile dev up -d
docker compose --profile test up -d
docker compose --profile prod up -d
```

### Ver estado

```bash
docker compose ps
docker compose --profile dev ps
```

### Ver logs

```bash
# Todos los servicios
docker compose logs -f

# Servicio específico
docker compose logs -f backend-dev
docker compose logs -f frontend-dev
```

### Detener servicios

```bash
make stop
# o
docker compose --profile dev down
```

### Reconstruir imágenes

```bash
# Reconstruir un servicio
docker compose --profile dev up -d --build backend-dev

# Reconstruir todo
docker compose --profile dev build --no-cache
docker compose --profile dev up -d
```

## Volúmenes

| Volumen | Propósito |
|---------|-----------|
| `postgres_dev_data` | Datos de BD desarrollo |
| `postgres_test_data` | Datos de BD testing |
| `postgres_prod_data` | Datos de BD producción |

### Limpiar volúmenes

```bash
# Listar volúmenes
docker volume ls

# Eliminar volumen específico
docker volume rm bizflow_postgres_dev_data

# Limpiar todo (¡cuidado!)
docker compose down -v
```

## Base de datos

### Acceder a PostgreSQL

```bash
# Via psql en contenedor
docker compose exec db-dev psql -U postgres -d erp_dev_db

# Comandos útiles
\dt          # Listar tablas
\d expense   # Describir tabla
\q           # Salir
```

### Backup y restore

```bash
# Backup
make db-backup ENV=dev

# Restore
make db-restore ENV=dev FILE=backup.dump
```

## Solución de Problemas

### Puerto en uso

```bash
# Verificar puertos
lsof -i :8080
lsof -i :3000
lsof -i :5433

# Matar proceso
kill -9 <PID>
```

### Contenedor no inicia

```bash
# Ver logs
docker compose logs backend-dev

# Reiniciar servicio
docker compose restart backend-dev
```

### Errores de permisos

```bash
# Arreglar permisos de volumen
sudo chown -R $USER:$USER ./backend/target
sudo chown -R $USER:$USER ./frontend/node_modules
```

### Limpiar todo y empezar de cero

```bash
# Detener todo
make stop

# Eliminar contenedores, redes, volúmenes e imágenes
docker compose down -v --rmi all

# Limpiar caché de Docker
docker system prune -af

# Reiniciar
make dev
```
