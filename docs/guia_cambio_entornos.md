# Sobre los contenedores frontend

Cada entorno tiene su propio contenedor frontend:

- **frontend-prod** (puerto 8080)
- **frontend-dev** (puerto 8085)
- **frontend-test** (puerto 8086)

Solo se levanta el frontend del perfil activo. Los otros frontends no se ejecutan ni ocupan recursos ni puertos.

Cuando cambias de entorno y ejecutas `docker compose down -v`, se eliminan los contenedores y volúmenes del entorno anterior, asegurando que solo esté activo el entorno que necesitas.

Esto permite trabajar de forma limpia y eficiente, sin contenedores innecesarios en segundo plano.

# Flujo multi-entorno con tres frontends

Ahora cada entorno (prod, dev, test) tiene su propio servicio frontend, backend y base de datos. Solo se levanta el frontend del entorno activo, así no ocupas recursos innecesarios.


# Puertos de las bases de datos

- **Producción:** PostgreSQL en el puerto 5442
- **Desarrollo:** PostgreSQL en el puerto 5433
- **Test:** PostgreSQL en el puerto 5434

Para levantar cada entorno desde cero:

1. Elimina todos los contenedores y volúmenes previos:
	 ```bash
	 docker compose down -v
	 ```
2. Levanta el entorno deseado:
	   - **Producción:**
		   ```bash
		   docker compose --profile prod up -d
		   # Accede al frontend en http://localhost:8080
		   # El backend-prod escucha en http://localhost:8181
		   # La base de datos prod escucha en el puerto 5442
		   ```
	 - **Desarrollo:**
		 ```bash
		 docker compose --profile dev up -d
		 # Accede al frontend en http://localhost:8085
		 ```
	 - **Test:**
		 ```bash
		 docker compose --profile test up -d
		 # Accede al frontend en http://localhost:8086
		 ```

Solo se ejecutan los servicios del perfil activo. Los otros frontends y backends no ocupan recursos ni puertos.
# Configuración de la variable FRONTEND_DEPENDS_ON

Para que el frontend dependa del backend correcto según el entorno, antes de levantar los servicios ejecuta en la terminal:

- **Producción:**
	```bash
	export FRONTEND_DEPENDS_ON=backend-prod
	docker compose --profile prod up -d
	```
- **Desarrollo:**
	```bash
	export FRONTEND_DEPENDS_ON=backend-dev
	docker compose --profile dev up -d
	```
- **Test:**
	```bash
	export FRONTEND_DEPENDS_ON=backend-test
	docker compose --profile test up -d
	```

Esto asegura que el frontend solo espere al backend correspondiente y evita errores de dependencias.

# Guía para probar los tres entornos: prod, dev y test

> Esta guía incluye también la secuencia de inicialización automatizada de la base de datos y la configuración de dependencias frontend-backend.
---

## Secuencia recomendada de inicialización y prueba de entornos

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
	./scripts/register_users.sh         # Para dev/prod
	./scripts/register_users_test.sh    # Para test
	```

---
## Persistencia y carga automática de datos
---

# Secuencia de inicialización automatizada de la base de datos

1. Al levantar la base de datos de cada entorno, el script SQL correspondiente (`init_prod.sql`, `init_dev.sql`, `init_test.sql`) se ejecuta automáticamente si el volumen está vacío.
2. El backend debe estar en estado healthy antes de poblar usuarios vía scripts.
3. Los scripts de usuarios usan el endpoint `/api/v1/auth/signup` para poblar la tabla de usuarios con contraseñas encriptadas.
4. En test, la base de datos se reinicia cada vez; en prod y dev, los datos persisten.

---

Cada entorno tiene su propia base de datos y comportamiento de persistencia:

- **Producción y Desarrollo (prod/dev):**
	- Los datos y tablas se mantienen entre reinicios gracias a los volúmenes Docker.
	- El script de inicialización (`init_prod.sql` o `init_dev.sql`) solo se ejecuta la primera vez que se crea la base de datos (cuando el volumen está vacío).
	- Si ya existen tablas o datos, el script NO se vuelve a ejecutar.
	- Así, los registros y cambios realizados en estos entornos son persistentes.

- **Test:**
	- La base de datos de test es efímera: se crea y destruye cada vez que levantas el entorno test.
	- El script `init_test.sql` se ejecuta siempre que se crea la base de datos, por lo que los datos se reinicializan en cada ciclo.
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
Los usuarios y datos definidos en el script de inicialización (`init_prod.sql`, `init_dev.sql`, `init_test.sql`) se crean automáticamente al levantar la base de datos por primera vez (o en cada ciclo en test).
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
./scripts/register_users.sh
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
./scripts/register_users_test.sh
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
