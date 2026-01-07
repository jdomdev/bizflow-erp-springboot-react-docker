# Plan de centralización de imágenes Docker

## Estado actual y validaciones

- El empaquetado `mvn clean package -DskipTests` genera el artefacto final en `target/bizflowerp-1.1.0.jar`. La ruta usada en el Dockerfile de runtime deberá copiar desde `/app/target/*.jar` cuando reutilicemos la etapa de build.
- El flujo de tests reutiliza Maven wrapper, por lo que también se beneficiará de una imagen base con dependencias cacheadas (`dependency:go-offline`).
- El frontend Vite ya utiliza multi-stage; se revisará para compartir nodos base y reutilizar scripts de healthcheck.

## Argumentos y convenciones compartidas propuestas

| Variable | Valor por defecto | Uso previsto |
|----------|-------------------|--------------|
| `ARG MAVEN_VERSION` | `3.9.5` | Imagen `maven:${MAVEN_VERSION}-eclipse-temurin-${TEMURIN_VERSION}-alpine` |
| `ARG TEMURIN_VERSION` | `21` | Versión de JDK/JRE para backend |
| `ARG NODE_VERSION` | `20` | Imagen base Node para el build del frontend |
| `ARG APP_UID` | `1000` | Usuario estándar dentro de contenedores |
| `ARG APP_GID` | `1000` | Grupo estándar dentro de contenedores |
| `ARG NPM_CLI_VERSION` | `latest` | Opcional, fija la versión de npm | 

Estas variables se exportarán desde imágenes base en `docker/base/` y podrán sobreescribirse en escenarios específicos.

## Plan de migración incremental

1. **Crear imágenes base**
   - `docker/base/backend-builder.Dockerfile`: Maven + Temurin configurado con argumentos compartidos.
   - `docker/base/backend-runtime.Dockerfile`: JRE ligero, instalación opcional de `curl`, creación del usuario aplicativo.
   - `docker/base/frontend-builder.Dockerfile` y `docker/base/frontend-runtime.Dockerfile`: Etapas separadas para Node y Nginx.
2. **Adaptar Dockerfiles existentes**
   - Refactorizar `backend/Dockerfile` para que consuma las bases nuevas (`FROM base` + copia del JAR desde `/app/target`).
   - Actualizar `backend/Dockerfile.test` reutilizando la misma etapa builder.
   - Ajustar `frontend/Dockerfile` para heredar de las imágenes base y centralizar scripts de healthcheck y creación de usuario.
3. **Actualizar docker-compose**
   - Declarar las nuevas rutas de build y, cuando proceda, enviar argumentos (`MAVEN_VERSION`, etc.) desde Compose.
   - Verificar que las rutas de volúmenes y logs no cambian para no romper despliegues locales.
4. **Validar builds**
   - Ejecutar `docker compose build backend-prod backend-dev backend-test` tras los cambios.
   - Correr `docker compose run --rm backend-dev ./mvnw test` o equivalente para validar la imagen de tests.
   - Levantar los perfiles `prod/dev/test` para confirmar que healthchecks y seeds siguen operando.

## Documentación requerida

- Añadir a `docs/docker/` un manual breve sobre cómo reconstruir las imágenes base (`docker build -f docker/base/backend-builder.Dockerfile .`).
- Actualizar `docs/docker/docker_commands_overview.md` con los nuevos comandos de build.
- Incluir un bloque en `README.md` (o en la guía de desarrollo) explicando la jerarquía `docker/base/` y cómo agregar nuevas variantes.
