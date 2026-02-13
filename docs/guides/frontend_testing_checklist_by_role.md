> **Fecha de creación**: 9 de Febrero 2026
> **Última actualización**: 13 de Febrero 2026
  
Este documento contiene los checklists de pruebas para verificar el correcto funcionamiento del frontend según cada rol de usuario.
 

---

## 📊 Matriz de Permisos por Rol

### Tabla Resumen

| Módulo         | ADMIN                 | MANAGER                 | USER                 |
| -------------- | :-------------------: | :---------------------: | :------------------: |
| **Dashboard**  | ✅ Ver todos los gastos | ✅ Ver todos los gastos | ⚠️ Solo sus gastos   |
| **Employees**  | ✅ CRUD completo       | ✅ CRUD completo        | ❌ Sin acceso        |
| **Positions**  | ✅ CRUD completo       | ❌ Sin acceso           | ❌ Sin acceso        |
| **Expenses**   | ✅ Ver/Gestionar todos | ✅ Ver todos / Gestionar propios | ⚠️ Solo los propios  |
| **Payrolls**   | ✅ CRUD + ver todas    | ✅ Ver todas            | ⚠️ Solo las propias  |
| **Users**      | ✅ CRUD completo       | ❌ Sin acceso           | ❌ Sin acceso        |
| **Notificaciones** | ✅ Todas            | ✅ Todas                | ✅ Todas             |
| **Perfil**     | ✅ Ver/Editar          | ✅ Ver/Editar           | ✅ Ver/Editar        |
| **Settings**   | ✅ Acceso              | ✅ Acceso               | ✅ Acceso            |

---

### Detalle de Permisos por Módulo

#### 👥 Employees

| Acción         | ADMIN | MANAGER | USER |
| -------------- | :---: | :-----: | :--: |
| Listar todos   |   ✅  |    ✅   |   ❌  |
| Ver detalle    |   ✅  |    ✅   |   ❌  |
| Crear          |   ✅  |    ✅   |   ❌  |
| Editar         |   ✅  |    ✅   |   ❌  |
| Eliminar       |   ✅  |    ✅   |   ❌  |

#### 💼 Positions

| Acción         | ADMIN | MANAGER | USER |
| -------------- | :---: | :-----: | :--: |
| Listar         |   ✅  |    ❌   |   ❌  |
| Crear          |   ✅  |    ❌   |   ❌  |
| Editar         |   ✅  |    ❌   |   ❌  |
| Eliminar       |   ✅  |    ❌   |   ❌  |

#### 💰 Expenses

| Acción             | ADMIN | MANAGER | USER |
| ------------------ | :---: | :-----: | :--: |
| Ver todos          |   ✅  |    ✅   |   ❌  |
| Ver propios        |   ✅  |    ✅   |   ✅  |
| Crear (propios)    |   ✅  |    ✅   |   ✅  |
| Editar propios     |   ✅  |    ✅   |   ✅  |
| Editar de otros    |   ✅  |    ❌   |   ❌  |
| Eliminar           |   ✅  |    ❌   |   ❌  |

#### 💵 Payrolls

| Acción         | ADMIN | MANAGER | USER |
| -------------- | :---: | :-----: | :--: |
| Ver todas      |   ✅  |    ✅   |   ❌  |
| Ver propias    |   ✅  |    ✅   |   ✅  |
| Crear          |   ✅  |    ❌   |   ❌  |
| Editar         |   ✅  |    ❌   |   ❌  |
| Eliminar       |   ✅  |    ❌   |   ❌  |

#### 👤 Users

| Acción         | ADMIN | MANAGER | USER |
| -------------- | :---: | :-----: | :--: |
| Listar         |   ✅  |    ❌   |   ❌  |
| Crear          |   ✅  |    ❌   |   ❌  |
| Editar         |   ✅  |    ❌   |   ❌  |
| Eliminar       |   ✅  |    ❌   |   ❌  |
| Asignar roles  |   ✅  |    ❌   |   ❌  |

---

### Menú de Navegación por Rol

| Opción de Menú | ADMIN | MANAGER | USER |
| -------------- | :---: | :-----: | :--: |
| Dashboard      |   ✅  |    ✅   |   ✅  |
| Gastos         |   ✅  |    ✅   |   ✅  |
| Nóminas        |   ✅  |    ✅   |   ✅  |
| Cargos         |   ✅  |    ❌   |   ❌  |
| Empleados      |   ✅  |    ✅   |   ❌  |
| Usuarios       |   ✅  |    ❌   |   ❌  |
| Perfil         |   ✅  |    ✅   |   ✅  |
| Configuración  |   ✅  |    ✅   |   ✅  |

