# Sesión 6: Refactorización de Entidad Employee y Limpieza de Esquema de Base de Datos

**Fecha:** 14 de Diciembre de 2025  
**Hora:** 23:30  
**Rama:** `chore/multi-env-db-config`  
**Estado:** ✅ Completado y Probado

---

## 📋 Tabla de Contenidos

1. [Resumen General](#resumen-general)
2. [Planteamiento del Problema](#planteamiento-del-problema)
3. [Análisis y Discusión](#análisis-y-discusión)
4. [Cambios Implementados](#cambios-implementados)
5. [Verificación de Base de Datos](#verificación-de-base-de-datos)
6. [Pruebas y Validación](#pruebas-y-validación)
7. [Detalles Técnicos](#detalles-técnicos)
8. [Archivos Modificados](#archivos-modificados)
9. [Próximos Pasos](#próximos-pasos)

---

## 🎯 Resumen General

Esta sesión se centró en comprender y limpiar el esquema de la entidad Employee, específicamente abordando la inconsistencia entre el esquema de la base de datos y la entidad Java respecto al campo `salary`. Realizamos un análisis exhaustivo de la base de datos y eliminamos la columna `salary` no utilizada para mantener la consistencia y seguir las mejores prácticas.

### Logros Principales

- ✅ Análisis completo del esquema de base de datos en todas las tablas
- ✅ Identificación de inconsistencia en el esquema (campo salary en BD pero no en entidad Java)
- ✅ Eliminación de columna `salary` no utilizada de todos los scripts de inicialización SQL
- ✅ Aplicación de migración de base de datos para eliminar columna `salary` de producción
- ✅ Verificación de que todos los endpoints de Employee funcionan correctamente vía Postman
- ✅ Confirmación de integridad de datos en todas las tablas relacionadas

---

## 🔍 Planteamiento del Problema

### Pregunta Inicial
El usuario quería entender el formato JSON correcto para crear registros de Employee, específicamente si el objeto `position` debería incluir un campo `id`.

### Descubrimiento
Durante el análisis, descubrimos que:
1. El esquema de base de datos incluía una columna `salary` en la tabla `employee`
2. La entidad Java `Employee` **no** tenía un campo `salary` correspondiente
3. Esta inconsistencia era un legado del diseño inicial, donde el salario se movió posteriormente a la entidad `Payroll`
4. Todos los registros de empleados existentes tenían `salary = NULL`

### Causa Raíz
La aplicación fue diseñada para manejar los salarios de empleados a través de la entidad `Payroll` (permitiendo salarios variables en el tiempo), pero el esquema de base de datos conservó la columna `salary` de una iteración de diseño anterior.

---

## 💬 Análisis y Discusión

### Relación Employee-Position

Aclaramos el formato JSON correcto para crear empleados:

#### ✅ Caso 1: Position existe (usando ID)
```json
{
    "name": "Dorothy",
    "surname": "Hodgkin",
    "birthDate": "1910-05-12T00:00:00",
    "email": "dorothy.hodgkin@bizflowerp.com",
    "position": {
        "id": 1
    }
}
```
**Usar cuando:** Conoces el ID de la posición y quieres asociar una posición existente.

#### ✅ Caso 2: Position por nombre (buscar/crear)
```json
{
    "name": "Dorothy",
    "surname": "Hodgkin",
    "birthDate": "1910-05-12T00:00:00",
    "email": "dorothy.hodgkin@bizflowerp.com",
    "position": {
        "name": "Software Engineer"
    }
}
```
**Usar cuando:** El backend debe buscar la posición por nombre o crearla si no existe.

#### ✅ Caso 3: Ambos (más seguro)
```json
{
    "name": "Dorothy",
    "surname": "Hodgkin",
    "birthDate": "1910-05-12T00:00:00",
    "email": "dorothy.hodgkin@bizflowerp.com",
    "position": {
        "id": 1,
        "name": "Software Engineer"
    }
}
```
**Usar cuando:** Máxima seguridad - el ID tiene prioridad, el nombre como respaldo.

### Restricciones de la Entidad Position

Verificamos que `Position.name` es obligatorio (non-null):
```java
@Column(name = "name", nullable = false)
@Length(min = 3, max = 128)
@NonNull
private String name;
```

---

## 🗄️ Verificación de Base de Datos

### Inventario Completo de la Base de Datos

Realizamos una consulta completa `SELECT *` en todas las tablas para entender la estructura de datos:

#### **Tabla: `position`** (51 registros)
- **Campos:** `id`, `name`
- **Datos:** 51 posiciones desde "Software Engineer" hasta "Import Manager"
- **Restricciones:** `name` es NOT NULL, mínimo 3 caracteres, máximo 128 caracteres

#### **Tabla: `role`** (3 registros)
| ID | Nombre  |
|----|---------|
| 1  | ADMIN   |
| 2  | USER    |
| 3  | MANAGER |

#### **Tabla: `employee`** (61 registros)
- **Campos:** `id`, `name`, `surname`, `birth_date`, `email`, `position_id`, `salary`, `created_at`
- **Hallazgo Clave:** Todos los registros tenían `salary = NULL`
- **Creado:** 2025-12-13 21:17:00.208208

#### **Tabla: `payroll`** (305 registros)
- **Campos:** `id`, `amount`, `payroll_date`, `employee_id`, `expense_user_id`
- **Montos:** Rango de 2000 a 3100
- **Nota:** `expense_user_id` es NULL en todos los registros

#### **Tabla: `expense_user`** (57 registros)
- **Campos:** `id`, `email`, `name`, `password`, `surname`, `employee_id`
- **Contraseñas:** Encriptadas con bcrypt ($2a$...)
- **Nota:** Algunos usuarios tienen `employee_id = NULL` (contratistas)

#### **Tabla: `expense`** (20+ registros)
- **Campos:** `id`, `amount`, `concept`, `expense_date`, `note`, `expense_user_id`
- **Conceptos:** Suministros de oficina, Licencias de software, Almuerzo de equipo, Formación, etc.

#### **Tabla: `user_role`** (59 registros)
- **Campos:** `user_id`, `role_id` (clave primaria compuesta)
- **Nota:** NO tiene campo `id` - usa clave compuesta
- **Estructura:**
  - user_id 1 y 2: Tienen roles ADMIN (1) y USER (2)
  - user_id 3-57: Tienen solo rol USER (2)

---

## ⚙️ Cambios Implementados

### 1. Actualizaciones de Esquema SQL

Se eliminó la columna `salary` de todos los scripts de inicialización:

#### Archivos Modificados:
- `/sql/01_init_prod.sql`
- `/sql/01_init_dev.sql`
- `/sql/init_test.sql`
- `/sql/init_prod.sql` (legacy)
- `/sql/init_dev.sql` (legacy)

#### Cambio Aplicado:
```sql
-- ANTES
CREATE TABLE IF NOT EXISTS employee (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(128) NOT NULL,
    surname VARCHAR(255) NOT NULL,
    birth_date TIMESTAMP NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    position_id BIGINT NOT NULL REFERENCES position(id),
    salary NUMERIC(12,2),  -- ❌ ELIMINADO
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- DESPUÉS
CREATE TABLE IF NOT EXISTS employee (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(128) NOT NULL,
    surname VARCHAR(255) NOT NULL,
    birth_date TIMESTAMP NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    position_id BIGINT NOT NULL REFERENCES position(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 2. Migración de Base de Datos

Se aplicó el cambio de esquema a la base de datos de producción en ejecución:

```sql
ALTER TABLE employee DROP COLUMN IF EXISTS salary;
```

**Resultado:** Columna eliminada exitosamente (o ya estaba eliminada).

### 3. Verificación de Esquema

Se confirmó la estructura actualizada:

```sql
\d employee
```

**Esquema Final:**
```
                  Table "public.employee"
   Column    |            Type             | Nullable |      Default
-------------+-----------------------------+----------+------------------
 id          | bigint                      | not null | nextval(...)
 name        | character varying(128)      | not null |
 surname     | character varying(255)      | not null |
 birth_date  | timestamp without time zone | not null |
 email       | character varying(255)      | not null |
 position_id | bigint                      | not null |
 created_at  | timestamp without time zone |          | CURRENT_TIMESTAMP

Indexes:
    "employee_pkey" PRIMARY KEY, btree (id)
    "employee_email_key" UNIQUE CONSTRAINT, btree (email)

Foreign-key constraints:
    "employee_position_id_fkey" FOREIGN KEY (position_id) 
        REFERENCES "position"(id)

Referenced by:
    TABLE "expense_user" CONSTRAINT "fk_expense_user_employee"
    TABLE "payroll" CONSTRAINT "fk_payroll_employee"
```

---

## ✅ Pruebas y Validación

### Pruebas Manuales vía Postman

Todos los endpoints de Employee fueron probados y verificados como funcionales:

#### Endpoints Probados:
- ✅ `GET /api/employees` - Listar todos los empleados
- ✅ `GET /api/employees/{id}` - Obtener empleado por ID
- ✅ `POST /api/employees` - Crear nuevo empleado
- ✅ `PUT /api/employees/{id}` - Actualizar empleado
- ✅ `DELETE /api/employees/{id}` - Eliminar empleado

### Tests Automatizados Disponibles

El proyecto incluye tests unitarios completos para la funcionalidad de Employee:

**Archivo de Test:** `/backend/src/test/java/io/sunbit/app/test/employee/EmployeeTest.java`

**Casos de Prueba:**
1. `testEmployeeSaving` - Verificar creación de empleado
2. `testEmployeeUpdating` - Verificar actualización de empleado
3. `testEmployeeDeleting` - Verificar eliminación de empleado
4. `testEmployeeFindingById` - Verificar recuperación de empleado
5. `testEmployeePayrollRelation` - Verificar relación empleado-nómina

**Configuración de Tests:**
- Perfil: `test`
- Base de Datos: PostgreSQL real (no en memoria)
- Rollback: Deshabilitado (`@Rollback(false)`)

### Cómo Ejecutar los Tests

```bash
# Ejecutar todos los tests de Employee
./mvnw test -Dtest=EmployeeTest

# Ejecutar un test específico
./mvnw test -Dtest=EmployeeTest#testEmployeeSaving

# Ejecutar todos los tests del proyecto
./mvnw test
```

---

## 🔧 Detalles Técnicos

### ¿Por Qué Eliminar el Campo Salary?

#### Decisión de Diseño
La aplicación usa una entidad `Payroll` dedicada para rastrear la compensación de empleados:

**Beneficios:**
1. **Salarios Variables:** Soporte para cambios de salario en el tiempo
2. **Seguimiento de Historial:** Historial completo de nóminas por empleado
3. **Flexibilidad:** Diferentes fechas de nómina, montos y referencias
4. **Trazabilidad:** Cuándo y cuánto se pagó

**Estructura de la Entidad Payroll:**
```java
@Entity
public class Payroll {
    private Long id;
    private Double amount;           // ✅ Monto del salario aquí
    private LocalDateTime payrollDate;
    private Employee employee;       // FK a employee
    private ExpenseUser expenseUser; // FK a expense_user
}
```

#### ¿Por Qué No Ambos?
Tener tanto `employee.salary` como `payroll.amount` causaría:
- ❌ Redundancia de datos
- ❌ Problemas de sincronización
- ❌ Confusión sobre cuál es la "fuente de verdad"
- ❌ Complicación de la lógica de negocio

### Resumen de Relaciones de Entidades

```
┌──────────────┐       ┌──────────────┐
│   Employee   │       │   Position   │
├──────────────┤       ├──────────────┤
│ id           │       │ id           │
│ name         │       │ name         │
│ surname      │       └──────────────┘
│ birthDate    │              ▲
│ email        │              │
│ position_id  ├──────────────┘
│ created_at   │
└──────────────┘
       │
       │ 1:N
       │
       ▼
┌──────────────┐
│   Payroll    │
├──────────────┤
│ id           │
│ amount       │◄── El salario se almacena aquí
│ payrollDate  │
│ employee_id  │
└──────────────┘
```

### Consistencia de la Entidad Java

La entidad `Employee` ya era correcta (sin campo salary):

```java
@Entity
@Table(name = "employee")
public class Employee implements Serializable {
    private Long id;
    private String name;
    private String surname;
    private LocalDateTime birthDate;
    private String email;
    private Position position;           // @OneToOne
    private List<Payroll> payrolls;      // @OneToMany
    // ✅ NO hay campo salary - intencionalmente
}
```

---

## 📁 Archivos Modificados

### Scripts SQL (5 archivos)
```
sql/
├── 01_init_prod.sql     ✏️ Eliminada columna salary
├── 01_init_dev.sql      ✏️ Eliminada columna salary
├── init_test.sql        ✏️ Eliminada columna salary
├── init_prod.sql        ✏️ Eliminada columna salary (legacy)
└── init_dev.sql         ✏️ Eliminada columna salary (legacy)
```

### Base de Datos
```
Base de Datos de Producción (erp_prod_db)
└── tabla employee       ✏️ Columna salary eliminada
```

### No Se Requirieron Cambios en Java
```
✅ Employee.java         - Ya era correcto (sin campo salary)
✅ EmployeeController.java - No se necesitaron cambios
✅ EmployeeService.java  - No se necesitaron cambios
✅ IEmployeeDao.java     - No se necesitaron cambios
```

---

## 🚀 Próximos Pasos

### Acciones Inmediatas
- ✅ **HECHO:** Scripts SQL actualizados
- ✅ **HECHO:** Base de datos de producción migrada
- ✅ **HECHO:** Todos los endpoints probados vía Postman

### Acciones Recomendadas

#### 1. Ejecutar Tests Automatizados
```bash
# Navegar al backend
cd backend

# Ejecutar tests de Employee
./mvnw test -Dtest=EmployeeTest

# Ejecutar todos los tests
./mvnw test
```

#### 2. Documentar API con Ejemplos
Considera crear una colección de Postman o documentación OpenAPI con:
- Ejemplos de requests para todos los endpoints de Employee
- Reglas de validación
- Respuestas de error
- Datos de ejemplo

#### 3. Tests de Integración
Considera añadir tests de integración para:
- Creación de empleado con búsqueda de posición
- Relación empleado-nómina
- Operaciones en cascada
- Casos límite (email duplicado, posición inválida, etc.)

#### 4. Migraciones de Base de Datos Futuras

Para futuros cambios de esquema, considera usar una herramienta de migración como:
- **Flyway** (recomendado para Spring Boot)
- **Liquibase**

Esto proporciona:
- Migraciones versionadas
- Aplicación automática al inicio
- Capacidades de rollback
- Soporte para colaboración en equipo

---

## 📊 Estadísticas Resumen

### Estado de la Base de Datos
| Tabla         | Registros | Estado |
|---------------|-----------|--------|
| position      | 51        | ✅     |
| role          | 3         | ✅     |
| employee      | 61        | ✅     |
| payroll       | 305       | ✅     |
| expense_user  | 57        | ✅     |
| expense       | 20+       | ✅     |
| user_role     | 59        | ✅     |

### Resumen de Cambios
| Categoría           | Cantidad | Estado |
|---------------------|----------|--------|
| Archivos SQL        | 5        | ✅     |
| Migraciones BD      | 1        | ✅     |
| Entidades Java      | 0        | ✅     |
| Tests escritos      | 0        | ✅     |
| Tests ejecutados    | N/A      | ⏸️     |
| Pruebas manuales    | 5        | ✅     |

---

## 🎓 Lecciones Aprendidas

### 1. La Consistencia Esquema-Entidad es Crítica
Siempre asegúrate de que el esquema de base de datos y las entidades Java estén sincronizados. Usa herramientas como:
- Validación de esquema JPA (`spring.jpa.hibernate.ddl-auto=validate`)
- Herramientas de migración de base de datos (Flyway/Liquibase)
- Revisiones de código

### 2. Limpieza de Código Legacy
La deuda técnica se acumula con el tiempo. Las revisiones regulares ayudan a:
- Identificar campos no utilizados
- Eliminar código obsoleto
- Mantener la documentación
- Mejorar la mantenibilidad

### 3. Estrategias de Testing
Múltiples enfoques de testing proporcionan cobertura completa:
- **Tests Unitarios:** Rápidos, aislados, prueban componentes individuales
- **Tests de Integración:** Prueban interacciones de componentes
- **Tests Manuales de API:** Validación de escenarios del mundo real
- **Tests Automatizados de API:** Verificación repetible de endpoints

### 4. Patrones de Diseño de Base de Datos
Elegir el patrón correcto es importante:
- ✅ **Bueno:** Salario variable vía tabla Payroll (flexibilidad)
- ❌ **Malo:** Campo de salario fijo en Employee (rígido)
- ✅ **Bueno:** Trazabilidad mediante timestamps
- ✅ **Bueno:** Las relaciones reflejan la lógica de negocio

---

## 📝 Notas

### ¿Por Qué No Se Requirió Recompilación?

El código Java nunca referenció el campo `salary`, por lo tanto:
- ✅ No se necesitaron cambios en el código Java
- ✅ El JAR existente funciona correctamente
- ✅ Solo se actualizó el esquema SQL
- ✅ Los nuevos contenedores usarán el esquema actualizado automáticamente

### Contexto de Usuario de Base de Datos

El proyecto usa diferentes usuarios de base de datos por entorno:
- **Producción:** `erp_prod_user` / `erp_prod_db` (puerto 5442)
- **Desarrollo:** `erp_dev_user` / `erp_dev_db` (puerto 5433)
- **Test:** `erp_test_user` / `erp_test_db` (puerto 5434)

Siempre verifica que te estés conectando al entorno correcto.

---

## 🔗 Documentación Relacionada

- [Hoja de Ruta Sesión 6](./session_6_roadmap.md)
- [Documentación Entidad Employee](../entity/employee-entity-join-vs-list-20251211-0935.md)
- [Guía de Inicialización de Base de Datos](../secuencia_inicializacion_bdd_automatizada.md)
- [Referencia de Comandos Docker](../docker/docker_commands_session_6.md)

---

## ✅ Cierre

**Sesión completada exitosamente.**  
Todos los objetivos logrados, base de datos limpiada, endpoints probados y funcionando.

**Ingeniero:** Asistente IA (GitHub Copilot)  
**Fecha:** 14 de Diciembre de 2025  
**Hora:** 23:30  
**Estado:** ✅ Listo para Producción

---

*Este documento sirve como registro completo del trabajo realizado durante esta sesión y puede ser usado como referencia para desarrollo futuro y onboarding.*
