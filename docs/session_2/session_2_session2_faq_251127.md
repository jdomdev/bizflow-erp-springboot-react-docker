# 🤔 FAQ - Respuestas a Preguntas Comunes

**Documento:** Frequently Asked Questions sobre Expense Note App  
**Creado:** 2025-11-27  
**Status:** Session 2 - Preguntas Aclaradas

---

## ❓ P1: ¿Las APIs devuelven arrays vacíos porque la BD está vacía?

### ✅ Respuesta: SÍ, exactamente

**¿Por qué sucede?**

La base de datos estaba **completamente nueva** cuando arrancamos los contenedores:

1. **Primer arranque de PostgreSQL:**
   - Crea el database `expense_note_app`
   - Pero SIN tablas

2. **Primer arranque de Spring Boot:**
   - Hibernate detecta que no hay tablas
   - Con configuración `spring.jpa.hibernate.ddl-auto=update`
   - Crea las tablas automáticamente basadas en las @Entity

3. **Resultado:**
   - Tablas creadas: ✅ Position, Employee, Expense, Payroll
   - Datos en tablas: ❌ Ninguno

**Por lo tanto:**
```bash
GET /api/v1/position   → HTTP 200 []     (tabla existe pero vacía)
GET /api/v1/employee   → HTTP 200 []     (tabla existe pero vacía)
GET /api/v1/expense    → HTTP 200 []     (tabla existe pero vacía)
GET /api/v1/payroll    → HTTP 200 []     (tabla existe pero vacía)
```

### ✅ Esto es NORMAL y ESPERADO

**No hay error.** Solo significa:
- ✅ La aplicación está funcionando correctamente
- ✅ La BD se conectó correctamente
- ✅ Hibernate creó las tablas correctamente
- ✅ El API devuelve JSON válido
- ⏳ Solo hace falta **poblar datos**

---

## ✅ Verificación: Confirmar que las tablas existen

### Opción 1: Conectarse directamente a PostgreSQL

```bash
# Conectarse a la base de datos
docker-compose exec postgres psql -U postgres -d expense_note_app

# Dentro de PostgreSQL, ejecutar:
\dt                    # Listar todas las tablas (should show 4 tables)

# Ver estructura de una tabla
\d position            # Mostrar columnas y tipos de position

# Ver datos
SELECT COUNT(*) FROM position;     # Debe retornar 0
SELECT COUNT(*) FROM employee;     # Debe retornar 0
```

### Opción 2: Usar el backend para consultar (Tomorrow)

```bash
# Verificar logs del backend (buscar Hibernate DDL)
docker-compose logs backend | grep "create table"

# El output mostrará:
# Hibernate: create table employee (...) if not exists
# Hibernate: create table expense (...) if not exists
# Hibernate: create table payroll (...) if not exists
# Hibernate: create table position (...) if not exists
```

---

## 🎯 Plan para Mañana: Poblar Datos

Hay **3 opciones** para agregar datos:

### Opción A: Usar el Frontend (RECOMENDADO si UI existe)

```
1. Navegar a http://localhost
2. Buscar botones: "Add Position", "Add Employee", etc.
3. Llenar formulario
4. Enviar
5. El frontend hace POST /api/v1/position
6. Backend crea el registro
7. Los datos aparecen en las listas
```

**Ventaja:** Interfaz visual, validaciones en frontend  
**Desventaja:** Requiere que el frontend tenga estos formularios implementados

---

### Opción B: Usar curl/Postman (Para testing)

```bash
# 1. Crear una Position
curl -X POST http://localhost:8080/api/v1/position \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Senior Developer",
    "description": "Experienced software engineer"
  }'

# 2. Crear un Employee (requiere positionId)
curl -X POST http://localhost:8080/api/v1/employee \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Juan García",
    "email": "juan@example.com",
    "positionId": 1,
    "salary": 5000.00
  }'

# 3. Crear un Expense
curl -X POST http://localhost:8080/api/v1/expense \
  -H "Content-Type: application/json" \
  -d '{
    "description": "Viaje a cliente",
    "amount": 500.00,
    "employeeId": 1,
    "date": "2025-11-27"
  }'

# 4. Crear Payroll
curl -X POST http://localhost:8080/api/v1/payroll \
  -H "Content-Type: application/json" \
  -d '{
    "month": "2025-11",
    "employeeId": 1,
    "amount": 5000.00
  }'

# 5. Verificar que se creó
curl http://localhost:8080/api/v1/position
# Ahora retorna: [{"id":1,"name":"Senior Developer",...}]
```

