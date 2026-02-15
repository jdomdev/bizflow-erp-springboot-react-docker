# Guía de Administrador

El rol **ADMIN** tiene acceso completo a todas las funcionalidades del sistema.

## Permisos

| Módulo | Acciones |
|--------|----------|
| Dashboard | Ver estadísticas de todos los gastos |
| Empleados | CRUD completo |
| Cargos | CRUD completo |
| Gastos | CRUD completo de todos los usuarios |
| Nóminas | CRUD completo |
| Usuarios | CRUD completo |
| Perfil | Ver y editar |
| Ajustes | Configurar preferencias |

## Dashboard

Como administrador, el dashboard muestra:

- **Total de gastos** de toda la empresa
- **Gastos del mes** actual
- **Gastos recientes** con nombre del creador
- **Gráfico de tendencia** mensual

## Gestión de Usuarios

### Crear usuario

1. Ir a **Usuarios** en el menú lateral
2. Click en **Nuevo Usuario**
3. Completar formulario:
   - Email (único en el sistema)
   - Contraseña
   - Rol (ADMIN, MANAGER, USER)
   - Empleado vinculado (opcional)
4. Click en **Guardar**

### Vincular usuario con empleado

Un usuario puede estar vinculado a un empleado para:
- Asociar gastos automáticamente
- Mostrar datos del empleado en perfil
- Asignar nóminas

```
Usuario ←────────── Empleado
  │                    │
  └── email            └── userId (FK)
```

## Gestión de Empleados

### Crear empleado

1. Ir a **Empleados**
2. Click en **Nuevo Empleado**
3. Completar datos personales y cargo
4. Opcionalmente vincular con usuario existente

## Gestión de Gastos

### Ver todos los gastos

Por defecto verás todos los gastos de la empresa. Puedes:

- **Filtrar** por fecha, categoría o creador
- **Ordenar** por columnas
- **Paginar** con selector de items por página
- **Eliminar** cualquier gasto

### Aprobar gastos

::: info En desarrollo
La funcionalidad de aprobación de gastos está planificada para futuras versiones.
:::

## Gestión de Nóminas

### Crear nómina

1. Ir a **Nóminas**
2. Click en **Nueva Nómina**
3. Seleccionar empleado
4. Completar datos:
   - Período
   - Salario base
   - Deducciones
   - Bonificaciones

El empleado recibirá una notificación automática.

## Notificaciones

Como ADMIN recibes notificaciones cuando:

- ✅ Un usuario crea un nuevo gasto
- ✅ Se genera una nueva nómina

Las notificaciones aparecen en la campana del header.

## Checklist de Testing

Usa este checklist para verificar que el rol funciona correctamente:

- [ ] Login exitoso
- [ ] Dashboard muestra todos los gastos
- [ ] Puede crear/editar/eliminar usuarios
- [ ] Puede crear/editar/eliminar empleados
- [ ] Puede crear/editar/eliminar cargos
- [ ] Puede crear/editar/eliminar gastos de cualquier usuario
- [ ] Puede crear/editar/eliminar nóminas
- [ ] Recibe notificaciones de gastos nuevos
- [ ] Modo oscuro funciona
- [ ] Ajustes de paginación persisten
