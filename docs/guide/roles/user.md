# Guía de Usuario

El rol **USER** es el más restrictivo: solo puede acceder y gestionar sus propios recursos.

## Permisos

| Módulo | Acciones |
|--------|----------|
| Dashboard | Ver solo sus propios gastos |
| Empleados | ❌ Sin acceso |
| Cargos | ❌ Sin acceso |
| Gastos | CRUD solo propios |
| Nóminas | Ver solo las propias |
| Usuarios | ❌ Sin acceso |
| Perfil | Ver y editar |
| Ajustes | Configurar preferencias |

## Dashboard

El dashboard muestra exclusivamente tus datos:

- **Total de gastos** - Solo tus gastos
- **Gastos del mes** - Tus gastos del mes actual
- **Gastos recientes** - Tus últimos gastos
- **Gráfico de tendencia** - Tu historial de gastos

::: info Datos personales
A diferencia de ADMIN y MANAGER, tu dashboard solo muestra tus estadísticas personales.
:::

## Módulo de Gastos

### Vista de gastos

Verás únicamente **tus propios gastos**.

### Operaciones permitidas

| Acción | ¿Permitido? |
|--------|-------------|
| Ver tus gastos | ✅ |
| Crear gasto | ✅ |
| Editar tu gasto | ✅ |
| Eliminar tu gasto | ✅ |
| Ver gastos de otros | ❌ |

### Crear un gasto

1. Ir a **Gastos**
2. Click en **Nuevo Gasto**
3. Completar:
   - Descripción
   - Monto
   - Categoría
   - Fecha
4. Click en **Guardar**

::: tip Notificación automática
Cuando crees un gasto, los administradores recibirán una notificación automática.
:::

## Módulo de Nóminas

### Solo visualización personal

Puedes ver únicamente **tus propias nóminas**.

Información disponible:
- Período
- Salario base
- Deducciones
- Bonificaciones
- Total neto

## Notificaciones

Como USER recibes notificaciones cuando:

- ✅ Se te asigna una nueva nómina

## Accesos Restringidos

Estas rutas mostrarán error 403 o no serán visibles en el menú:

| Ruta | Estado |
|------|--------|
| `/users` | ❌ 403 - Sin acceso |
| `/employees` | ❌ 403 - Sin acceso |
| `/positions` | ❌ 403 - Sin acceso |

Si intentas acceder directamente por URL:

```
Error 403: No tienes permisos para acceder a este recurso
```

## Perfil de Usuario

En la página de **Perfil** puedes ver:

- Tu información de usuario (ID, email, rol)
- Si tienes un empleado vinculado, verás sus datos:
  - Nombre completo
  - Cargo
  - Departamento

## Ajustes

En la página de **Ajustes** puedes configurar:

- **Tema**: Modo claro / oscuro
- **Items por página**: 10, 20, 50, 100

Estas preferencias se guardan en el navegador.

## Checklist de Testing

- [ ] Login exitoso
- [ ] Dashboard muestra solo mis gastos
- [ ] No ve menú de Empleados
- [ ] No ve menú de Cargos
- [ ] No ve menú de Usuarios
- [ ] Error 403 al acceder a /employees
- [ ] Error 403 al acceder a /positions
- [ ] Error 403 al acceder a /users
- [ ] Ve solo sus gastos
- [ ] Puede crear gastos
- [ ] Puede editar sus gastos
- [ ] No ve botón eliminar en gastos
- [ ] Ve solo sus nóminas
- [ ] Recibe notificaciones de nóminas
- [ ] Perfil muestra datos de empleado vinculado (si aplica)
- [ ] Ajustes persisten correctamente
