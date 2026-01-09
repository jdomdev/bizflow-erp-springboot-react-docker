
# Cambio de entorno con Docker Compose

## Estado actual

Ya no es necesario modificar ni renombrar el archivo `.env` para alternar entre entornos. Cada perfil de Docker Compose (`prod`, `dev`, `test`) carga automáticamente su archivo de variables correspondiente (`.env.prod`, `.env.dev`, `.env.test`) gracias a la directiva `env_file:` en el `docker-compose.yml`.

## ¿Qué debes hacer para cambiar de entorno?

1. Detén los contenedores activos:
	```bash
	docker compose down
	```
2. Levanta el entorno deseado:
	- **Producción:**
	  ```bash
	  docker compose --profile prod up -d
	  ```
	- **Desarrollo:**
	  ```bash
	  docker compose --profile dev up -d
	  ```
	- **Test:**
	  ```bash
	  docker compose --profile test up -d
	  ```

No es necesario modificar ni copiar archivos `.env`. Solo asegúrate de tener los archivos `.env.prod`, `.env.dev` y `.env.test` configurados localmente (y nunca subidos al repositorio).

## Notas de seguridad
- El archivo `.env` ya no es necesario y puede eliminarse.
- Mantén solo `.env.example` en el repositorio como plantilla.
- Los archivos de entorno reales deben estar en tu máquina local y estar listados en `.gitignore`.

---

> **Actualización:**
> El método antiguo de renombrar o editar `.env` ya no aplica. Docker Compose gestiona automáticamente el entorno según el perfil seleccionado.

---

1. **Detén cualquier contenedor previo:**
	```bash
	docker compose down
	```
2. **Configura la variable FRONTEND_DEPENDS_ON según el entorno (ver arriba).**
3. **Levanta el entorno deseado:**
	```bash
	docker compose --profile <entorno> up -d
	# Reemplaza <entorno> por prod, dev o test
	```
4. **Verifica el estado:**
	```bash
	docker compose ps
	```
	El backend debe estar en estado healthy.
5. **Consulta los logs si hay problemas:**
	```bash
	docker compose logs <backend-correspondiente>
	# Ejemplo: docker compose logs backend-prod
	```
6. **Comprueba que los datos iniciales se han cargado automáticamente.**
7. **(Opcional) Pobla usuarios vía scripts si necesitas datos adicionales:**
	```bash
	./scripts/users/register_users.sh         # En prod/dev se ejecuta automáticamente si existe el archivo de semillas
	./scripts/users/register_users_test.sh    # Wrapper manual para test
	```

---
## Persistencia y carga automática de datos
---

# Secuencia de inicialización automatizada de la base de datos

1. Al levantar la base de datos de cada entorno, el script SQL correspondiente (`01_init_prod.sql`, `01_init_dev.sql`, `01_init_test.sql`) delega inmediatamente en el archivo maestro del entorno (`sql/prod/00_master.sql`, `sql/dev/00_master.sql`, `sql/test/00_master.sql`).
2. Cada `00_master.sql` orquesta los `.sql` necesarios importando primero los artefactos comunes (esquema, catálogos compartidos) y después los seeds propios del entorno. Así se evita duplicidad y se mantiene un orden seguro para las claves foráneas.
3. El backend debe estar en estado healthy antes de poblar usuarios vía scripts.
4. Los scripts de usuarios usan el endpoint `/api/v1/auth/signup` para poblar la tabla de usuarios con contraseñas encriptadas.
5. En test, la base de datos se reinicia cada vez; en prod y dev, los datos persisten.

### Organización actual de los scripts SQL

