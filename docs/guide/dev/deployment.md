# Deployment

Guía para desplegar Bizflow ERP en diferentes entornos.

## Docker Compose (Auto-hosting)

La forma más sencilla de desplegar es con Docker Compose.

### Preparación

```bash
# Clonar en servidor
git clone https://github.com/jdomdev/bizflow-erp-springboot-react-docker.git
cd bizflow-erp-springboot-react-docker

# Configurar variables de producción
cp .env.example .env
nano .env
```

### Variables de producción

```env
# Base de datos
POSTGRES_PASSWORD=<TU_PASSWORD_SEGURA_PRODUCCION>
POSTGRES_DB=erp_prod_db

# JWT – genera con: openssl rand -base64 32  (32 bytes aleatorios → ~44 caracteres base64)
# Para mayor entropía usa: openssl rand -base64 64  (64 bytes → ~88 caracteres base64)
JWT_SECRET=<SECRETO_BASE64_MINIMO_32_BYTES>

# URLs
BACKEND_URL=https://api.tudominio.com
FRONTEND_URL=https://app.tudominio.com
```

### Iniciar producción

```bash
docker compose --profile prod up -d
```

### Verificar

```bash
docker compose ps
curl http://localhost:8181/api/v1/health
```

## Cloud Platforms

### Railway

1. Crear cuenta en [Railway](https://railway.app)
2. Nuevo proyecto → Deploy from GitHub
3. Seleccionar repositorio
4. Configurar variables de entorno
5. Railway detectará el Dockerfile

### Render

1. Crear cuenta en [Render](https://render.com)
2. New → Web Service
3. Conectar repositorio
4. Configurar:
   - Build Command: `cd backend && ./mvnw package -DskipTests`
   - Start Command: `java -jar backend/target/*.jar`
5. Añadir PostgreSQL desde Render Dashboard

### DigitalOcean App Platform

1. Crear App desde GitHub
2. Configurar componentes:
   - Backend: Docker
   - Frontend: Static Site
   - Database: PostgreSQL
3. Configurar variables de entorno
4. Deploy

## Kubernetes

### Manifiestos básicos

```yaml
# deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: bizflow-backend
spec:
  replicas: 2
  selector:
    matchLabels:
      app: bizflow-backend
  template:
    metadata:
      labels:
        app: bizflow-backend
    spec:
      containers:
      - name: backend
        image: bizflow/backend:latest
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "prod"
        - name: DATABASE_URL
          valueFrom:
            secretKeyRef:
              name: bizflow-secrets
              key: database-url
```

```yaml
# service.yaml
apiVersion: v1
kind: Service
metadata:
  name: bizflow-backend
spec:
  selector:
    app: bizflow-backend
  ports:
  - port: 80
    targetPort: 8080
  type: ClusterIP
```

## CI/CD con GitHub Actions

### Workflow de ejemplo

```yaml
# .github/workflows/deploy.yml
name: Deploy

on:
  push:
    branches: [main]

jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      
      - name: Set up Java
        uses: actions/setup-java@v4
        with:
          distribution: 'temurin'
          java-version: '17'
      
      - name: Build backend
        run: cd backend && ./mvnw package -DskipTests
      
      - name: Build Docker image
        run: docker build -t bizflow/backend:${{ github.sha }} ./backend
      
      - name: Push to registry
        run: docker push bizflow/backend:${{ github.sha }}
```

## Checklist de Producción

- [ ] Variables de entorno seguras (no en código)
- [ ] JWT_SECRET generado con `openssl rand -base64 32` (mínimo 32 bytes de entropía)
- [ ] HTTPS configurado
- [ ] Base de datos con backups automáticos
- [ ] Logs centralizados
- [ ] Monitoreo y alertas
- [ ] Pruebas de carga realizadas
- [ ] Plan de rollback documentado