**Ventaja:** Rápido, flexible, sin UI necesaria  
**Desventaja:** Requiere conocer la estructura del JSON

---

### Opción C: Usar Script SQL (Para inicialización masiva)

```bash
# 1. Crear archivo init.sql con datos
cat > /tmp/init.sql << 'EOF'
INSERT INTO position (name, description) VALUES 
  ('Senior Developer', 'Experienced engineer'),
  ('Project Manager', 'Manages projects'),
  ('QA Engineer', 'Tests applications');

INSERT INTO employee (name, email, position_id, salary) VALUES
  ('Juan García', 'juan@company.com', 1, 5000.00),
  ('María López', 'maria@company.com', 2, 4500.00),
  ('Carlos Ruiz', 'carlos@company.com', 3, 4000.00);

-- Add more data...
EOF

# 2. Ejecutar en PostgreSQL
docker-compose exec postgres psql -U postgres -d expense_note_app -f /tmp/init.sql
```

**Ventaja:** Datos iniciales permanentes, repetible  
**Desventaja:** Requiere SQL knowledge

---

## 🔄 Qué sucede después de poblar datos

Una vez agregues datos:

```bash
# ANTES (sin datos)
$ curl http://localhost:8080/api/v1/position
[]

# DESPUÉS de crear Position
$ curl http://localhost:8080/api/v1/position
[{
  "id": 1,
  "name": "Senior Developer",
  "description": "Experienced engineer"
}]

# DESPUÉS de crear Employee
$ curl http://localhost:8080/api/v1/employee
[{
  "id": 1,
  "name": "Juan García",
  "email": "juan@company.com",
  "positionId": 1,
  "salary": 5000.00
}]
```

---

## 🎮 Interacción con Frontend Mañana

### Pasos para Probar la UI

1. **Abrir en navegador:**
   ```
   http://localhost
   ```

2. **Ver qué hay:**
   - ¿Página de login?
   - ¿Dashboard?
   - ¿Formularios para crear datos?

3. **Interactuar:**
   - Hacer click en botones
   - Llenar formularios
   - Navegar entre páginas

4. **Verificar Backend:**
   - Abrir DevTools (F12)
   - Tab "Network"
   - Hacer acciones en UI
   - Ver qué URLs se llaman
   - Ver qué JSON se envía/recibe

5. **Verificar Datos:**
   ```bash
   # Después de crear datos en UI, consultar API
   curl http://localhost:8080/api/v1/employee
   
   # Debe retornar los datos que creaste
   ```

---

## 📊 Estado de BD - Resumen Técnico

### Tablas Creadas por Hibernate

```sql
-- Tabla: position
CREATE TABLE position (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(255),
  description TEXT
);

-- Tabla: employee
CREATE TABLE employee (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(255),
  email VARCHAR(255),
  position_id BIGINT REFERENCES position(id),
  salary NUMERIC(10,2)
);

-- Tabla: expense
CREATE TABLE expense (
  id BIGSERIAL PRIMARY KEY,
  description TEXT,
  amount NUMERIC(10,2),
  employee_id BIGINT REFERENCES employee(id),
  date DATE
);

-- Tabla: payroll
CREATE TABLE payroll (
  id BIGSERIAL PRIMARY KEY,
  month VARCHAR(7),
  employee_id BIGINT REFERENCES employee(id),
  amount NUMERIC(10,2)
);
```

### Configuración en application.properties

```properties
# Hibernate DDL Auto = update (crea tablas si no existen)
spring.jpa.hibernate.ddl-auto=update

# Esto significa:
# - Primer arranque: crea todas las tablas
# - Arranques siguientes: sincroniza cambios en entidades
# - NUNCA borra datos
# - NUNCA borra tablas
```

---

## 🚨 Posibles Problemas Mañana

### Problema 1: Frontend no carga

**Síntomas:** Página en blanco o error de conexión

**Soluciones:**
```bash
# Verificar que frontend está corriendo
docker-compose ps | grep frontend

# Ver logs del frontend
docker-compose logs frontend | tail -50

# Reiniciar frontend
docker-compose restart frontend

# Verificar que puerto 80 está disponible
lsof -i :80
```

---

### Problema 2: API devuelve error 500

**Síntomas:** POST /api/v1/position retorna error

**Soluciones:**
```bash
# Ver logs del backend
docker-compose logs backend | tail -50

# Verificar que backend está healthy
curl http://localhost:8080/actuator/health

# Conectarse al backend y ver detalles
docker-compose exec backend bash
cd / && java -version
```

