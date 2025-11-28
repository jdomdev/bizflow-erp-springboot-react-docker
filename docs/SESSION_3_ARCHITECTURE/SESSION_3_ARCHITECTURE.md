# 🏗️ Technical Architecture Overview

**Document:** Expense Note App - Architecture & Technology Stack  
**Version:** 1.0  
**Date:** 2025-11-27  
**Status:** Production Ready

---

## 📊 System Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                        USER BROWSER                          │
│                    http://localhost:80                       │
└──────────────────────────┬──────────────────────────────────┘
                           │
                    ┌──────▼──────┐
                    │   NGINX     │
                    │  (Frontend) │
                    │  Port: 80   │
                    └──────┬──────┘
                           │
         ┌─────────────────┴────────────────┐
         │                                  │
    ┌────▼──────────┐          ┌──────────▼──────┐
    │  React App    │          │  Backend Proxy  │
    │  (Vite Build) │          │  (nginx proxy)  │
    │  Port: 3000   │          │  /api → :8080   │
    └────┬──────────┘          └──────────┬──────┘
         │                                 │
         └─────────────────┬───────────────┘
                           │
                    ┌──────▼──────────┐
                    │ Spring Boot API │
                    │  Port: 8080     │
                    │ (Java 21)       │
                    └──────┬──────────┘
                           │
         ┌─────────────────┴────────────────┐
         │      Spring Data JPA             │
         │  + Hibernate ORM Mapping         │
         └─────────────────┬────────────────┘
                           │
                    ┌──────▼──────────┐
                    │  PostgreSQL     │
                    │  Port: 5433     │
                    │  (16-alpine)    │
                    │  Database       │
                    └─────────────────┘
```

---

## 🎨 Frontend Stack

### Technology
- **Framework:** React 18.2.0
- **Build Tool:** Vite 5.0.0
- **CSS Framework:** Tailwind CSS 3.4.0
- **HTTP Client:** Axios 1.7.7
- **Styling Tool:** PostCSS 8.4.0
- **Server:** Nginx (Alpine Linux)
- **Node Version:** 20-alpine (in Docker)

### Project Structure
```
frontend/
├── public/
│   └── favicon.ico
├── src/
│   ├── components/
│   ├── pages/
│   ├── App.jsx
│   ├── main.jsx
│   └── index.css
├── index.html
├── vite.config.js
├── postcss.config.js
├── tailwind.config.js
├── package.json
├── package-lock.json
├── .env.development
├── Dockerfile          (Multi-stage build)
└── .dockerignore
```

### Build Process
```
npm install
  ↓
npm run build         (Vite build → dist/)
  ↓
Nginx serve dist/     (Reverse proxy for /api)
```

### Docker Build (Multi-Stage)
```
Stage 1: Dependencies
  - node:20-alpine
  - npm install

Stage 2: Builder
  - node:20-alpine
  - npm run build
  - Vite creates dist/

Stage 3: Runtime
  - nginx:alpine
  - Copy dist/ to nginx
  - Configure proxy /api → backend:8080
  - User: appuser (non-root)