- [sql/common](../../sql/common) agrupa los artefactos compartidos: [sql/common/01_schema.sql](../../sql/common/01_schema.sql) define tablas base, [sql/common/02_positions.sql](../../sql/common/02_positions.sql) y [sql/common/03_roles.sql](../../sql/common/03_roles.sql) cargan catálogos, y [sql/common/05_expense_admin_bootstrap.sql](../../sql/common/05_expense_admin_bootstrap.sql) crea los administradores Ada y Alan.
- [sql/dev_prod](../../sql/dev_prod) concentra los datasets voluminosos para dev/prod: [sql/dev_prod/10_employees_full.sql](../../sql/dev_prod/10_employees_full.sql), [sql/dev_prod/20_payrolls_full.sql](../../sql/dev_prod/20_payrolls_full.sql) y [sql/dev_prod/30_expenses_extended.sql](../../sql/dev_prod/30_expenses_extended.sql).
- [sql/test](../../sql/test) reutiliza los recursos anteriores mediante [sql/test/00_master.sql](../../sql/test/00_master.sql) y mantiene alineado el entorno de pruebas.
- [sql/dev/00_master.sql](../../sql/dev/00_master.sql) y [sql/prod/00_master.sql](../../sql/prod/00_master.sql) importan primero los scripts comunes y después los datasets específicos.
- [sql/test/00_master.sql](../../sql/test/00_master.sql) replica el orden de prod/dev; tras su ejecución se registran los usuarios de gastos vía scripts API.
- Los perfiles `prod` y `dev` incluyen servicios efímeros (`seed-expense-users-*`) que ejecutan `scripts/users/register_users.sh` dentro del contenedor; si el archivo `scripts/secrets/register_users_payloads.jsonl` (o la ruta indicada en `REGISTER_USERS_SEED_FILE`) no existe, se omite sin bloquear el arranque.
- La raíz de [sql](../../sql) solo conserva los entrypoints [sql/01_init_prod.sql](../../sql/01_init_prod.sql), [sql/01_init_dev.sql](../../sql/01_init_dev.sql) y [sql/01_init_test.sql](../../sql/01_init_test.sql); el resto de datasets viven en las carpetas anteriores.
- **Producción y Desarrollo:** [sql/01_init_prod.sql](../../sql/01_init_prod.sql) y [sql/01_init_dev.sql](../../sql/01_init_dev.sql) montan el entrypoint que delega en [sql/prod/00_master.sql](../../sql/prod/00_master.sql) o [sql/dev/00_master.sql](../../sql/dev/00_master.sql) para ejecutar esquema → catálogos → empleados → nóminas → bootstrap.
- **Testing:** [sql/01_init_test.sql](../../sql/01_init_test.sql) invoca [sql/test/00_master.sql](../../sql/test/00_master.sql) con la misma secuencia; al completar, se pueden cargar datasets adicionales con `docker compose exec <db-container> psql -U postgres -d <dbname> -f /docker-entrypoint-initdb.d/dev_prod/30_expenses_extended.sql`.

---

Cada entorno tiene su propia base de datos y comportamiento de persistencia:

- **Producción y Desarrollo (prod/dev):**
	- Los datos y tablas se mantienen entre reinicios gracias a los volúmenes Docker.
	- El script de inicialización (`01_init_prod.sql` o `01_init_dev.sql`) solo se ejecuta la primera vez que se crea la base de datos (cuando el volumen está vacío).
	- Si ya existen tablas o datos, el script NO se vuelve a ejecutar.
	- Así, los registros y cambios realizados en estos entornos son persistentes.

- **Test:**
	- La base de datos de test es efímera: se crea y destruye cada vez que levantas el entorno test.
	- El script `01_init_test.sql` se ejecuta siempre que se crea la base de datos, por lo que los datos se reinicializan en cada ciclo.
	- Útil para pruebas limpias y repetibles.

**Resumen:**
- Si quieres reinicializar los datos de prod o dev, elimina el volumen correspondiente manualmente.
- En test, los datos se reinician automáticamente cada vez.

# Guía para probar los tres entornos: prod, dev y test

Esta guía explica cómo levantar y cambiar entre los entornos **producción (prod)**, **desarrollo (dev)** y **test** usando Docker Compose y Spring Boot. Incluye los comandos y recomendaciones para evitar errores comunes.

---

## 1. Requisitos previos
- Tener Docker y Docker Compose instalados.
- Estar en la raíz del proyecto (`bizflow-erp-springboot-react-docker`).
- Tener los contenedores detenidos antes de cambiar de entorno.

Puedes ver los contenedores activos con:
```bash
docker compose ps
```





## Nuevo método: perfiles Docker Compose


Ahora cada entorno tiene su propio backend y base de datos, y un único frontend que puede apuntar al backend correspondiente según el entorno:

- **Producción:**
	**Terminal principal:**
	```bash
	docker compose --profile prod up -d
	```
	- Servicios activos: `erp-prod-db-container`, `erp-backend-prod-container`, `erp-frontend-container`
	- El backend escucha en el puerto 8081.
	- El frontend debe configurarse para apuntar a `http://localhost:8081/api/v1` (variable `VITE_API_URL`).

- **Desarrollo:**
	**Terminal principal:**
	```bash
	docker compose --profile dev up -d
	```
	- Servicios activos: `erp-dev-db-container`, `erp-backend-dev-container`, `erp-frontend-container`
	- El backend escucha en el puerto 8082.
	- El frontend debe configurarse para apuntar a `http://localhost:8082/api/v1` (variable `VITE_API_URL`).

