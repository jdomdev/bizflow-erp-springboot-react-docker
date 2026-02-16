# Gestión de bases de datos PostgreSQL con Docker

Esta guía resume los comandos que permiten administrar las bases de datos del proyecto directamente desde la terminal usando Docker, sin entrar a `psql`. Los ejemplos asumen que estamos en la raíz del repositorio y que los servicios Docker están levantados.

## Verificar el estado de los contenedores de base de datos
- `docker ps --filter "name=erp-dev-db-container"` — Comprueba si el contenedor dev está en ejecución.
- `docker compose --profile dev ps` — Muestra el estado de todos los servicios del perfil dev, incluida la base de datos.
- `docker inspect -f '{{ .State.Health.Status }}' erp-dev-db-container` — Obtiene el estado de salud reportado por el contenedor.

## Comprobar conectividad sin `psql`
- `docker exec erp-dev-db-container pg_isready -h localhost -p 5432 -U erp_dev_user` — Verifica la disponibilidad del servicio PostgreSQL desde dentro del contenedor.
- `docker exec erp-dev-db-container pg_isready -h erp-dev-db-container -p 5432 -U erp_dev_user` — Variante para comprobar resolución de nombres dentro de la red Docker.

## Crear y restaurar backups
- `docker exec erp-prod-db-container pg_dump -U erp_prod_user -d erp_prod_db -F c -Z 6 -f /tmp/prod_backup.dump` — Genera un dump comprimido dentro del contenedor.
- `docker cp erp-prod-db-container:/tmp/prod_backup.dump backups/prod/erp_prod_db_backup_$(date +%Y%m%d_%H%M%S).dump` — Copia el dump al host y conserva el timestamp.
- `docker exec erp-dev-db-container pg_restore -U erp_dev_user -d erp_dev_db /tmp/prod_backup.dump` — Restaura un dump en la base dev (requerido: copiar el archivo antes con `docker cp`).
- `docker exec erp-dev-db-container pg_restore -l /tmp/prod_backup.dump` — Lista el contenido de un dump sin restaurarlo.

## Importar/exportar datos con archivos SQL
- `docker cp sql/04_employees_sample.sql erp-dev-db-container:/tmp/` — Copia un script SQL desde el host al contenedor dev.
- `docker exec erp-dev-db-container sh -c "psql -U erp_dev_user -d erp_dev_db -f /tmp/04_employees_sample.sql"` — Ejecuta el script dentro del contenedor.
- `docker exec erp-dev-db-container pg_dump -U erp_dev_user -d erp_dev_db -f /tmp/dev_snapshot.sql` — Genera un volcado plano en formato SQL.
- `docker cp erp-dev-db-container:/tmp/dev_snapshot.sql backups/dev/` — Recupera el archivo SQL al host.

## Sincronizar bases entre entornos
- `docker exec erp-prod-db-container pg_dump -U erp_prod_user -d erp_prod_db | docker exec -i erp-dev-db-container psql -U erp_dev_user -d erp_dev_db` — Replica la base de producción sobre la base de desarrollo en un único paso (sobrescribe datos existentes).
- `docker exec erp-prod-db-container pg_dump -U erp_prod_user -d erp_prod_db | docker exec -i erp-test-db-container psql -U erp_test_user -d erp_test_db` — Replica producción hacia test (útil antes de una batería de pruebas manuales).

## Consultas rápidas sin `psql`
- `docker exec erp-dev-db-container psql -U erp_dev_user -d erp_dev_db -c "SELECT COUNT(*) FROM employees"` — Ejecuta una consulta directa y devuelve el resultado en la terminal sin necesidad de abrir la consola interactiva.
- `docker exec erp-prod-db-container psql -U erp_prod_user -d erp_prod_db -c "\dt"` — Lista tablas de un esquema (el backslash requiere escapar con otra barra).

## Gestión de usuarios y contraseñas
- `docker exec erp-prod-db-container psql -U postgres -c "ALTER USER erp_prod_user WITH PASSWORD 'nueva_clave';"` — Actualiza la contraseña de un rol.
- `docker exec erp-prod-db-container psql -U postgres -c "CREATE USER auditor WITH PASSWORD 'password';"` — Crea un nuevo rol desde el host sin entrar a `psql`.
- `docker exec erp-prod-db-container psql -U postgres -c "GRANT CONNECT ON DATABASE erp_prod_db TO auditor;"` — Concede permisos básicos al nuevo rol.

## Administración avanzada
- `docker exec erp-prod-db-container psql -U postgres -c "SELECT pg_terminate_backend(pid) FROM pg_stat_activity WHERE datname = 'erp_prod_db' AND state = 'idle';"` — Cierra sesiones inactivas (evita bloqueos antes de una restauración).
- `docker exec erp-prod-db-container psql -U postgres -c "VACUUM (VERBOSE, ANALYZE);"` — Ejecuta mantenimiento de tablas.
- `docker exec erp-prod-db-container psql -U postgres -c "SELECT pg_size_pretty(pg_database_size('erp_prod_db'));"` — Obtiene el tamaño de la base en una unidad legible.

## Limpieza de volúmenes
- `docker volume ls | grep erp` — Identifica los volúmenes asociados al proyecto.
- `docker volume inspect <nombre_volumen>` — Comprueba en qué ruta del host se almacena el volumen.
- `docker volume rm <nombre_volumen>` — Elimina un volumen (asegúrate de que ningún contenedor lo usa).
- `docker compose down --volumes` — Detiene los servicios y elimina los volúmenes asociados al compose actual (usarlo solo cuando se quiera reiniciar la base desde cero).

## Consejos operativos
- Prefiere `pg_dump` + `pg_restore` en formato custom (`-F c`) para backups regulares; permite restauraciones selectivas.
- Usa `docker exec` con tuberías para sincronizaciones rápidas entre contenedores sin archivos temporales en disco.
- Mantén los scripts SQL en `sql/` y cópialos con `docker cp` solo cuando necesites ejecutarlos; así se versionan los cambios.
