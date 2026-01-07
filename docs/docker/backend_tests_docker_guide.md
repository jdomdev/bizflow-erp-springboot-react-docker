# Guía para ejecutar tests del backend con Docker

Puedes ejecutar la batería de tests del backend dentro de un contenedor reutilizando la misma red que las bases de datos definidas en docker compose. Así el hostname `erp-test-db-container` se resuelve correctamente y el entorno resulta idéntico al de producción.

## Pasos rápidos

1. **Levanta las bases de datos de prueba (si no están corriendo):**
   ```bash
   docker compose up -d erp-test-db-container
   ```

2. **Construye la imagen de pruebas:**
   ```bash
   cd backend
   docker build -f Dockerfile.test -t bizflow-backend-test .
   ```

3. **Ejecuta los tests en la red de docker compose:**
   ```bash
    docker run --rm --network=bizflow-erp-springboot-react-docker_bizflow_erp_network \
       -v ~/.m2:/root/.m2 \
       -v $(pwd)/src:/app/src \
       -v $(pwd)/../build/backend:/build/backend \
       bizflow-backend-test
   ```

El comando usa la configuración `application-test.properties`, apuntando al servicio `erp-test-db-container` para acceder a la base de datos de pruebas.

> **Consejo:** añade estos pasos a un script o Makefile para simplificar ejecuciones recurrentes.
