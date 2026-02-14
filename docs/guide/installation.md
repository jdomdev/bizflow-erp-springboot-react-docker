# Instalación

Esta guía cubre los diferentes métodos de instalación de Bizflow ERP.

## Docker Compose (Recomendado)

La forma más sencilla de ejecutar Bizflow ERP es con Docker Compose.

### Paso 1: Clonar repositorio

```bash
git clone https://github.com/jdomdev/bizflow-erp-springboot-react-docker.git
cd bizflow-erp-springboot-react-docker
```

### Paso 2: Configurar variables de entorno

Copia el archivo de ejemplo y configura tus variables:

```bash
cp .env.example .env
```

Variables importantes:

```bash
# Base de datos
POSTGRES_USER=postgres
POSTGRES_PASSWORD=tu_password_seguro

# JWT
JWT_SECRET=tu_clave_secreta_muy_larga

# Entorno
SPRING_PROFILES_ACTIVE=dev
```

### Paso 3: Iniciar servicios

```bash
# Usando Make
make dev

# O directamente con Docker Compose
docker compose --profile dev up -d
```

### Paso 4: Verificar servicios

```bash
# Ver estado de contenedores
docker compose ps

# Ver logs
docker compose logs -f backend-dev
```

## Entornos Disponibles

Bizflow ERP soporta tres entornos aislados:

| Entorno | Perfil | Puerto Backend | Puerto DB | Uso |
|---------|--------|----------------|-----------|-----|
| Desarrollo | `dev` | 8080 | 5433 | Desarrollo local |
| Testing | `test` | 8282 | 5434 | Tests automatizados |
| Producción | `prod` | 8181 | 5442 | Despliegue final |

### Cambiar de entorno

```bash
# Desarrollo
make dev

# Testing  
make test

# Producción
make prod
```

## Instalación Manual (Sin Docker)

### Prerrequisitos

- Java 17 (OpenJDK o similar)
- Node.js 18+
- PostgreSQL 15+
- Maven 3.6+

### Backend

```bash
cd backend

# Configurar base de datos
createdb erp_dev_db

# Compilar y ejecutar
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### Frontend

```bash
cd frontend

# Instalar dependencias
npm install

# Iniciar servidor de desarrollo
npm run dev
```

## Poblar datos de prueba

```bash
# Con Make
make db-seed

# O manualmente con Python
cd scripts/seeds
python seed_runner.py --env dev
```

## Solución de Problemas

### Puerto en uso

```bash
# Verificar qué proceso usa el puerto
lsof -i :8080

# Detener servicios
make stop
```

### Errores de conexión a BD

```bash
# Verificar que PostgreSQL está corriendo
docker compose ps

# Revisar logs
docker compose logs db-dev
```

### Limpiar y reiniciar

```bash
# Detener y eliminar volúmenes
make clean

# Reiniciar desde cero
make dev
```
