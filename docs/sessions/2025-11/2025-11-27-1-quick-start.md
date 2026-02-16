**Fecha:** 2025-11-27

# 🚀 Quick Start Guide - Expense Note App (Session 3)

**Estado:** ✅ Todos los servicios en ejecución y saludables  
**Fecha de creación:** 2025-11-27  
**Sesión:** 2 - Correcciones de autorización de API completadas

---

## 🟢 Estado actual de la aplicación

Todos los servicios de Docker están **en ejecución y saludables**:

```
✅ expense_db       → PostgreSQL 16-alpine (puerto 5433)
✅ expense_backend  → Spring Boot 8080
✅ expense_frontend → React + Nginx (puerto 80)
```

---

## 🌐 Puntos de acceso para mañana

### Frontend (Aplicación React)
```
📍 URL: http://localhost
🎨 Aplicación: Expense Note App - Control de Gastos
⚠️ Nota: La base de datos está vacía (no se ha creado ningún dato)
```

### Backend API (Spring Boot)
```
📍 URL base: http://localhost:8080
📊 Endpoints de la API:
  - GET /api/v1/position  → Datos de puestos (arreglo vacío)
  - GET /api/v1/employee  → Datos de empleados (arreglo vacío)
  - GET /api/v1/expense   → Datos de gastos (arreglo vacío)
  - GET /api/v1/payroll   → Datos de nómina (arreglo vacío)

🏥 Chequeo de salud:
  - GET /actuator/health  → {"status":"UP"}
```

### Base de datos (PostgreSQL)
```
📍 Host: localhost
🔌 Puerto: 5433
💾 Base de datos: expense_note_app
👤 Usuario: ${DB_USER} (desde variables de entorno)
🔐 Contraseña: ${DB_PASSWORD} (desde variables de entorno)

🔗 Cadena de conexión:
   jdbc:postgresql://localhost:5433/${DB_NAME}
```

---

## 🎯 Tareas para mañana (Sesión 3)

### Fase 1: Interacción con el frontend (empieza aquí) 🎨
1. **Abre el navegador y navega a:** `http://localhost`
2. **Explora la interfaz:**
   - Comprueba si aparece la pantalla de inicio de sesión (si se requiere autenticación)
   - Navega por las pantallas disponibles
   - Busca errores en la consola de JavaScript (F12 → pestaña Console)
   - Prueba el diseño responsive (redimensiona la ventana)

### Fase 2: Población de datos 📊
1. **Crea datos de prueba mediante la API o el frontend**
2. **Opción A: Usa la interfaz del frontend (si está disponible)**
   - Navega a "Añadir puesto", "Añadir empleado", etc.
   - Crea datos de ejemplo
   
3. **Opción B: Usa curl/Postman (si los endpoints están expuestos)**
   ```bash
   # Ejemplo: crear un puesto
   curl -X POST http://localhost:8080/api/v1/position \
     -H "Content-Type: application/json" \
     -d '{"name":"Senior Developer","description":"Experienced developer"}'
   
   # Ejemplo: obtener todos los puestos
   curl http://localhost:8080/api/v1/position
   ```

### Fase 3: Ejecutar pruebas ✅
```bash
cd /home/bytetech/code/java/ExpenseNoteApp

# Ejecutar pruebas del backend
docker-compose exec backend bash -c "cd . && mvn test"

# Ver resultados de las pruebas
# Estarán en: build/backend/surefire-reports/
```

### Fase 4: Crear Pull Request 🔀
1. Crea PR: `fix/api-endpoint-authorization` → `dev`
2. Añade el documento de migración como descripción de la PR
3. Solicita revisión de código

---

## 🐳 Referencia de comandos Docker

### Comprobar estado
```bash
# Ver todos los contenedores
docker-compose ps

# Ver logs detallados
docker-compose logs -f backend    # Logs del backend (tiempo real)
docker-compose logs -f frontend   # Logs del frontend
docker-compose logs -f postgres   # Logs de la base de datos

# Estado completo de la aplicación
docker-compose ps --all
```

### Detener/iniciar servicios
```bash
# Detener todos los servicios (mantiene los datos)
docker-compose down

# Iniciar todos los servicios de nuevo
docker-compose up -d

# Reinicio completo
docker-compose restart

# Reconstrucción completa (si es necesario, tarda ~2-3 minutos)
docker-compose down && docker-compose build --no-cache && docker-compose up -d
```