---

**Leyenda:**
- ✅ Acceso completo
- ⚠️ Acceso limitado (solo sus propios datos)
- ❌ Sin acceso

---

## 🔐 Usuarios de Prueba

| Rol        | Nombre       | Email                          | Notas                          |
| ---------- | ------------ | ------------------------------ | ------------------------------ |
| **ADMIN**  | Ada Lovelace | ada.lovelace@bizflowerp.com    | Acceso completo                |
| **MANAGER**| Nikola Tesla | nikola.tesla@bizflowerp.com    | Ver todos los gastos, gestión de empleados |
| **USER**   | Ken Thompson | ken.thompson@bizflowerp.com    | Acceso básico, solo sus datos  |
  

> ⚠️ **Nota sobre credenciales**: Las contraseñas se encuentran en `scripts/secrets/users_with_passwords/` (gitignored). Consulta [credentials_system.md](../credentials_system.md) para más información.

---

## 👑 CHECKLIST ADMIN (Ada Lovelace) ✅ COMPLETADO
```json
{
  "name": "Ada",
  "surname": "Lovelace",
  "email": "ada.lovelace@bizflowerp.com"
}
```

### 🔐 1. AUTENTICACIÓN
- [x] **Login** - Ingresar con credenciales válidas (Ada Lovelace)
- [x] **Login error** - Intentar con credenciales inválidas (debe mostrar error)
- [x] **Logout** - Cerrar sesión correctamente
- [x] **Persistencia de sesión** - Refrescar página (F5) y verificar que sigue logueado
- [x] **Signup** - Crear nuevo usuario (si la funcionalidad está habilitada)

### 🏠 2. DASHBOARD
- [x] **Carga inicial** - Dashboard carga sin errores
- [x] **Estadísticas** - Se muestran correctamente las tarjetas/cards
- [x] **Gastos recientes** - Lista de gastos visible
- [x] **Permisos ADMIN** - Con usuario admin ve TODOS los gastos

### 👤 3. PERFIL (ProfilePage)
- [x] **Ver perfil** - Muestra nombre, email, roles
- [x] **Roles visibles** - Se muestran los roleIds del usuario
- [x] **Employee vinculado** - Si tiene employee, muestra sus datos

### 👥 4. EMPLOYEES (EmployeesPage)
- [x] **Listar** - Ver lista de empleados
- [x] **Crear** - Añadir nuevo empleado
- [x] **Editar** - Modificar empleado existente
- [x] **Eliminar** - Borrar empleado
- [x] **Búsqueda/filtro** - Si existe, probar filtrado
- [x] **Vinculación User↔Employee** - Verificar que al crear employee con email existente se vincula

### 💼 5. POSITIONS (PositionsPage)
- [x] **Listar** - Ver lista de posiciones/cargos
- [x] **Crear** - Añadir nueva posición (solo ADMIN)
- [x] **Editar** - Modificar posición (solo ADMIN)
- [x] **Eliminar** - Borrar posición (solo ADMIN)

### 💰 6. EXPENSES (ExpensesPage)

- [x] **Listar** - Ver lista de gastos
- [x] **Crear** - Añadir nuevo gasto
- [x] **Editar** - Modificar gasto existente
- [x] **Eliminar** - Borrar gasto
- [x] **Info del usuario** - Cada gasto muestra quién lo creó

### 💵 7. PAYROLL (PayrollPage)
- [x] **Listar** - Ver lista de nóminas
- [x] **Filtrar por empleado** - Si existe
- [x] **Crear** - Añadir nueva nómina (solo ADMIN)
- [x] **Editar** - Modificar nómina (solo ADMIN)
- [x] **Eliminar** - Borrar nómina (solo ADMIN)

### 👤 8. USERS (UsersPage)
- [x] **Listar** - Ver lista de usuarios (solo ADMIN)
- [x] **Crear** - Añadir nuevo usuario
- [x] **Editar** - Modificar usuario
- [x] **Eliminar** - Borrar usuario
- [x] **Asignar roles** - Cambiar roles de usuario

# ⚙️ 9. SETTINGS (SettingsPage)
- [x] **Acceso** - Página carga correctamente
- [x] **Funcionalidad** - Lo que ofrezca (temas, preferencias, etc.)

### 🔔 10. NOTIFICACIONES (NotificationBell)
- [x] **WebSocket conexión** - Se conecta al backend
- [x] **Recibir notificación** - Al crear gasto, llega notificación
- [x] **Badge contador** - Muestra número de no leídas
- [x] **Marcar como leída** - Click en notificación la marca leída

