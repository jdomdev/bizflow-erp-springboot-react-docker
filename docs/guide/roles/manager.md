# Guía de Manager

El rol **MANAGER** sigue un modelo de supervisor: puede ver todos los datos de la empresa pero solo editar sus propios registros.

## Permisos

| Módulo | Acciones |
|--------|----------|
| Dashboard | Ver estadísticas de todos los gastos |
| Empleados | Ver todos, editar solo el propio |
| Cargos | Solo lectura |
| Gastos | Ver todos, CRUD solo propios |
| Nóminas | Ver todas (no crear/editar) |
| Usuarios | ❌ Sin acceso |
| Perfil | Ver y editar |
| Ajustes | Configurar preferencias |

## Dashboard

Como manager, el dashboard muestra:

- **Total de gastos** de toda la empresa
- **Gastos del mes** actual
- **Gastos recientes** de todos los usuarios
- **Gráfico de tendencia** mensual

::: tip Visibilidad completa
Los managers tienen la misma visibilidad que los admins en el dashboard para poder supervisar el estado financiero de la empresa.
:::

## Módulo de Gastos

### Vista de gastos

Por defecto verás **todos los gastos** de la empresa. Tienes un toggle para cambiar entre:

- **Ver todos** - Gastos de toda la empresa
- **Mis gastos** - Solo tus gastos

### Operaciones permitidas

| Acción | ¿Permitido? |
|--------|-------------|
| Ver todos los gastos | ✅ |
| Crear gasto propio | ✅ |
| Editar gasto propio | ✅ |
| Eliminar gasto propio | ❌ |
| Editar gasto de otro | ❌ |
| Eliminar gasto de otro | ❌ |

::: warning Botón eliminar
El botón de eliminar solo está visible para administradores. Si necesitas eliminar un gasto, contacta a un administrador.
:::

## Módulo de Empleados

### Visibilidad

Puedes ver la lista completa de empleados con:
- ID
- Nombre
- Cargo
- Departamento
- Usuario vinculado

### Edición

Solo puedes editar **tu propio perfil de empleado** (si tu usuario está vinculado a uno).

## Módulo de Cargos

### Solo lectura

Puedes ver la lista de cargos de la empresa:
- Nombre del cargo
- Departamento
- Salario base

No puedes crear, editar ni eliminar cargos.

## Módulo de Nóminas

### Solo visualización

Puedes ver todas las nóminas de la empresa para supervisión, pero no puedes:
- Crear nóminas
- Editar nóminas
- Eliminar nóminas

## Notificaciones

Como MANAGER recibes notificaciones cuando:

- ✅ Se te asigna una nueva nómina

**No recibes** notificaciones de nuevos gastos (eso es solo para ADMIN).

## Accesos Restringidos

Estas rutas mostrarán error 403:

- `/users` - Gestión de usuarios

Si intentas acceder directamente por URL:

```
Error 403: No tienes permisos para acceder a este recurso
```

## Checklist de Testing

- [ ] Login exitoso
- [ ] Dashboard muestra todos los gastos
- [ ] Lista de empleados visible
- [ ] No puede crear/eliminar empleados
- [ ] Solo puede editar su empleado vinculado
- [ ] Lista de cargos visible (solo lectura)
- [ ] Ve todos los gastos
- [ ] Puede crear sus propios gastos
- [ ] No puede eliminar gastos
- [ ] Ve todas las nóminas (solo lectura)
- [ ] No ve menú de Usuarios
- [ ] Error 403 al acceder a /users
- [ ] Recibe notificaciones de nóminas
