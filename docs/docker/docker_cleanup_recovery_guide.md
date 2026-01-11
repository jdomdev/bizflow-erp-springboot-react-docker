# Guía de limpieza y recuperación de Docker

Esta guía detalla cómo limpiar a fondo el entorno Docker del proyecto y reconstruir los servicios cuando aparecen errores persistentes o residuos de builds anteriores. Incluye una opción de limpieza rápida y un flujo controlado paso a paso.

## 1. Limpieza rápida (*full prune*)
Utiliza esta opción cuando necesitas partir de un entorno completamente limpio y puedes sacrificar imágenes y volúmenes locales que no estén en uso:

```bash
docker system prune -af --volumes
```

- `-a`: elimina todas las imágenes que no tengan contenedores asociados.
- `-f`: omite la confirmación interactiva.
- `--volumes`: borra volúmenes huérfanos.

> ⚠️ **Cuidado:** este comando elimina cualquier recurso que no esté en uso activo. Asegúrate de no necesitar los volúmenes o imágenes que puedas tener en otros proyectos.

## 2. Limpieza controlada
Si prefieres gestionar cada recurso manualmente, sigue estos pasos:

```bash
docker ps -aq | xargs -r docker stop
docker ps -aq | xargs -r docker rm
docker volume ls -q | xargs -r docker volume rm
docker images -f "dangling=true" -q | xargs -r docker rmi
```

- Detiene y elimina todos los contenedores en ejecución.
- Borra volúmenes e imágenes huérfanas, manteniendo únicamente las capas necesarias.

## 3. Reconstrucción de imágenes
Fuerza builds limpios para backend y frontend evitando capas cacheadas:

```bash
docker compose build --no-cache backend frontend
```

- `--no-cache`: invalida el caché y recompila todas las capas.

## 4. Levantar servicios
Inicia nuevamente los servicios clave en segundo plano:

```bash
docker compose up -d backend frontend
```

Si necesitas incluir las bases de datos u otros perfiles, añade los servicios requeridos al comando anterior.

## 5. Verificación de estado
Asegúrate de que los contenedores estén saludables antes de continuar con pruebas manuales:

```bash
docker compose ps
```

- Verifica que el estado sea `running` o `healthy` para backend, frontend y bases de datos.

## 6. Restaurar la base de datos desde un backup
Cuando sea necesario restaurar la base de producción en local, copia el dump y ejecútalo dentro del contenedor correspondiente:

```bash
docker cp backups/prod/erp_prod_db_backup_YYYYMMDD_HHMMSS.dump erp-prod-db-container:/tmp/erp_prod_db_backup.dump
docker exec erp-prod-db-container pg_restore -U erp_prod_user -d erp_prod_db /tmp/erp_prod_db_backup.dump
```

- Sustituye `YYYYMMDD_HHMMSS` por la marca temporal del backup elegido.
- Ajusta el nombre del contenedor y las credenciales según el entorno que necesites restaurar (por ejemplo, `erp-dev-db-container`).

## Resumen operativo
1. Decide entre limpieza rápida (`docker system prune`) o controlada según tus necesidades.
2. Reconstruye las imágenes con `docker compose build --no-cache` para garantizar builds limpios.
3. Levanta los servicios con `docker compose up -d` y verifica su estado.
4. Restaura la base de datos desde el backup deseado si necesitas datos consistentes.

Con estos pasos recuperas un entorno Docker estable listo para nuevas iteraciones de desarrollo.