### Acceder a contenedores
```bash
# Acceder a la shell del backend
docker-compose exec backend bash

# Acceder a la base de datos
docker-compose exec postgres psql -U postgres -d expense_note_app

# Ver archivos dentro del contenedor
docker-compose exec backend ls -la /app/

# Ejecutar comandos Maven en el backend
docker-compose exec backend mvn clean test
```

### Inspección de la base de datos
```bash
# Conectarse a la base de datos
docker-compose exec postgres psql -U postgres -d expense_note_app

# Una vez conectado, comandos útiles:
\dt                    # Listar todas las tablas
\d table_name          # Describir la estructura de una tabla
SELECT * FROM table;   # Consultar una tabla
```

---

## 🔍 Resolución de problemas

### ¿Los servicios no arrancan?
```bash
# Comprueba que el daemon de Docker esté funcionando
docker ps

# Elimina contenedores detenidos e inténtalo de nuevo
docker-compose down -v    # -v elimina también los volúmenes
docker-compose up -d

# Comprueba conflictos de puertos
lsof -i :80      # Verifica si el puerto 80 está en uso
lsof -i :8080    # Verifica si el puerto 8080 está en uso
lsof -i :5433    # Verifica si el puerto 5433 está en uso
```

### ¿El frontend no carga?
```bash
# Comprueba el servicio de frontend
docker-compose logs frontend

# Verifica que nginx esté ejecutándose
docker-compose exec frontend ps aux | grep nginx

# Reinicia el frontend
docker-compose restart frontend
```

### ¿Los endpoints de la API devuelven error?
```bash
# Comprueba el servicio de backend
docker-compose logs backend

# Verifica si el backend responde
curl -v http://localhost:8080/actuator/health

# Comprueba la conexión con la base de datos
docker-compose exec backend grep "Database connection" -i logs | tail -20
```

### ¿Problemas de conexión con la base de datos?
```bash
# Verifica que la base de datos esté en ejecución
docker-compose exec postgres pg_isready

# Prueba la conexión
docker-compose exec postgres psql -U postgres -d expense_note_app -c "SELECT 1"

# Revisa si se crearon las tablas
docker-compose exec postgres psql -U postgres -d expense_note_app -c "\dt"
```

---

## 📝 Notas importantes

### ¡Los datos vacíos son normales!
- Los endpoints de la API devuelven `[]` (arreglos vacíos) porque la base de datos aún no tiene datos
- Esto es **ESPERADO** en la primera ejecución
- Debes:
  1. Poblar datos mediante la interfaz del frontend
  2. O usar los endpoints de la API para hacer POST
  3. O importar datos iniciales desde scripts SQL

### Archivos de configuración
- **Configuración del backend:** `backend/src/main/resources/application.properties`
- **Docker Compose:** `docker-compose.yml`
- **Configuración del frontend:** `frontend/vite.config.js`, `frontend/.env.development`

### Información de ramas
- **Rama actual:** `fix/api-endpoint-authorization` (5 commits por delante de dev)
- **Estado:** Lista para PR
- **Estado de pruebas:** ✅ Todas las APIs verificadas

---

## 📚 Archivos de referencia

Estos documentos contienen información detallada:

1. **Documento de migración:** [docs/sessions/2025-11/2025-11-26-2-migration.md](2025-11-26-2-migration.md)
   - Resumen completo de la sesión
   - Todos los bugs corregidos
   - Todos los commits realizados
   - Visión general de la arquitectura

2. **Este archivo:** `QUICK_START_SESSION3.md`
   - Referencia rápida para mañana
   - Comandos y puntos de acceso
   - Lista de tareas

---

## 🎬 Comandos rápidos para la sesión 3

```bash
# Comprobar si está en ejecución (desde la raíz del proyecto)
docker-compose ps

# Acceder al frontend
open http://localhost  # o: firefox http://localhost

# Probar la API
curl http://localhost:8080/api/v1/position

# Crear un puesto mediante la API
curl -X POST http://localhost:8080/api/v1/position \
  -H "Content-Type: application/json" \
  -d '{"name":"Manager"}'

# Ver logs del backend
docker-compose logs backend | tail -50

# Ejecutar pruebas
docker-compose exec backend mvn test
```

---

**¡Sesión 3 lista!** 🚀  
Todos los servicios configurados, probados y listos para la interacción.  
Consulta [docs/sessions/2025-11/2025-11-26-2-migration.md](2025-11-26-2-migration.md) para información detallada.
