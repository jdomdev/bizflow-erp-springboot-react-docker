# Guía de Despliegue - ExpenseNoteApp v1.1.0

Guía completa para desplegar ExpenseNoteApp en diferentes entornos.

## 📋 Tabla de Contenidos

1. [Visión General](#visión-general)
2. [Requisitos Previos](#requisitos-previos)
3. [Despliegue Local](#despliegue-local)
4. [Despliegue con Docker](#despliegue-con-docker)
5. [Despliegue en Producción](#despliegue-en-producción)
6. [Despliegue en Cloud](#despliegue-en-cloud)
7. [Configuración de Entornos](#configuración-de-entornos)
8. [Monitoreo y Mantenimiento](#monitoreo-y-mantenimiento)
9. [Respaldos y Recuperación](#respaldos-y-recuperación)

---

## 🎯 Visión General

Esta guía cubre diferentes escenarios de despliegue:

- **Desarrollo Local**: Para desarrollo y pruebas
- **Docker Local**: Usando contenedores
- **Servidor Dedicado**: En VPS o servidor físico
- **Cloud Providers**: AWS, Azure, GCP, Heroku

---

## 📦 Requisitos Previos

### Para Despliegue Local

- Java 21 JDK
- Node.js 18+ y npm
- PostgreSQL 12+
- Maven 3.6+
- Git

### Para Despliegue Docker

- Docker 20+
- Docker Compose 2+

### Para Despliegue en Producción

- Servidor Linux (Ubuntu 20.04+ recomendado)
- Mínimo 2GB RAM
- 20GB espacio en disco
- Acceso SSH
- Dominio y SSL certificado

---

## 💻 Despliegue Local

### 1. Clonar Repositorio

```bash
git clone https://github.com/yourusername/ExpenseNoteApp.git
cd ExpenseNoteApp
```

### 2. Configurar Base de Datos

```bash
# Iniciar PostgreSQL
sudo systemctl start postgresql

# Crear base de datos
sudo -u postgres psql
CREATE DATABASE expense_note_app;
CREATE USER expenseapp WITH PASSWORD 'secure_password';
GRANT ALL PRIVILEGES ON DATABASE expense_note_app TO expenseapp;
\q
```

### 3. Configurar Backend

```bash
cd backend-springboot

# Editar application.properties
nano src/main/resources/application.properties
```

```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/expense_note_app
spring.datasource.username=expenseapp
spring.datasource.password=secure_password

# JWT
app.jwt.secret=your-super-secret-key-minimum-32-characters-long
app.jwt.expiration=86400000

# Server
server.port=8080

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
```

### 4. Compilar y Ejecutar Backend

```bash
# Compilar
mvn clean package -DskipTests

# Ejecutar
java -jar target/expensenoteapp-1.1.0.jar

# O con Maven
mvn spring-boot:run
```

### 5. Configurar Frontend

```bash
cd frontend

# Instalar dependencias
npm install

# Crear archivo .env
cat > .env << EOF
VITE_API_URL=http://localhost:8080/api/v1
EOF

# Ejecutar en desarrollo
npm run dev

# O compilar para producción
npm run build
```

### 6. Acceder a la Aplicación

- Frontend: http://localhost:3000
- Backend: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html

---

## 🐳 Despliegue con Docker

### Opción 1: Docker Compose (Recomendado)

```bash
# Clonar repositorio
git clone https://github.com/yourusername/ExpenseNoteApp.git
cd ExpenseNoteApp

# Crear archivo .env para variables de entorno
cat > .env << EOF
# Database
POSTGRES_DB=expense_note_app
POSTGRES_USER=expenseapp
POSTGRES_PASSWORD=secure_password

# Backend
JWT_SECRET=your-super-secret-key-minimum-32-characters-long
JWT_EXPIRATION=86400000

# Frontend
VITE_API_URL=http://localhost:8080/api/v1
EOF

# Iniciar todos los servicios
docker-compose up -d

# Ver logs
docker-compose logs -f

# Detener servicios
docker-compose down
```

### Opción 2: Contenedores Individuales

#### Backend

```bash
cd backend-springboot

# Compilar aplicación
mvn clean package -DskipTests

# Construir imagen Docker
docker build -t expenseapp-backend .

# Ejecutar contenedor
docker run -d \
  --name expenseapp-backend \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/expense_note_app \
  -e SPRING_DATASOURCE_USERNAME=expenseapp \
  -e SPRING_DATASOURCE_PASSWORD=secure_password \
  -e APP_JWT_SECRET=your-secret-key \
  expenseapp-backend
```

#### Frontend

```bash
cd frontend

# Construir imagen Docker
docker build -t expenseapp-frontend \
  --build-arg VITE_API_URL=http://localhost:8080/api/v1 .

# Ejecutar contenedor
docker run -d \
  --name expenseapp-frontend \
  -p 80:80 \
  expenseapp-frontend
```

### Docker Compose Completo

```yaml
version: '3.8'

services:
  postgres:
    image: postgres:15-alpine
    container_name: expenseapp-db
    environment:
      POSTGRES_DB: ${POSTGRES_DB}
      POSTGRES_USER: ${POSTGRES_USER}
      POSTGRES_PASSWORD: ${POSTGRES_PASSWORD}
    volumes:
      - postgres_data:/var/lib/postgresql/data
    ports:
      - "5432:5432"
    networks:
      - expense_network
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U ${POSTGRES_USER}"]
      interval: 10s
      timeout: 5s
      retries: 5

  backend:
    build:
      context: ./backend-springboot
      dockerfile: Dockerfile
    container_name: expenseapp-backend
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/${POSTGRES_DB}
      SPRING_DATASOURCE_USERNAME: ${POSTGRES_USER}
      SPRING_DATASOURCE_PASSWORD: ${POSTGRES_PASSWORD}
      APP_JWT_SECRET: ${JWT_SECRET}
      APP_JWT_EXPIRATION: ${JWT_EXPIRATION}
    ports:
      - "8080:8080"
    depends_on:
      postgres:
        condition: service_healthy
    networks:
      - expense_network
    restart: unless-stopped

  frontend:
    build:
      context: ./frontend
      dockerfile: Dockerfile
      args:
        VITE_API_URL: ${VITE_API_URL}
    container_name: expenseapp-frontend
    ports:
      - "80:80"
    depends_on:
      - backend
    networks:
      - expense_network
    restart: unless-stopped

volumes:
  postgres_data:

networks:
  expense_network:
    driver: bridge
```

---

## 🚀 Despliegue en Producción

### Arquitectura de Producción

```
Internet
    │
    ▼
┌────────────────┐
│  Load Balancer │ (Nginx/HAProxy)
│   + SSL/TLS    │
└────────┬───────┘
         │
    ┌────┴────┐
    │         │
┌───▼──┐  ┌──▼───┐
│ App  │  │ App  │ (Multiple instances)
│ Node │  │ Node │
└───┬──┘  └──┬───┘
    │        │
    └────┬───┘
         │
    ┌────▼────┐
    │Database │ (PostgreSQL)
    │Cluster  │
    └─────────┘
```

### 1. Preparar Servidor

```bash
# Actualizar sistema
sudo apt update && sudo apt upgrade -y

# Instalar dependencias
sudo apt install -y \
  openjdk-21-jdk \
  postgresql \
  nginx \
  certbot \
  python3-certbot-nginx

# Crear usuario para la aplicación
sudo useradd -m -s /bin/bash expenseapp
```

### 2. Configurar PostgreSQL

```bash
# Editar configuración
sudo nano /etc/postgresql/14/main/pg_hba.conf

# Agregar:
# local   expense_note_app    expenseapp    md5

# Reiniciar PostgreSQL
sudo systemctl restart postgresql

# Crear base de datos
sudo -u postgres psql
CREATE DATABASE expense_note_app;
CREATE USER expenseapp WITH ENCRYPTED PASSWORD 'STRONG_PASSWORD';
GRANT ALL PRIVILEGES ON DATABASE expense_note_app TO expenseapp;
\q
```

### 3. Desplegar Backend

```bash
# Como usuario expenseapp
sudo su - expenseapp

# Clonar repositorio
git clone https://github.com/yourusername/ExpenseNoteApp.git
cd ExpenseNoteApp/backend-springboot

# Crear application-prod.properties
cat > src/main/resources/application-prod.properties << EOF
server.port=8080
spring.datasource.url=jdbc:postgresql://localhost:5432/expense_note_app
spring.datasource.username=expenseapp
spring.datasource.password=STRONG_PASSWORD
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
app.jwt.secret=PRODUCTION_SECRET_KEY_MINIMUM_32_CHARACTERS
app.jwt.expiration=3600000
logging.level.root=WARN
logging.level.io.sunbit.app=INFO
EOF

# Compilar
mvn clean package -DskipTests -Pprod

# Crear servicio systemd
sudo nano /etc/systemd/system/expenseapp-backend.service
```

```ini
[Unit]
Description=ExpenseNoteApp Backend
After=postgresql.service

[Service]
Type=simple
User=expenseapp
ExecStart=/usr/bin/java -jar /home/expenseapp/ExpenseNoteApp/backend-springboot/target/expensenoteapp-1.1.0.jar --spring.profiles.active=prod
Restart=on-failure
RestartSec=10

[Install]
WantedBy=multi-user.target
```

```bash
# Habilitar e iniciar servicio
sudo systemctl daemon-reload
sudo systemctl enable expenseapp-backend
sudo systemctl start expenseapp-backend
sudo systemctl status expenseapp-backend
```

### 4. Desplegar Frontend

```bash
# Compilar frontend
cd /home/expenseapp/ExpenseNoteApp/frontend

# Crear .env.production
cat > .env.production << EOF
VITE_API_URL=https://api.tudominio.com/api/v1
EOF

# Compilar
npm install
npm run build

# Copiar archivos a directorio web
sudo mkdir -p /var/www/expenseapp
sudo cp -r dist/* /var/www/expenseapp/
sudo chown -R www-data:www-data /var/www/expenseapp
```

### 5. Configurar Nginx

```bash
sudo nano /etc/nginx/sites-available/expenseapp
```

```nginx
# Backend (API)
server {
    listen 80;
    server_name api.tudominio.com;

    location / {
        proxy_pass http://localhost:8080;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host $host;
        proxy_cache_bypass $http_upgrade;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}

# Frontend
server {
    listen 80;
    server_name tudominio.com www.tudominio.com;

    root /var/www/expenseapp;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    # Cache static assets
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg)$ {
        expires 1y;
        add_header Cache-Control "public, immutable";
    }

    # Gzip compression
    gzip on;
    gzip_vary on;
    gzip_min_length 1024;
    gzip_types text/plain text/css text/xml text/javascript application/x-javascript application/xml+rss application/javascript application/json;
}
```

```bash
# Habilitar sitio
sudo ln -s /etc/nginx/sites-available/expenseapp /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl reload nginx
```

### 6. Configurar SSL con Let's Encrypt

```bash
# Obtener certificados SSL
sudo certbot --nginx -d tudominio.com -d www.tudominio.com
sudo certbot --nginx -d api.tudominio.com

# Renovación automática (ya está configurada)
sudo systemctl status certbot.timer
```

### 7. Configurar Firewall

```bash
# Configurar UFW
sudo ufw allow 22/tcp
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp
sudo ufw enable
sudo ufw status
```

---

## ☁️ Despliegue en Cloud

### AWS (Amazon Web Services)

#### Opción 1: EC2 + RDS

```bash
# 1. Crear RDS PostgreSQL instance
# 2. Crear EC2 instance (t3.medium)
# 3. Seguir pasos de "Despliegue en Producción"
# 4. Configurar Security Groups:
#    - Allow 22 (SSH)
#    - Allow 80 (HTTP)
#    - Allow 443 (HTTPS)
#    - Allow 8080 from Load Balancer only
```

#### Opción 2: Elastic Beanstalk

```bash
# Instalar EB CLI
pip install awsebcli

# Inicializar
eb init -p java-21 expenseapp-backend

# Crear environment
eb create expenseapp-prod

# Deploy
eb deploy
```

#### Opción 3: ECS (Docker)

```bash
# 1. Crear ECR repositories
aws ecr create-repository --repository-name expenseapp-backend
aws ecr create-repository --repository-name expenseapp-frontend

# 2. Push images
docker tag expenseapp-backend:latest <account-id>.dkr.ecr.<region>.amazonaws.com/expenseapp-backend:latest
docker push <account-id>.dkr.ecr.<region>.amazonaws.com/expenseapp-backend:latest

# 3. Crear ECS cluster y services usando AWS Console o CloudFormation
```

### Azure

```bash
# 1. Crear Resource Group
az group create --name expenseapp-rg --location eastus

# 2. Crear Azure Database for PostgreSQL
az postgres server create \
  --resource-group expenseapp-rg \
  --name expenseapp-db \
  --location eastus \
  --admin-user expenseapp \
  --admin-password STRONG_PASSWORD \
  --sku-name B_Gen5_1

# 3. Crear App Service
az appservice plan create \
  --name expenseapp-plan \
  --resource-group expenseapp-rg \
  --sku B1 \
  --is-linux

# 4. Deploy backend
az webapp create \
  --resource-group expenseapp-rg \
  --plan expenseapp-plan \
  --name expenseapp-backend \
  --runtime "JAVA:21-java21"

az webapp deploy \
  --resource-group expenseapp-rg \
  --name expenseapp-backend \
  --src-path target/expensenoteapp-1.1.0.jar
```

### Google Cloud Platform (GCP)

```bash
# 1. Crear Cloud SQL instance
gcloud sql instances create expenseapp-db \
  --database-version=POSTGRES_14 \
  --tier=db-f1-micro \
  --region=us-central1

# 2. Deploy en App Engine
gcloud app deploy

# O en Cloud Run (Docker)
gcloud run deploy expenseapp-backend \
  --image gcr.io/PROJECT_ID/expenseapp-backend \
  --platform managed \
  --region us-central1
```

### Heroku

```bash
# 1. Login
heroku login

# 2. Crear app
heroku create expenseapp-backend

# 3. Agregar PostgreSQL
heroku addons:create heroku-postgresql:hobby-dev

# 4. Deploy
git push heroku main

# 5. Configurar variables de entorno
heroku config:set JWT_SECRET=your-secret-key
```

---

## ⚙️ Configuración de Entornos

### Variables de Entorno por Ambiente

#### Desarrollo (local)
```properties
ENV=development
DB_HOST=localhost
DB_PORT=5432
JWT_EXPIRATION=86400000  # 24 horas
LOG_LEVEL=DEBUG
```

#### Staging
```properties
ENV=staging
DB_HOST=staging-db.internal
DB_PORT=5432
JWT_EXPIRATION=7200000  # 2 horas
LOG_LEVEL=INFO
```

#### Producción
```properties
ENV=production
DB_HOST=prod-db.internal
DB_PORT=5432
JWT_EXPIRATION=3600000  # 1 hora
LOG_LEVEL=WARN
```

### Gestión de Secrets

```bash
# Usando variables de entorno
export JWT_SECRET=$(openssl rand -base64 32)
export DB_PASSWORD=$(openssl rand -base64 16)

# Usando archivos de secrets (Docker)
docker secret create jwt_secret jwt_secret.txt
docker secret create db_password db_password.txt

# Usando AWS Secrets Manager
aws secretsmanager create-secret \
  --name expenseapp/jwt-secret \
  --secret-string "your-secret-key"
```

---

## 📊 Monitoreo y Mantenimiento

### Health Checks

```bash
# Backend health
curl http://localhost:8080/actuator/health

# Database health
pg_isready -h localhost -U expenseapp
```

### Logs

```bash
# Backend logs (systemd)
sudo journalctl -u expenseapp-backend -f

# Backend logs (file)
tail -f /var/log/expenseapp/application.log

# Nginx logs
tail -f /var/log/nginx/access.log
tail -f /var/log/nginx/error.log

# PostgreSQL logs
tail -f /var/log/postgresql/postgresql-14-main.log
```

### Métricas

```bash
# Spring Boot Actuator endpoints
curl http://localhost:8080/actuator/metrics
curl http://localhost:8080/actuator/metrics/jvm.memory.used
curl http://localhost:8080/actuator/metrics/http.server.requests
```

### Alertas

Configurar alertas para:
- CPU > 80%
- Memoria > 90%
- Disco > 85%
- Error rate > 1%
- Response time > 2s

---

## 💾 Respaldos y Recuperación

### Backup Automático de PostgreSQL

```bash
# Script de backup
cat > /usr/local/bin/backup-expenseapp.sh << 'EOF'
#!/bin/bash
BACKUP_DIR=/var/backups/expenseapp
DATE=$(date +%Y%m%d_%H%M%S)
mkdir -p $BACKUP_DIR

# Backup database
pg_dump -U expenseapp expense_note_app | gzip > $BACKUP_DIR/db_backup_$DATE.sql.gz

# Mantener solo últimos 7 días
find $BACKUP_DIR -name "db_backup_*.sql.gz" -mtime +7 -delete

# Sync to S3 (opcional)
# aws s3 sync $BACKUP_DIR s3://expenseapp-backups/
EOF

chmod +x /usr/local/bin/backup-expenseapp.sh

# Agregar a cron (diario a las 2 AM)
echo "0 2 * * * /usr/local/bin/backup-expenseapp.sh" | sudo crontab -
```

### Restaurar Backup

```bash
# Restaurar desde backup
gunzip < db_backup_20241206_020000.sql.gz | psql -U expenseapp expense_note_app
```

### Disaster Recovery Plan

1. **RPO (Recovery Point Objective)**: 24 horas
2. **RTO (Recovery Time Objective)**: 4 horas

**Pasos**:
1. Restaurar base de datos desde backup
2. Desplegar última versión estable del código
3. Verificar integridad de datos
4. Probar funcionalidades críticas
5. Redirigir tráfico

---

## 🔄 Actualización de Versiones

### Rolling Update

```bash
# 1. Backup database
pg_dump -U expenseapp expense_note_app > backup_pre_update.sql

# 2. Pull nueva versión
cd /home/expenseapp/ExpenseNoteApp
git pull origin main

# 3. Compilar backend
cd backend-springboot
mvn clean package -DskipTests

# 4. Compilar frontend
cd ../frontend
npm run build
sudo cp -r dist/* /var/www/expenseapp/

# 5. Restart backend
sudo systemctl restart expenseapp-backend

# 6. Verificar
curl http://localhost:8080/actuator/health

# 7. Reload nginx
sudo systemctl reload nginx
```

### Blue-Green Deployment

```bash
# Mantener dos versiones:
# - Blue: Versión actual en producción
# - Green: Nueva versión

# 1. Deploy green version en puerto diferente (8081)
# 2. Probar green version
# 3. Cambiar Nginx para apuntar a green
# 4. Si hay problemas, revertir a blue
```

---

## ✅ Checklist de Despliegue

### Pre-Deployment
- [ ] Código revisado y probado
- [ ] Tests pasan exitosamente
- [ ] Documentación actualizada
- [ ] Variables de entorno configuradas
- [ ] Secrets generados y guardados
- [ ] Backup de base de datos actual

### Deployment
- [ ] Build exitoso
- [ ] Base de datos migrada
- [ ] Servicios iniciados correctamente
- [ ] Health checks pasan
- [ ] SSL configurado (producción)
- [ ] Firewall configurado

### Post-Deployment
- [ ] Smoke tests exitosos
- [ ] Logs sin errores críticos
- [ ] Métricas monitoreadas
- [ ] Equipo notificado
- [ ] Documentación de deployment actualizada
- [ ] Plan de rollback preparado

---

## 📞 Soporte

Para más información:
- [INDEX.md](./INDEX.md)
- [TROUBLESHOOTING.md](./TROUBLESHOOTING.md)
- [ARCHITECTURE.md](./ARCHITECTURE.md)

---

**Última actualización**: Diciembre 2024  
**Versión**: 1.0.0  
**Mantenido por**: ExpenseNoteApp Team
