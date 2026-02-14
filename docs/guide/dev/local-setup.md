# Configuración Local

Guía para configurar el entorno de desarrollo local sin Docker.

## Prerrequisitos

- **Java 17** (OpenJDK recomendado)
- **Node.js 18+** con npm
- **PostgreSQL 15+**
- **Maven 3.6+**
- **Git**

## Instalación de herramientas

### macOS

```bash
# Homebrew
brew install openjdk@17 node postgresql@15 maven

# Configurar Java
echo 'export JAVA_HOME=$(/usr/libexec/java_home -v 17)' >> ~/.zshrc
source ~/.zshrc
```

### Ubuntu/Debian

```bash
# Java
sudo apt update
sudo apt install openjdk-17-jdk

# Node.js (via NodeSource)
curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
sudo apt install -y nodejs

# PostgreSQL
sudo apt install postgresql postgresql-contrib

# Maven
sudo apt install maven
```

### Windows

Usar instaladores oficiales o `winget`:

```powershell
winget install Oracle.JDK.17
winget install OpenJS.NodeJS
winget install PostgreSQL.PostgreSQL
```

## Configurar Base de Datos

```bash
# Crear usuario y base de datos
sudo -u postgres psql

CREATE DATABASE erp_dev_db;
CREATE USER erp_user WITH PASSWORD 'tu_password';
GRANT ALL PRIVILEGES ON DATABASE erp_dev_db TO erp_user;
\q
```

## Clonar proyecto

```bash
git clone https://github.com/jdomdev/bizflow-erp-springboot-react-docker.git
cd bizflow-erp-springboot-react-docker
```

## Backend

### Configurar variables

Crear `backend/src/main/resources/application-local.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/erp_dev_db
spring.datasource.username=erp_user
spring.datasource.password=tu_password

jwt.secret=tu_clave_secreta_muy_larga_de_al_menos_32_caracteres
jwt.expiration=86400000
```

### Ejecutar backend

```bash
cd backend
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

El backend estará en http://localhost:8080

## Frontend

### Instalar dependencias

```bash
cd frontend
npm install
```

### Configurar API URL

Editar `frontend/.env`:

```env
VITE_API_URL=http://localhost:8080/api/v1
```

### Ejecutar frontend

```bash
npm run dev
```

El frontend estará en http://localhost:5173

## Poblar datos

```bash
cd scripts/seeds
pip install -r requirements.txt
python seed_runner.py --env dev
```

## IDE Recomendado

### VS Code

Extensiones recomendadas:
- Extension Pack for Java
- Spring Boot Extension Pack
- ES7+ React/Redux/React-Native
- Tailwind CSS IntelliSense
- Prettier

### IntelliJ IDEA

- Importar como proyecto Maven
- Configurar SDK Java 17
- Habilitar anotaciones Lombok
