# Guía de comandos Docker

Esta guía recopila los comandos de Docker y Docker Compose que usamos con más frecuencia en el proyecto. Todos los ejemplos asumen que se ejecutan desde la raíz del repositorio salvo que se indique lo contrario.

## Gestión de contenedores
- `docker ps` — Lista los contenedores en ejecución.
- `docker ps -a` — Lista todos los contenedores, incluidos los detenidos.
- `docker start <nombre|id>` — Arranca un contenedor detenido.
- `docker stop <nombre|id>` — Detiene un contenedor en ejecución de forma ordenada.
- `docker restart <nombre|id>` — Reinicia un contenedor.
- `docker rm <nombre|id>` — Elimina un contenedor detenido. Añade `-f` para forzar la eliminación de uno en ejecución.
- `docker logs <nombre|id>` — Muestra el log histórico de un contenedor.
- `docker logs -f <nombre|id>` — Sigue en tiempo real el log del contenedor.
- `docker exec -it <nombre|id> /bin/sh` — Abre una shell interactiva dentro del contenedor (si la imagen usa Alpine o BusyBox).
- `docker exec -it <nombre|id> /bin/bash` — Igual que el anterior pero con Bash (para imágenes basadas en Debian/Ubuntu).

## Gestión de imágenes
- `docker images` — Lista las imágenes disponibles localmente.
- `docker build -t <nombre>:<tag> <ruta>` — Construye una imagen a partir de un Dockerfile.
- `docker pull <imagen>:<tag>` — Descarga una imagen del registro remoto.
- `docker rmi <imagen>:<tag>` — Elimina una imagen local.
- `docker image prune` — Borra capas e imágenes dangling (no usadas por contenedores).
- `docker system df` — Muestra el uso de disco de imágenes, contenedores y volúmenes.

## docker compose
- `docker compose up` — Construye (si es necesario) y levanta los servicios definidos en `docker-compose.yml`.
- `docker compose up --build` — Fuerza la recompilación de las imágenes antes de levantar los servicios.
- `docker compose up -d` — Levanta los servicios en segundo plano.
- `docker compose down` — Detiene y elimina contenedores, redes y artefactos creados por `up`.
- `docker compose down --remove-orphans` — Incluye los contenedores huérfanos (perfiles antiguos o servicios renombrados).
- `docker compose logs -f <servicio>` — Sigue en tiempo real los logs de un servicio definido en el compose.
- `docker compose ps` — Lista el estado de los servicios del compose actual.
- `docker compose build <servicio>` — Construye o reconstruye la imagen de un servicio concreto.
- `docker compose rm` — Elimina contenedores detenidos pertenecientes al compose actual.
- `docker compose config` — Valida y muestra la configuración resultante tras procesar variables y perfiles.

## Perfiles del proyecto
- `docker compose --profile dev up -d --build` — Reconstruye y levanta los servicios del entorno de desarrollo.
- `docker compose --profile test up -d --build` — Reconstruye y levanta los servicios del entorno de test.
- `docker compose --profile prod up -d --build` — Reconstruye y levanta los servicios del entorno de producción.
- `docker compose --profile <perfil> down` — Detiene y limpia los servicios del perfil indicado.

## Limpieza y mantenimiento
- `docker system prune` — Elimina contenedores detenidos, redes sin uso e imágenes dangling (pregunta confirmación).
- `docker system prune -a` — Además elimina imágenes no referenciadas por ningún contenedor.
- `docker volume prune` — Borra volúmenes no utilizados por contenedores.
- `docker network prune` — Borra redes sin contenedores asociados.

## Inspección y diagnóstico
- `docker inspect <nombre|id>` — Devuelve un JSON con la configuración completa del contenedor.
- `docker stats` — Muestra en tiempo real CPU, RAM, I/O y red por contenedor.
- `docker top <nombre|id>` — Lista los procesos activos dentro del contenedor.
- `docker events` — Emite eventos del demonio Docker (deploys, paradas, etc.) en tiempo real.
