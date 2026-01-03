# Gestión de Contraseñas y Contexto del Shell

## Resumen

Este documento aclara cómo circulan las credenciales por el proyecto, qué aportan los ficheros `.env.*`, cómo interviene el entorno del proceso del shell y por qué GitGuardian todavía puede indicar secretos heredados en las pull requests.

## Ficheros `.env` en el repositorio

| Fichero | Uso | Variables clave |
| --- | --- | --- |
| `.env.dev` | Valores por defecto para el stack Docker de desarrollo. | `POSTGRES_PASSWORD`, `SPRING_DATASOURCE_PASSWORD`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_URL`, `APP_JWT_SECRET` |
| `.env.test` | Ajustes para pruebas automatizadas o stack de test local. | Las mismas claves con valores específicos de test. |
| `.env.prod` | Valores de referencia para plantillas de despliegue en producción. | Las mismas claves con valores orientados a producción. |

Los tres ficheros residen en el repo por comodidad, pero **no deben versionarse nunca con secretos reales**. Actualmente contienen contraseñas y claves JWT de ejemplo pensadas para ser sustituidas por valores reales suministrados mediante gestores de secretos o pipelines CI/CD.

### Cómo usa Docker estos ficheros

- Docker Compose (ver `docker-compose.yml`) lee los ficheros de entorno al levantar servicios. Los valores se inyectan en el contenedor como `POSTGRES_PASSWORD`, `SPRING_DATASOURCE_*`, etc.
- Spring Boot consume `SPRING_DATASOURCE_*` dinámicamente en tiempo de ejecución (vía la abstracción de entorno de Spring) sin necesidad de incrustar secretos en el jar.
- El contenedor de Postgres emplea `POSTGRES_PASSWORD` para inicializar la cuenta de base de datos.

### Relación con `DB_PASSWORD`

Los scripts de copias de seguridad ([scripts/backup_dev_db.sh](../../scripts/backup_dev_db.sh) y [scripts/backup_prod_db.sh](../../scripts/backup_prod_db.sh)) evitan de forma deliberada interpretar los `.env.*`. Solo leen `DB_PASSWORD` del shell que los invoca para garantizar que:

1. Las credenciales llegan por un canal externo (línea de comandos, gestor de secretos, variable en CI).
2. `DB_PASSWORD` pueda diferir de `POSTGRES_PASSWORD` si rotas credenciales para herramientas sin tocar la configuración del contenedor.

Flujo típico:

```bash
# Cargar el fichero .env deseado en el shell actual (exporta todas las variables)
set -a
source .env.dev
set +a

# Propagar la contraseña al nombre que esperan los scripts
export DB_PASSWORD="$POSTGRES_PASSWORD"

./scripts/backup_dev_db.sh
```

Puedes repetir el patrón con `.env.test` o `.env.prod` según el servicio que necesites.

## Fundamentos del entorno de shell

- El **shell** (p. ej. `bash`, `zsh`) mantiene en memoria un diccionario con las variables de entorno de la sesión en curso.
- Las variables viven solo dentro del árbol de procesos lanzado desde ese shell. Al cerrar la terminal el entorno desaparece, salvo que escribas los `export` en `~/.bashrc`, `~/.profile` u otro script de inicio.
- Las variables exportadas se heredan a los procesos hijos (Docker CLI, Maven, Node). Las variables no exportadas permanecen locales al shell y no son visibles aguas abajo.
- Por defecto no se persiste ningún dato de entorno en disco. Guardar secretos implica almacenarlos manualmente en ficheros de configuración, gestores de secretos o llaveros del sistema operativo.

### Formas habituales de definir el entorno

1. **Export interactivo**
   ```bash
   export DB_PASSWORD="supersecret"
   ```
2. **Cargar ficheros**
   ```bash
   set -a
   source .env.dev
   set +a
   ```
3. **Inyección puntual en un proceso**
   ```bash
   DB_PASSWORD="supersecret" ./scripts/backup_dev_db.sh
   ```
4. **Gestores de secretos en CI/CD** (GitHub Actions, Azure Pipelines, etc.) inyectan variables en tiempo de ejecución sin guardarlas en el repo.

## Por qué GitGuardian sigue alertando

- GitGuardian escanea datos históricos y pull requests abiertas. Aunque reescribas la historia localmente y hagas force push, las alertas pueden permanecer hasta que vuelva a analizar el repositorio.
- Las PR abiertas antes del rewrite pueden seguir apuntando al grafo antiguo. Hay que actualizar esas ramas (rebase o recrearlas) para que GitGuardian inspeccione la historia saneada.
- En la documentación aún hay cadenas JWT **de ejemplo** (por ejemplo en las guías de la sesión 5). Aunque sean muestras inertes, GitGuardian detecta el patrón. Sustituirlas por placeholders claros (p. ej. `{{jwt_token_example}}`) evitará nuevas alertas.

## Próximos pasos recomendados

1. **Reemplazar los ejemplos de la documentación** que contienen cadenas con formato JWT por placeholders para esquivar coincidencias.
2. **Asegurar que todas las ramas de PR** se reconstruyen a partir de la historia reescrita (`git reset --hard origin/<rama>` o recreación) para que GitGuardian vea commits limpios.
3. **Mantener los secretos fuera del repo**: almacena credenciales reales (incluida `DB_PASSWORD`) en ficheros `.env.local`, llaveros del sistema operativo o gestores de secretos. No comprometas contraseñas reales en los `.env.*`.
4. **Revisar `.gitignore`**: confirma que los scripts auxiliares con credenciales embebidas (p. ej. `init-expense-data.sh`) siguen ignorados o se han adaptado para leer de variables de entorno como hacen los scripts de backup.

Seguir este flujo mantiene las credenciales fuera del repositorio y a la vez permite que el desarrollo local y los scripts de automatización funcionen apoyándose en variables de entorno bien definidas.
