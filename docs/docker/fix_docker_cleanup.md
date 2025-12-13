# Docker Cleanup y Reconstrucción Total

**Movido a:** docs/docker/fix_docker_cleanup.md

Este documento describe los pasos realizados para solucionar errores de arranque de contenedores Docker en el proyecto, incluyendo la limpieza profunda del entorno y la reconstrucción de imágenes.

---

## 1. Limpieza total de Docker

Elimina todos los contenedores, imágenes, volúmenes y cachés huérfanos para asegurar un entorno limpio:

```bash
docker system prune -af --volumes
```

- `-a`: Elimina todas las imágenes no usadas por contenedores activos.
- `-f`: Forzar sin pedir confirmación.
- `--volumes`: Incluye volúmenes huérfanos.

> **Nota:** Esto borra todo lo que no esté en uso. Úsalo solo si no tienes datos importantes en volúmenes o imágenes locales.

---

## 2. Reconstrucción completa de imágenes

Construye las imágenes del backend y frontend sin usar caché para evitar residuos de builds anteriores:

```bash
docker-compose build --no-cache backend frontend
```

- `--no-cache`: Fuerza a Docker a no usar ninguna capa cacheada.

---

## 3. Levantar los servicios

Arranca los contenedores backend y frontend en modo desatendido:

```bash
docker-compose up -d backend frontend
```

- `-d`: Ejecuta los servicios en segundo plano (detached).

---

## 4. Verificar estado de los contenedores

Comprueba que todos los servicios estén en estado `healthy`:

```bash
docker-compose ps
```

---

## 5. Resumen del fix aplicado

- Se detectó un error persistente de Docker Compose relacionado con `ContainerConfig` y residuos de builds.
- Se realizó una limpieza profunda del entorno Docker.
- Se reconstruyeron todas las imágenes desde cero.
- Se levantaron los servicios y se verificó que backend, frontend y base de datos estén en estado `healthy`.

---

> **Este procedimiento es seguro para entornos de desarrollo y útil cuando hay errores de arranque inexplicables o residuos de builds previos.**