- **Test:**
	**Terminal principal:**
	```bash
	docker compose --profile test up -d
	```
	- Servicios activos: `erp-test-db-container`, `erp-backend-test-container`, `erp-frontend-container`
	- El backend escucha en el puerto 8083.
	- El frontend debe configurarse para apuntar a `http://localhost:8083/api/v1` (variable `VITE_API_URL`).


Para ver qué entorno está levantado y qué contenedores están activos:
**Terminal secundaria:**
```bash
docker ps -a
```

Busca en la columna NAMES los contenedores activos:
- `erp-prod-db-container` + `erp-backend-prod-container` + `erp-frontend-container` → entorno PROD
- `erp-dev-db-container` + `erp-backend-dev-container` + `erp-frontend-container` → entorno DEV
- `erp-test-db-container` + `erp-backend-test-container` + `erp-frontend-container` → entorno TEST


**IMPORTANTE:** Antes de cambiar de entorno, siempre baja los contenedores activos:
**Terminal principal:**
```bash
docker compose down
```


Para detener todos los contenedores:
**Terminal principal:**
```bash
docker compose down
```


Ya no es necesario eliminar manualmente los contenedores uno a uno, basta con `docker compose down` antes de cambiar de perfil.

---



## 2. Probar entorno **PROD**



### a) Levanta el entorno prod (base de datos, backend-prod y frontend):
**Terminal principal:**
```bash
docker compose --profile prod up -d
```

### b) Verifica el estado:
**Terminal secundaria:**
```bash
docker compose ps
```
El backend-prod debe estar en estado `healthy`.

### c) Mira los logs si hay problemas:
**Terminal secundaria:**
```bash
docker compose logs backend-prod
```


### d) Usuarios y datos iniciales
Los usuarios y datos definidos en el script de inicialización (`01_init_prod.sql`, `01_init_dev.sql`, `01_init_test.sql`) se crean automáticamente al levantar la base de datos por primera vez (o en cada ciclo en test).
No es necesario ejecutar scripts manuales salvo que quieras añadir datos adicionales.

### e) Detén los contenedores antes de cambiar de entorno:
**Terminal principal:**
```bash
docker compose down
```

---



## 3. Probar entorno **DEV**



### a) Levanta el entorno dev (base de datos, backend-dev y frontend):
**Terminal principal:**
```bash
docker compose --profile dev up -d
```

### b) Verifica el estado:
**Terminal secundaria:**
```bash
docker compose ps
```
El backend-dev debe estar en estado `healthy`.

### c) Mira los logs si hay problemas:
**Terminal secundaria:**
```bash
docker compose logs backend-dev
```

### d) Pobla los usuarios de desarrollo:
**Terminal principal:**
```bash
./scripts/users/register_users.sh
```

### e) Detén los contenedores antes de cambiar de entorno:
**Terminal principal:**
```bash
docker compose down
```

---



## 4. Probar entorno **TEST**



### a) Levanta el entorno test (base de datos, backend-test y frontend):
**Terminal principal:**
```bash
docker compose --profile test up -d
```

### b) Verifica el estado:
**Terminal secundaria:**
```bash
docker compose ps
```
El backend-test debe estar en estado `healthy`.

### c) Mira los logs si hay problemas:
**Terminal secundaria:**
```bash
docker compose logs backend-test
```

### d) Pobla los usuarios de test:
**Terminal principal:**
```bash
./scripts/users/register_users_test.sh
```

### e) Detén los contenedores al finalizar:
**Terminal principal:**
```bash
docker compose down
```

---


## 5. Consejos y buenas prácticas
- **Siempre detén los contenedores antes de cambiar de entorno** para evitar conflictos de puertos y datos.
- **Verifica el estado** del backend correspondiente antes de poblar usuarios.
- **Consulta los logs** si el backend no está "healthy".
- Si tienes problemas de puertos ocupados, asegúrate de que no haya otros procesos usando los puertos 5432, 5433, 5434, 8081, 8082, 8083.
- **Configura el frontend** con la variable `VITE_API_URL` para apuntar al backend correcto según el entorno:
	- Prod: `http://localhost:8081/api/v1`
	- Dev: `http://localhost:8082/api/v1`
	- Test: `http://localhost:8083/api/v1`

---

> **Recuerda:** Cambia de entorno solo con los contenedores detenidos. Sigue el orden: prod → dev → test, o el que prefieras, pero siempre deteniendo antes de cambiar.
