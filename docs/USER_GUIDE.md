# Guía de Usuario - ExpenseNoteApp v1.1.0

Guía completa para usuarios finales del sistema de gestión de gastos empresariales.

## 📋 Tabla de Contenidos

1. [Introducción](#introducción)
2. [Primeros Pasos](#primeros-pasos)
3. [Gestión de Gastos](#gestión-de-gastos)
4. [Gestión de Nómina](#gestión-de-nómina)
5. [Gestión de Empleados](#gestión-de-empleados)
6. [Configuración de Perfil](#configuración-de-perfil)
7. [Roles y Permisos](#roles-y-permisos)
8. [Preguntas Frecuentes](#preguntas-frecuentes)

---

## 🎯 Introducción

ExpenseNoteApp es un sistema completo para la gestión de gastos empresariales que permite:
- Registrar y gestionar gastos de empleados
- Aprobar o rechazar gastos (administradores)
- Consultar información de nómina
- Gestionar empleados y puestos
- Generar reportes y estadísticas

### ¿Para quién es este sistema?

**Usuarios (Empleados)**:
- Pueden crear, ver y editar sus propios gastos
- Consultar su información de nómina
- Ver estadísticas de sus gastos

**Administradores**:
- Todas las funciones de usuarios
- Aprobar o rechazar gastos
- Gestionar empleados y puestos
- Acceder a todos los gastos y nóminas
- Administrar usuarios y roles

---

## 🚀 Primeros Pasos

### 1. Acceso al Sistema

1. Abre tu navegador web
2. Navega a: `http://localhost:3000` (o la URL proporcionada)
3. Verás la página de inicio de sesión

### 2. Inicio de Sesión

#### Primera vez (Registro)
1. Click en "Crear cuenta" o "Sign Up"
2. Completa el formulario:
   - **Nombre**: Tu nombre
   - **Apellido**: Tu apellido
   - **Email**: Tu correo electrónico (será tu usuario)
   - **Contraseña**: Mínimo 8 caracteres
3. Click en "Registrarse"
4. Inicia sesión con tus credenciales

#### Usuario existente
1. Ingresa tu email
2. Ingresa tu contraseña
3. Click en "Iniciar sesión"

### 3. Panel Principal (Dashboard)

Después de iniciar sesión, verás el dashboard con:
- **Tarjetas de estadísticas**: Total de gastos, gastos pendientes, aprobados
- **Tabla de gastos recientes**: Últimos gastos registrados
- **Menú lateral**: Navegación a diferentes secciones

---

## 💰 Gestión de Gastos

### Ver Mis Gastos

1. Click en "Gastos" en el menú lateral
2. Verás una lista de todos tus gastos con:
   - Descripción
   - Monto
   - Fecha
   - Estado (Pendiente, Aprobado, Rechazado)

### Crear un Nuevo Gasto

1. En la página de Gastos, click en "Nuevo Gasto" o "+"
2. Completa el formulario:
   - **Descripción**: Describe el gasto (ej: "Viaje a cliente")
   - **Monto**: Cantidad en dinero
   - **Fecha**: Fecha del gasto
   - **Categoría**: Selecciona una categoría (Transporte, Comida, etc.)
3. Click en "Guardar" o "Crear"

**Ejemplo**:
```
Descripción: Taxi al aeropuerto
Monto: $45.50
Fecha: 15/11/2024
Categoría: Transporte
```

### Editar un Gasto

> ⚠️ Solo puedes editar gastos que estén en estado "Pendiente"

1. En la lista de gastos, busca el gasto a editar
2. Click en el icono de editar (lápiz)
3. Modifica los campos necesarios
4. Click en "Guardar cambios"

### Eliminar un Gasto

> ⚠️ Solo puedes eliminar gastos que estén en estado "Pendiente"

1. En la lista de gastos, busca el gasto a eliminar
2. Click en el icono de eliminar (basura)
3. Confirma la eliminación

### Estados de Gastos

| Estado | Descripción | Acciones Disponibles |
|--------|-------------|---------------------|
| **Pendiente** | Gasto creado, esperando aprobación | Editar, Eliminar |
| **Aprobado** | Gasto aprobado por administrador | Solo ver |
| **Rechazado** | Gasto rechazado por administrador | Solo ver |

### Filtrar Gastos

En la página de gastos puedes filtrar por:
- **Fecha**: Rango de fechas
- **Estado**: Pendiente, Aprobado, Rechazado
- **Categoría**: Por tipo de gasto
- **Búsqueda**: Por descripción

---

## 💵 Gestión de Nómina

### Ver Mi Nómina

1. Click en "Nómina" en el menú lateral
2. Verás un historial de tus pagos con:
   - Periodo (mes/año)
   - Monto bruto
   - Deducciones
   - Monto neto
   - Fecha de pago
   - Estado

### Descargar Recibo de Nómina

1. En la lista de nóminas, busca el periodo deseado
2. Click en "Descargar" o el icono de PDF
3. Se descargará el recibo en formato PDF

### Entender tu Recibo

Un recibo de nómina típico incluye:
- **Salario base**: Tu salario mensual
- **Bonos**: Bonificaciones adicionales
- **Deducciones**: Impuestos, seguridad social, etc.
- **Salario neto**: Cantidad que recibes

---

## 👥 Gestión de Empleados (Solo Administradores)

### Ver Empleados

1. Click en "Empleados" en el menú lateral
2. Verás una lista con todos los empleados

### Agregar Nuevo Empleado

1. Click en "Nuevo Empleado" o "+"
2. Completa el formulario:
   - **Nombre**: Nombre del empleado
   - **Apellido**: Apellido
   - **Email**: Correo electrónico (único)
   - **Fecha de nacimiento**: Fecha de nacimiento
   - **Fecha de inicio**: Fecha de contratación
   - **Puesto**: Selecciona el puesto
   - **Estado**: Activo, Inactivo, Terminado
3. Click en "Guardar"

### Editar Empleado

1. En la lista de empleados, busca al empleado
2. Click en el icono de editar
3. Modifica los campos necesarios
4. Click en "Guardar cambios"

### Cambiar Estado de Empleado

Los estados disponibles son:
- **ACTIVE**: Empleado activo
- **INACTIVE**: Empleado temporalmente inactivo
- **TERMINATED**: Empleado terminado/despedido

### Eliminar Empleado

> ⚠️ Esta acción es permanente

1. En la lista de empleados, busca al empleado
2. Click en el icono de eliminar
3. Confirma la eliminación

---

## ⚙️ Configuración de Perfil

### Actualizar Información Personal

1. Click en tu nombre o avatar en la parte superior derecha
2. Selecciona "Configuración" o "Perfil"
3. Edita los campos:
   - Nombre
   - Apellido
   - Email
4. Click en "Guardar cambios"

### Cambiar Contraseña

1. Ve a Configuración > Seguridad
2. Ingresa tu contraseña actual
3. Ingresa tu nueva contraseña
4. Confirma la nueva contraseña
5. Click en "Cambiar contraseña"

**Requisitos de contraseña**:
- Mínimo 8 caracteres
- Al menos una letra mayúscula
- Al menos una letra minúscula
- Al menos un número

### Cerrar Sesión

1. Click en tu nombre o avatar en la parte superior derecha
2. Selecciona "Cerrar sesión" o "Logout"

---

## 🔐 Roles y Permisos

### Rol: Usuario (ROLE_USER)

**Puede hacer**:
- ✅ Ver y crear sus propios gastos
- ✅ Editar sus gastos pendientes
- ✅ Ver su información de nómina
- ✅ Ver lista de empleados
- ✅ Ver su perfil y configuración

**No puede hacer**:
- ❌ Aprobar o rechazar gastos
- ❌ Ver gastos de otros usuarios
- ❌ Crear o editar empleados
- ❌ Gestionar nóminas
- ❌ Administrar usuarios y roles

### Rol: Administrador (ROLE_ADMIN)

**Puede hacer**:
- ✅ Todas las funciones de Usuario
- ✅ Aprobar o rechazar cualquier gasto
- ✅ Ver todos los gastos del sistema
- ✅ Crear, editar y eliminar empleados
- ✅ Gestionar nóminas de todos los empleados
- ✅ Administrar usuarios y asignar roles
- ✅ Acceder a reportes completos

---

## 🔍 Funciones de Administrador

### Aprobar Gastos

1. Ve a "Gastos" > "Pendientes de Aprobación"
2. Revisa los detalles del gasto
3. Click en "Aprobar" o el icono de check
4. El gasto cambiará a estado "Aprobado"

### Rechazar Gastos

1. Ve a "Gastos" > "Pendientes de Aprobación"
2. Revisa los detalles del gasto
3. Click en "Rechazar" o el icono de X
4. Opcionalmente, ingresa un motivo de rechazo
5. Click en "Confirmar rechazo"

### Crear Nómina

1. Ve a "Nómina" > "Nueva Nómina"
2. Selecciona el empleado
3. Ingresa los datos:
   - Periodo (mes/año)
   - Salario base
   - Bonos (opcional)
   - Deducciones
4. El sistema calculará automáticamente el salario neto
5. Click en "Crear"

### Gestionar Puestos

1. Ve a "Configuración" > "Puestos"
2. Verás la lista de puestos existentes
3. Para crear nuevo puesto:
   - Click en "Nuevo Puesto"
   - Ingresa nombre, departamento, salario
   - Click en "Guardar"

---

## ❓ Preguntas Frecuentes

### ¿Puedo editar un gasto después de ser aprobado?
No, una vez que un gasto es aprobado o rechazado, no puede ser editado. Si necesitas hacer cambios, contacta al administrador.

### ¿Cómo sé si mi gasto fue aprobado?
Recibirás una notificación (si están habilitadas) y el estado del gasto cambiará a "Aprobado" en tu lista de gastos.

### ¿Puedo ver los gastos de otros empleados?
Los usuarios regulares solo pueden ver sus propios gastos. Los administradores pueden ver todos los gastos.

### ¿Qué hago si olvidé mi contraseña?
1. En la página de login, click en "¿Olvidaste tu contraseña?"
2. Ingresa tu email
3. Recibirás un enlace para restablecer tu contraseña

### ¿Puedo exportar mis gastos?
Sí, en la página de gastos hay opciones para exportar a:
- Excel (.xlsx)
- PDF
- CSV

### ¿Cómo adjunto documentos a un gasto?
> 🔜 Esta función está en desarrollo y estará disponible en la próxima versión.

### ¿Puedo usar la aplicación desde mi móvil?
Sí, la aplicación es completamente responsiva y funciona en móviles y tablets.

### ¿Los datos están seguros?
Sí, el sistema utiliza:
- Encriptación HTTPS
- Tokens JWT para autenticación
- Contraseñas encriptadas
- Control de acceso basado en roles

---

## 📱 Consejos de Uso

### Mejores Prácticas

1. **Registra gastos inmediatamente**: No esperes días o semanas
2. **Sé descriptivo**: Usa descripciones claras (ej: "Taxi del hotel al cliente XYZ")
3. **Categoriza correctamente**: Usa las categorías apropiadas para mejor análisis
4. **Guarda recibos**: Aunque no estén en el sistema, guárdalos físicamente
5. **Revisa regularmente**: Verifica el estado de tus gastos

### Atajos de Teclado

| Atajo | Acción |
|-------|--------|
| `Ctrl + N` | Nuevo gasto |
| `Ctrl + S` | Guardar |
| `Esc` | Cerrar modal |
| `Ctrl + F` | Buscar |

---

## 🆘 Soporte y Ayuda

### ¿Necesitas ayuda?

1. **Documentación técnica**: Ver [INDEX.md](./INDEX.md)
2. **Problemas técnicos**: Ver [TROUBLESHOOTING.md](./TROUBLESHOOTING.md)
3. **API**: Ver [API_REFERENCE.md](./API_REFERENCE.md)
4. **Contacto**: Contacta al administrador del sistema

### Reportar un Problema

Si encuentras un error:
1. Describe qué estabas haciendo
2. Qué esperabas que pasara
3. Qué pasó en realidad
4. Incluye capturas de pantalla si es posible
5. Envía la información al administrador

---

## 📊 Tutorial Visual

### Flujo Completo: Crear y Aprobar un Gasto

#### Paso 1: Usuario crea un gasto
```
Usuario → Gastos → Nuevo Gasto → Completar formulario → Guardar
```

#### Paso 2: Administrador recibe notificación
```
Admin ve: Dashboard → Gastos Pendientes (badge con cantidad)
```

#### Paso 3: Administrador revisa y aprueba
```
Admin → Gastos Pendientes → Ver detalles → Aprobar
```

#### Paso 4: Usuario recibe confirmación
```
Usuario ve: Estado cambia a "Aprobado" en su lista
```

---

## 🎓 Ejemplos Prácticos

### Ejemplo 1: Viaje de Negocios

Juan va a una reunión con un cliente:

1. **Taxi al aeropuerto**: $30
   - Categoría: Transporte
   - Descripción: "Taxi del hotel al aeropuerto"

2. **Comida con cliente**: $85
   - Categoría: Comida/Negocios
   - Descripción: "Almuerzo con cliente ABC Corp"

3. **Hotel**: $150
   - Categoría: Alojamiento
   - Descripción: "Hotel Marriott - 1 noche"

Total de gastos del viaje: $265

### Ejemplo 2: Compra de Equipo

María necesita un nuevo monitor:

1. **Monitor**: $350
   - Categoría: Equipo/Hardware
   - Descripción: "Monitor Dell 27' para workstation"

El administrador aprueba porque está dentro del presupuesto del departamento.

---

## 🔄 Actualizaciones y Cambios

### Versión 1.1.0 (Actual)
- ✅ Interfaz moderna con React
- ✅ Autenticación JWT mejorada
- ✅ Dashboard con estadísticas
- ✅ Filtros avanzados
- ✅ Diseño responsivo

### Próximas Características (v1.2.0)
- 🔜 Adjuntar documentos a gastos
- 🔜 Notificaciones push
- 🔜 Reportes avanzados
- 🔜 App móvil nativa

---

## 📞 Información de Contacto

**Soporte Técnico**:
- Email: support@expensenoteapp.com
- Horario: Lunes a Viernes, 9:00 - 18:00

**Administrador del Sistema**:
- Contacta a tu administrador local para asistencia

---

**Última actualización**: Diciembre 2024  
**Versión**: 1.1.0  
**Documento**: Guía de Usuario v1.0