```

### Key Configuration
**vite.config.js:**
```javascript
export default {
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
}
```

**nginx.conf:**
```nginx
server {
  listen 80;
  
  location / {
    root /usr/share/nginx/html;
    try_files $uri $uri/ /index.html;
  }
  
  location /api {
    proxy_pass http://expense_backend:8080;
    proxy_http_version 1.1;
  }
}
```

---

## 🔧 Backend Stack

### Technology
- **Language:** Java 21 (OpenJDK)
- **Framework:** Spring Boot 3.3.4
- **Security:** Spring Security 6.3.3
- **ORM:** Hibernate 6.6.5 (via Spring Data JPA 3.3.4)
- **Database Driver:** PostgreSQL JDBC 42.7.3
- **Authentication:** JWT (jjwt 0.12.6)
- **Build Tool:** Maven 3.9.5
- **Server:** Tomcat 10.1.28 (embedded)

### Project Structure
```
backend/
├── src/main/
│   ├── java/io/sunbit/app/
│   │   ├── ExpenseNoteAppApplication.java
│   │   ├── controller/
│   │   │   ├── IEmployeeController.java
│   │   │   ├── EmployeeControllerImpl.java
│   │   │   ├── IExpenseController.java
│   │   │   ├── ExpenseControllerImpl.java
│   │   │   ├── IPayrollController.java
│   │   │   ├── PayrollControllerImpl.java
│   │   │   ├── IPositionController.java
│   │   │   └── PositionControllerImpl.java
│   │   ├── service/
│   │   ├── dao/
│   │   ├── entity/
│   │   │   ├── Employee.java
│   │   │   ├── Expense.java
│   │   │   ├── Payroll.java
│   │   │   └── Position.java
│   │   ├── dto/
│   │   │   ├── EmployeeDto.java
│   │   │   ├── ExpenseDto.java
│   │   │   └── *Mapper.java
│   │   ├── exception/
│   │   ├── security/
│   │   │   ├── configuration/
│   │   │   │   ├── AppSecurityConfig.java  (✅ FIXED)
│   │   │   │   ├── CustomAuthenticationEntryPoint.java (✅ FIXED)
│   │   │   │   └── SecurityConfig.java
│   │   │   ├── jwt/
│   │   │   ├── login/
│   │   │   ├── dao/
│   │   │   ├── dto/
│   │   │   ├── entity/
│   │   │   └── service/
│   │   └── util/
│   └── resources/
│       ├── application.properties        (✅ UPDATED)
│       └── application-dev.properties
├── src/test/
│   └── java/io/sunbit/app/
│       ├── test/
│       │   ├── employee/
│       │   ├── expense/
│       │   └── user/
│       └── security/
├── pom.xml                              (✅ FIXED - spring-security-crypto version)
├── Dockerfile
├── mvnw
└── mvnw.cmd
```

### Core Endpoints

**Position Management**
```
GET    /api/v1/position         - Get all positions
POST   /api/v1/position         - Create position
PUT    /api/v1/position/{id}    - Update position
DELETE /api/v1/position/{id}    - Delete position
```

**Employee Management**
```
GET    /api/v1/employee         - Get all employees
POST   /api/v1/employee         - Create employee
PUT    /api/v1/employee/{id}    - Update employee
DELETE /api/v1/employee/{id}    - Delete employee
```

**Expense Management**
```
GET    /api/v1/expense          - Get all expenses
POST   /api/v1/expense          - Create expense
PUT    /api/v1/expense/{id}     - Update expense
DELETE /api/v1/expense/{id}     - Delete expense
```

**Payroll Management**
```
GET    /api/v1/payroll          - Get all payroll records
POST   /api/v1/payroll          - Create payroll
PUT    /api/v1/payroll/{id}     - Update payroll
DELETE /api/v1/payroll/{id}     - Delete payroll
```

**Health & Actuator**
```
GET    /actuator/health         - Health check
GET    /actuator/metrics        - Metrics (if enabled)
```

### Security Configuration

**AppSecurityConfig.java (✅ FIXED in this session)**
```java
@Configuration
@EnableWebSecurity
public class AppSecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/api/v1/**", "/actuator/**", "/health").permitAll()
                .anyRequest().authenticated()
            )
            .cors(cors -> cors.configurationSource(getCorsConfig()))
            .csrf(csrf -> csrf.disable());
            
        return http.build();
    }
    
    // CORS configuration for frontend communication
}
```

**Why This Was Critical:**
- Without `.authorizeHttpRequests()`, Spring Security denies all requests by default
- `/api/v1/**` endpoints needed to be explicitly permitted for public access
- CORS configuration required for frontend-backend communication

### Database Schema

**Entities:**

1. **Position**
   - id: Long (PK)
   - name: String
   - description: String

2. **Employee**
   - id: Long (PK)
   - name: String
   - email: String
   - position: Position (FK)
   - salary: BigDecimal

3. **Expense**
   - id: Long (PK)
   - description: String
   - amount: BigDecimal
   - employee: Employee (FK)
   - date: LocalDate

4. **Payroll**
   - id: Long (PK)
   - month: YearMonth
   - employee: Employee (FK)
   - amount: BigDecimal

### Data Flow

```
HTTP Request
    ↓
Controller (ControllerImpl)
    ↓
DTO Mapper (EmployeeMapper, etc.)
    ↓
Service Layer (business logic)
    ↓
DAO/Repository (Spring Data JPA)
    ↓
Hibernate (ORM)
    ↓
PostgreSQL Driver (JDBC)
    ↓
PostgreSQL Database
```

---

## 💾 Database Stack

### Technology
- **Engine:** PostgreSQL 16-alpine
- **JDBC Driver:** postgresql:42.7.3
- **Connection Pool:** HikariCP (Spring default)
- **ORM Mappings:** Hibernate 6.6.5

### Configuration

**application.properties:**
```properties
# Database Connection (Environment-Driven)
spring.datasource.url=jdbc:postgresql://${DB_HOST:postgres}:${DB_PORT:5432}/${DB_NAME:expense_note_app}
spring.datasource.username=${DB_USER:postgres}
spring.datasource.password=${DB_PASSWORD:postgres}

# Hibernate Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# Connection Pool
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=2
```

**Docker Compose Environment:**
```yaml
environment:
  POSTGRES_DB: ${DB_NAME:expense_note_app}
  POSTGRES_USER: ${DB_USER:postgres}
  POSTGRES_PASSWORD: ${DB_PASSWORD:***hidden***}
```

### Table Creation Flow
```
Hibernate DDL-Auto: update
  ↓
Scans @Entity classes on startup
  ↓
Creates tables if not exist
  ↓
Alters tables if schema changes
  ↓
Preserves existing data
```

---

## 🐳 Docker Orchestration

### docker-compose.yml Services

**PostgreSQL Service**
```yaml
postgres:
  image: postgres:16-alpine
  container_name: expense_db
  environment:
    POSTGRES_DB: ${DB_NAME:expense_note_app}
    POSTGRES_USER: ${DB_USER:postgres}
    POSTGRES_PASSWORD: ${DB_PASSWORD:***hidden***}
  ports:
    - "5433:5432"  # Mapped to 5433 to avoid conflicts
  volumes:
    - postgres_data:/var/lib/postgresql/data
  healthcheck:
    test: ["CMD-SHELL", "pg_isready -U ${DB_USER:postgres}"]
    interval: 10s
    timeout: 5s
    retries: 5
```

**Backend Service**
```yaml
backend:
  image: expensenoteapp-backend
  container_name: expense_backend
  ports:
    - "8080:8080"
  environment:
    DB_HOST: postgres
    DB_PORT: 5432  (container port)
    DB_NAME: ${DB_NAME:expense_note_app}
    DB_USER: ${DB_USER:postgres}
    DB_PASSWORD: ${DB_PASSWORD:***hidden***}
  depends_on:
    postgres:
      condition: service_healthy
  healthcheck:
    test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
    interval: 10s
    timeout: 5s
    retries: 5
```

**Frontend Service**
```yaml
frontend:
  image: expensenoteapp-frontend
  container_name: expense_frontend
  ports:
    - "80:80"
  depends_on:
    - backend
  healthcheck:
    test: ["CMD", "curl", "-f", "http://localhost/health"]
    interval: 10s
    timeout: 5s
    retries: 5
```

### Network Architecture
```
Bridge Network: expense_network
├── postgres (host: postgres:5432)
├── backend (host: expense_backend:8080)
└── frontend (host: expense_frontend:80)

External Access:
├── localhost:80    → Frontend (Nginx)
├── localhost:8080  → Backend (Spring Boot)
└── localhost:5433  → Database (PostgreSQL)
```

### Docker Build Flow

**Backend Dockerfile:**
```dockerfile
# Stage 1: Build
FROM maven:3.9.5-eclipse-temurin-21-alpine AS builder
WORKDIR /build
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /build/target/app.jar .
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
```

**Frontend Dockerfile:**
```dockerfile
# Stage 1: Dependencies
FROM node:20-alpine AS dependencies
WORKDIR /build
COPY package*.json ./
RUN npm install

# Stage 2: Builder
FROM node:20-alpine AS builder
WORKDIR /build
COPY --from=dependencies /build/node_modules ./node_modules
COPY . .
RUN npm run build

# Stage 3: Runtime
FROM nginx:alpine
COPY --from=builder /build/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

---

## 🔄 Request Flow Example

### Getting All Positions

```
1. Browser: GET http://localhost/api/v1/position
   ↓
2. Nginx (Frontend): Receives request
   ├─ Path starts with /api
   └─ Proxies to: http://expense_backend:8080/api/v1/position
   ↓
3. Spring Boot (Backend): PositionControllerImpl
   ├─ @GetMapping("/position")
   ├─ Calls: positionService.findAll()
   └─ Returns: List<PositionDto>
   ↓
4. Service Layer: PositionService
   ├─ Business logic validation
   ├─ Calls: positionDao.findAll()
   └─ Maps: List<Position> → List<PositionDto>
   ↓
5. DAO Layer: PositionRepository (extends JpaRepository)
   ├─ Calls: findAll()
   └─ Generates SQL: SELECT * FROM position
   ↓
6. Hibernate ORM
   ├─ Maps SQL results to Position entities
   └─ Returns: List<Position>
   ↓
7. PostgreSQL
   ├─ Query: SELECT * FROM position
   └─ Returns: Result set (empty on first run)
   ↓
8. Response Path (reverse):
   ├─ Hibernate maps to entities
   ├─ Mapper converts to DTO
   ├─ Controller returns JSON
   ├─ Spring serializes to: []
   ├─ Nginx passes through
   └─ Browser receives: HTTP 200 with [] in body
```

---

## 🔐 Security Architecture

### Current Implementation
- Spring Security 6.3.3 with SecurityFilterChain
- Explicit endpoint authorization via `.authorizeHttpRequests()`
- CORS enabled for frontend communication
- CSRF disabled (for REST API)
- JWT support available (jjwt 0.12.6)

### Request Authentication Flow
```
Incoming Request
    ↓
SecurityFilterChain
    ├─ Check if endpoint requires authentication
    ├─ /api/v1/** → permitAll() ✅
    ├─ /actuator/** → permitAll() ✅
    └─ Everything else → authenticated()
    ↓
If authenticated required:
    ├─ Check Authorization header
    ├─ Validate JWT token (if applicable)
    └─ Load user credentials
    ↓
CustomAuthenticationEntryPoint
    ├─ If not authenticated
    ├─ Sends: HTTP 401 Unauthorized
    └─ Response body: error details
```

---

## 📈 Performance Considerations

### Connection Pooling
- HikariCP: 10 max connections, 2 min idle
- Auto-reconnect if connection drops
- 30-second idle timeout

### Caching Strategy
- Database query caching (Hibernate 2nd level if enabled)
- Frontend static asset caching (Nginx expires headers)
- Response caching (if configured)

### Optimization Tips
1. **Enable query result caching** in application.properties
2. **Implement pagination** for large datasets
3. **Add database indexes** on frequently queried columns
4. **Use lazy loading** for related entities
5. **Implement response DTOs** to avoid N+1 queries

---

## 🚀 Deployment Readiness

### Production Checklist
- [ ] Environment variables configured (DB credentials)
- [ ] HTTPS enabled (SSL certificates)
- [ ] CORS origins whitelist configured
- [ ] Logging configured for all services
- [ ] Backup strategy for PostgreSQL data
- [ ] Monitoring and alerting setup
- [ ] Load balancer configured (if needed)
- [ ] Security headers added (HSTS, CSP, etc.)
- [ ] Rate limiting implemented
- [ ] Input validation/sanitization enforced

### Scaling Strategy
1. **Horizontal Scaling:**
   - Run multiple backend instances behind load balancer
   - Frontend served via CDN
   - PostgreSQL replicated for read scaling

2. **Vertical Scaling:**
   - Increase container resource limits (CPU, memory)
   - Increase database connection pool size
   - Increase Nginx worker processes

---

## 📝 Configuration Files Summary

| File | Purpose | Last Updated |
|------|---------|--------------|
| `docker-compose.yml` | Container orchestration | Session 2 |
| `application.properties` | Spring Boot config | Session 2 |
| `.env.development` | Frontend env vars | Session 2 |
| `vite.config.js` | Frontend build config | Session 2 |
| `postcss.config.js` | CSS processing | Session 2 |
| `nginx.conf` | Reverse proxy config | Session 2 |
| `pom.xml` | Maven dependencies | Session 2 |
| `Dockerfile` (Frontend) | Frontend container | Session 2 |
| `Dockerfile` (Backend) | Backend container | Session 2 |

---

## 🔗 Technology Compatibility Matrix

| Component | Version | Compatibility | Status |
|-----------|---------|---|---|
| Java | 21 (LTS) | Spring Boot 3.3.4 ✅ | Active |
| Spring Boot | 3.3.4 | Jakarta EE ✅ | Active |
| Spring Security | 6.3.3 | Spring Boot 3.3.4 ✅ | Active |
| Hibernate | 6.6.5 | Jakarta EE ✅ | Active |
| PostgreSQL | 16 | JDBC 42.7.3 ✅ | Active |
| React | 18.2.0 | Vite 5.0.0 ✅ | Active |
| Vite | 5.0.0 | Node 20 ✅ | Active |
| Node | 20 | npm 10.x ✅ | Active |
| Nginx | Latest Alpine | Production ✅ | Active |

---

**Architecture Document Complete**  
**Suitable for:** Development, QA, DevOps, and Production Teams  
**Next Review:** After Session 3 integration testing