### 🧭 11. NAVEGACIÓN (Layout)
- [x] **Menú lateral** - Todos los links funcionan
- [x] **Menú responsive** - En móvil funciona correctamente
- [x] **Links según rol** - Admin ve más opciones que user normal

### 🎨 12. UI/UX GENERAL
- [x] **Responsive** - Probar en diferentes tamaños de pantalla
- [x] **Loading states** - Se muestran spinners mientras carga
- [x] **Error handling** - Errores de API se muestran al usuario
- [x] **Validación formularios** - Campos requeridos, formatos correctos
---

## 👔 CHECKLIST MANAGER (Nicola Tesla)
```json
{
"name": "Nikola",
"surname": "Tesla",
"email": "nikola.tesla@bizflowerp.com"
}
```

### 🔐 1. AUTENTICACIÓN
- [x] **Login** - Ingresar con credenciales válidas
- [x] **Verificar rol** - En perfil debe mostrar "MANAGER"
- [x] **Logout** - Cerrar sesión correctamente

### 🏠 2. DASHBOARD
- [ ] **Carga inicial** - Dashboard carga sin errores
- [ ] **Estadísticas** - Se muestran las tarjetas de resumen
- [ ] **Gastos visibles** - Verificar qué gastos ve (propios o todos)

### 👤 3. PERFIL (ProfilePage)
- [ ] **Ver perfil** - Muestra nombre, email
- [ ] **Rol MANAGER** - Se muestra el rol correctamente
- [ ] **Employee vinculado** - Muestra datos del empleado asociado

### 👥 4. EMPLOYEES (EmployeesPage) ✅ ACCESO COMPLETO
- [ ] **Menú visible** - El link "Empleados" aparece en el menú lateral
- [ ] **Listar** - Ver lista de todos los empleados
- [ ] **Crear** - Añadir nuevo empleado
- [ ] **Editar** - Modificar empleado existente
- [ ] **Eliminar** - Borrar empleado
- [ ] **Búsqueda** - Filtrar por nombre funciona

### 💼 5. POSITIONS (PositionsPage) ❌ SIN ACCESO
- [ ] **Menú oculto** - El link "Cargos" NO aparece en el menú lateral
- [ ] **URL directa** - Al ir a `/positions` debe redirigir o mostrar error

### 💰 6. EXPENSES (ExpensesPage) ⚠️ SOLO PROPIOS
- [ ] **Listar propios** - Ve solo SUS gastos
- [ ] **NO ve toggle** - No puede cambiar a "Todos los gastos"
- [ ] **Crear** - Puede crear sus propios gastos
- [ ] **Editar propios** - Puede editar sus gastos
- [ ] **NO puede eliminar** - Solo ADMIN puede eliminar gastos

### 💵 7. PAYROLL (PayrollPage) ✅ VER TODAS
- [ ] **Ver todas** - Puede ver TODAS las nóminas del sistema
- [ ] **Toggle disponible** - Puede alternar entre "Mis nóminas" y "Todas"
- [ ] **NO puede crear** - Solo ADMIN puede crear nóminas
- [ ] **NO puede editar** - Solo ADMIN puede editar nóminas
- [ ] **NO puede eliminar** - Solo ADMIN puede eliminar nóminas

### 👤 8. USERS (UsersPage) ❌ SIN ACCESO
- [ ] **Menú oculto** - El link "Usuarios" NO aparece en el menú lateral
- [ ] **URL directa** - Al ir a `/users` debe mostrar error de permisos


### ⚙️ 9. SETTINGS
- [ ] **Acceso** - Página carga correctamente

### 🔔 10. NOTIFICACIONES
- [ ] **WebSocket** - Se conecta correctamente
- [ ] **Recibir** - Recibe notificaciones al crear gasto
- [ ] **Marcar leída** - Funciona correctamente

### 🧭 11. NAVEGACIÓN
- [ ] **Menú lateral** - Ve: Dashboard, Gastos, Nómina, **Empleados**, Perfil, Config
- [ ] **NO ve** - Cargos, Usuarios
- [ ] **Command Palette** - Solo busca en módulos permitidos

### ❌ 12. PRUEBAS DE RESTRICCIÓN
- [ ] **403 en Positions** - Al intentar crear/editar cargo (si llega al endpoint)
- [ ] **403 en Users** - Al intentar acceder a gestión de usuarios
- [ ] **403 en Payroll CRUD** - Al intentar crear/editar/eliminar nómina

---

## 👤 CHECKLIST USER (Ken Thompson)
  
```json
{
"name": "Ken",
"surname": "Thompson",
"email": "ken.thompson@bizflowerp.com"
}
```

