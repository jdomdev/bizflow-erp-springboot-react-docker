# Ejecutar tests del backend en contenedor Docker

**Movido a:** docs/docker/README_TESTS_DOCKER.md

Puedes ejecutar los tests del backend en un contenedor, usando la misma red que las bases de datos definidas en docker-compose. Esto asegura que el hostname `erp-test-db-container` sea resolvible y el entorno sea idéntico a producción.

## Pasos rápidos

1. **Levanta las bases de datos (si no están corriendo):**

```bash
docker compose up -d erp-test-db-container
```

2. **Construye la imagen de test:**

```bash
cd backend
docker build -f Dockerfile.test -t bizflow-backend-test .
```

3. **Ejecuta los tests en la red de docker-compose:**

```bash
docker run --rm --network=bizflow-erp-springboot-react-docker_bizflow_erp_network \
  -v ~/.m2:/root/.m2 \
  -v $(pwd)/src:/app/src \
  -v $(pwd)/target:/app/target \
  bizflow-backend-test
```

Esto ejecutará los tests con la configuración de `application-test.properties` apuntando a `erp-test-db-container`.


**Consejo:** Puedes automatizar esto con un script o Makefile.
**Moved to:** docs/docker/README_TESTS_DOCKER.md