---

### Problema 3: BD no guarda datos

**Síntomas:** POST retorna 200 pero después GET devuelve []

**Soluciones:**
```bash
# Verificar conexión a BD
docker-compose logs postgres | tail -20

# Conectarse a BD y verificar
docker-compose exec postgres psql -U postgres -d expense_note_app
SELECT COUNT(*) FROM employee;   # Debe retornar el número de registros

# Si retorna 0, el INSERT falló (revisar logs backend)
# Si retorna > 0, los datos SÍ se guardaron
```

---

## 📝 Checklist para Session 3

### ANTES de empezar
- [ ] Docker-compose running: `docker-compose ps`
- [ ] All services healthy (3 containers)
- [ ] Documentation files exist (3 markdown files created)

### Durante Session 3
- [ ] Abrir http://localhost en navegador
- [ ] Explorar UI del frontend
- [ ] Probar crear algunos datos (via UI o curl)
- [ ] Verificar que datos aparecen en GET endpoints
- [ ] Revisar DevTools network tab (F12)
- [ ] Revisar logs del backend (docker-compose logs backend)

### Antes de terminar Session 3
- [ ] Datos poblados en BD (al menos 5 registros)
- [ ] Frontend funcionando y mostrando datos
- [ ] Todos los endpoints probados (GET, POST, etc.)
- [ ] Tests unitarios ejecutados (mvn test)
- [ ] PR creado: fix/api-endpoint-authorization → dev
- [ ] Branch dev actualizado con fix

---

## 🎓 Aprendizajes Clave de Esta Session

### 1. Arrays Vacíos No Son Error
- Son respuesta válida HTTP 200
- Solo significa: tabla existe pero sin datos
- Completamente normal en primer arranque

### 2. Ciclo de Vida de Aplicación
```
Docker Compose Start
  ↓
PostgreSQL inicia (sin datos, sin tablas)
  ↓
Spring Boot inicia
  ↓
Hibernate inspecciona @Entity classes
  ↓
Hibernate ejecuta DDL-Auto (crea tablas)
  ↓
Aplicación lista para recibir requests
  ↓
APIs retornan [] hasta que se popule data
  ↓
POST endpoints crean datos
  ↓
GET endpoints retornan datos (ahora con información)
```

### 3. Data Flow
```
Browser → Nginx → Spring Boot → Hibernate → PostgreSQL
                                              ↓
                                   (tabla existe, vacía)
                                              ↓
                                   (busca SELECT)
                                              ↓
                                        (retorna vacío)
                                              ↓
Hibernate mapea (nada para mapear)
                                              ↓
Spring serializa a JSON: []
                                              ↓
Nginx proxied a navegador
                                              ↓
Browser recibe HTTP 200 []
```

---

## 📞 Preguntas Frecuentes - Resumen Rápido

| Pregunta | Respuesta |
|----------|-----------|
| ¿APIs devuelven [] porque BD vacía? | ✅ SÍ, es normal. Datos se agregan luego. |
| ¿Hay error en la app? | ❌ NO. Todo funciona perfecto. |
| ¿Cómo agrego datos? | 3 opciones: UI frontend, curl/Postman, o SQL |
| ¿Mañana qué hago? | Interactúa con frontend, prueba crear datos, ejecuta tests |
| ¿Dónde están los logs? | `docker-compose logs <service_name>` |
| ¿Cómo accedo a la BD? | `docker-compose exec postgres psql ...` |
| ¿Puedo borrar todo y empezar? | `docker-compose down -v && docker-compose up -d` |
| ¿La BD persiste entre reinicios? | ✅ SÍ (volume postgres_data) |
| ¿Qué significa ddl-auto=update? | Crea/actualiza tablas automáticamente sin borrar datos |
| ¿Cuándo hacer PR? | Después de tests Session 3 (aproximadamente) |

---

## 🎯 Conclusión

**La aplicación está funcionando perfectamente.** Los arrays vacíos son completamente normales y esperados. Mañana durante la Session 3:

1. Interactuarás con el frontend
2. Poblarás datos en la BD
3. Verás las APIs retornando datos reales
4. Ejecutarás los tests
5. Crearás la PR

**¡Todo listo para continuar mañana!** 🚀

---

**Documento:** FAQ - Session 2 Wrap-up  
**Última actualización:** 2025-11-27 23:XX  
**Siguiente:** Session 3 - Frontend Testing & Data Population