### 🔐 1. AUTENTICACIÓN
- [ ] **Login** - Ingresar con credenciales válidas
- [ ] **Verificar rol** - En perfil debe mostrar "USER"
- [ ] **Logout** - Cerrar sesión correctamente
- [ ] **Persistencia** - Refrescar página mantiene sesión

### 🏠 2. DASHBOARD
- [ ] **Carga inicial** - Dashboard carga sin errores
- [ ] **Gastos** - Solo ve SUS propios gastos en el resumen
- [ ] **Estadísticas** - Basadas en sus propios datos

### 👤 3. PERFIL (ProfilePage)
- [ ] **Ver perfil** - Muestra su nombre, email
- [ ] **Rol USER** - Se muestra el rol correctamente
- [ ] **Employee vinculado** - Muestra datos si tiene empleado asociado

### 👥 4. EMPLOYEES (EmployeesPage) ❌ SIN ACCESO
- [ ] **Menú oculto** - El link "Empleados" NO aparece en el menú lateral
- [ ] **URL directa** - Al ir a `/employees` debe mostrar "No tienes permisos"

### 💼 5. POSITIONS (PositionsPage) ❌ SIN ACCESO
- [ ] **Menú oculto** - El link "Cargos" NO aparece en el menú lateral
- [ ] **URL directa** - Al ir a `/positions` debe mostrar error

### 💰 6. EXPENSES (ExpensesPage) ⚠️ SOLO PROPIOS
- [ ] **Listar propios** - Ve SOLO sus gastos
- [ ] **NO ve toggle** - No tiene opción de ver "Todos"
- [ ] **Crear** - Puede crear sus propios gastos
- [ ] **Editar propios** - Puede editar sus gastos
- [ ] **NO puede ver otros** - No puede ver gastos de otros usuarios
- [ ] **NO puede eliminar** - Solo ADMIN puede eliminar

### 💵 7. PAYROLL (PayrollPage) ⚠️ SOLO PROPIAS
- [ ] **Ver propias** - Ve SOLO sus nóminas
- [ ] **NO ve toggle** - No tiene opción de ver "Todas"
- [ ] **NO puede crear** - Solo ADMIN puede crear nóminas
- [ ] **NO puede editar** - Solo ADMIN puede editar
- [ ] **NO puede eliminar** - Solo ADMIN puede eliminar

### 👤 8. USERS (UsersPage) ❌ SIN ACCESO
- [ ] **Menú oculto** - El link "Usuarios" NO aparece
- [ ] **URL directa** - Al ir a `/users` debe mostrar error de permisos
 

### ⚙️ 9. SETTINGS
- [ ] **Acceso** - Página carga correctamente

### 🔔 10. NOTIFICACIONES
- [ ] **WebSocket** - Se conecta correctamente
- [ ] **Recibir** - Recibe notificaciones de sus acciones
- [ ] **Marcar leída** - Funciona correctamente

### 🧭 11. NAVEGACIÓN
- [ ] **Menú lateral** - Ve SOLO: Dashboard, Gastos, Nómina, Perfil, Config
- [ ] **NO ve** - Cargos, Empleados, Usuarios
- [ ] **Command Palette** - Solo busca en Dashboard, Gastos, Nóminas

### ❌ 12. PRUEBAS DE RESTRICCIÓN
- [ ] **Error 403** - Al intentar acciones prohibidas, muestra mensaje claro
- [ ] **No crash** - La app no se rompe al recibir 403
- [ ] **Redirección** - Al acceder a URLs prohibidas, manejo adecuado

---

## 📝 Plantilla de Registro de Pruebas
  
```markdown
## Registro de Ejecución
  
**Fecha:** _______________
**Entorno:** _______________ (dev/test/prod)
**Navegador:** _______________
**Tester:** _______________

### Resultados por Rol
  
| Rol | Usuario | Checks Pasados | Checks Fallidos | Notas |
|-----|---------|----------------|-----------------|-------|
| ADMIN | Ada Lovelace | __/__ | __/__ | |
| MANAGER | Marie Curie | __/__ | __/__ | |
| USER | Ken Thompson | __/__ | __/__ | |


### Issues Encontrados
1. **[ROL] Módulo - Descripción**
- Pasos para reproducir:
- Comportamiento esperado:
- Comportamiento actual:

2. ...
  

### Comentarios Adicionales

```

---

  

## 🔄 Historial de Cambios

  

| Fecha       | Cambio                                             | Autor |
| ----------- | -------------------------------------------------- | :---: |
| 2026-02-09  | Creación inicial con 3 roles                      |   -   |
| 2026-02-09  | Implementación de permisos MANAGER en backend     |   -   |
| 2026-02-09  | Checklist ADMIN completado                        |   -   |