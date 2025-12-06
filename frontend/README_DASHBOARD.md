# Dashboard de Gastos y Nóminas - Guía de Usuario

## 🎯 Resumen

Esta implementación proporciona un dashboard completo para la gestión y visualización de gastos y nóminas, con gráficos interactivos, KPIs en tiempo real y filtros avanzados.

## ✨ Características Principales

### 📊 Dashboard Principal
- **4 KPIs principales:**
  - Gasto Total (todos los tiempos)
  - Gastos del Mes Actual
  - Nómina del Mes Actual
  - Gasto Promedio

- **Visualizaciones:**
  - Gráfico de tendencia mensual (últimos 6 meses) comparando gastos y nóminas
  - Gráfico de dona mostrando las top 5 categorías de gastos
  - Tabla de los 5 gastos más recientes

- **Filtros:**
  - Rango de fechas (fecha inicio y fecha fin)
  - Filtro rápido con botón de limpiar

### 💰 Página de Gastos
- **Funcionalidades CRUD completas:**
  - ✅ Crear nuevo gasto
  - ✅ Editar gasto existente
  - ✅ Eliminar gasto (con confirmación)
  - ✅ Visualizar todos los gastos

- **Filtros Avanzados:**
  - Búsqueda por texto (concepto o nota)
  - Rango de fechas personalizado
  - Filtrado en tiempo real

- **Validaciones:**
  - Concepto: mínimo 3 caracteres
  - Monto: debe ser mayor a 0
  - Fecha: requerida
  - Nota: opcional

### 📋 Página de Nóminas
- **KPIs Específicos:**
  - Total Nómina (acumulado)
  - Nómina del Mes
  - Número de Empleados
  - Promedio por Nómina

- **Visualizaciones:**
  - Gráfico de barras con tendencia mensual de nóminas
  - Gráfico horizontal con top 10 empleados por total pagado
  - Tabla completa del historial de nóminas

- **Filtros:**
  - Búsqueda por nombre de empleado
  - Rango de fechas

## 🚀 Instalación y Configuración

### Prerequisitos
- Node.js 18+ 
- npm o yarn
- Backend API corriendo en `http://localhost:8080` (o configurar `VITE_API_URL`)

### Pasos de Instalación

1. **Navegar al directorio del frontend:**
```bash
cd frontend
```

2. **Instalar dependencias:**
```bash
npm install
```

3. **Configurar variables de entorno:**
Crear un archivo `.env` basado en `.env.example`:
```bash
cp .env.example .env
```

Editar `.env` para configurar la URL del backend:
```
VITE_API_URL=http://localhost:8080/api/v1
```

4. **Iniciar en modo desarrollo:**
```bash
npm run dev
```

La aplicación estará disponible en `http://localhost:5173`

5. **Build para producción:**
```bash
npm run build
```

Los archivos de producción estarán en el directorio `dist/`

## 📱 Uso de la Aplicación

### Navegación
La aplicación cuenta con un menú lateral con las siguientes opciones:
- **Dashboard** - Vista general con KPIs y gráficos
- **Gastos** - Gestión completa de gastos
- **Nómina** - Visualización de nóminas
- **Configuración** - Ajustes de la cuenta

### Crear un Nuevo Gasto

1. Navegar a la página de **Gastos**
2. Hacer clic en el botón **"Nuevo Gasto"**
3. Completar el formulario:
   - **Concepto:** Descripción corta del gasto (ej: "Viáticos", "Material de oficina")
   - **Nota:** Descripción adicional (opcional)
   - **Monto:** Cantidad en formato numérico
   - **Fecha:** Fecha del gasto
4. Hacer clic en **"Crear"**

### Editar un Gasto

1. En la tabla de gastos, hacer clic en el icono de **lápiz** (✏️)
2. Modificar los campos necesarios
3. Hacer clic en **"Actualizar"**

### Eliminar un Gasto

1. En la tabla de gastos, hacer clic en el icono de **papelera** (🗑️)
2. Confirmar la eliminación en el diálogo que aparece
3. El gasto será eliminado permanentemente

### Aplicar Filtros

#### En el Dashboard:
1. Hacer clic en el botón **"Filtros"**
2. Seleccionar rango de fechas:
   - **Fecha inicial:** Primera fecha del rango
   - **Fecha final:** Última fecha del rango
3. Los gráficos y tabla se actualizarán automáticamente
4. Para limpiar, hacer clic en **"Limpiar Filtros"**

#### En la Página de Gastos:
1. Hacer clic en el botón **"Filtros"**
2. Aplicar filtros:
   - **Buscar:** Escribir texto para buscar en concepto o nota
   - **Rango de fechas:** Seleccionar inicio y fin
3. La tabla se filtra en tiempo real
4. Para limpiar, hacer clic en **"Limpiar Filtros"**

#### En la Página de Nóminas:
1. Hacer clic en el botón **"Filtros"**
2. Aplicar filtros:
   - **Buscar:** Escribir nombre de empleado
   - **Rango de fechas:** Seleccionar inicio y fin
3. Los gráficos y tabla se actualizarán automáticamente
4. Para limpiar, hacer clic en **"Limpiar Filtros"**

## 🎨 Características de UX/UI

### Diseño Responsive
- **Móvil:** Diseño optimizado para pantallas pequeñas
- **Tablet:** Layout adaptado con grids de 2 columnas
- **Desktop:** Aprovecha el espacio completo con grids de 4 columnas

### Animaciones
- Transiciones suaves al cargar páginas
- Animaciones de entrada para cards y elementos
- Feedback visual en botones y acciones

### Temas
- Tema oscuro por defecto
- Colores personalizados para diferentes tipos de datos:
  - 🔵 Azul: Gastos totales
  - 🟢 Verde: Gastos/nómina del mes
  - 🟣 Púrpura: Nóminas
  - 🟠 Naranja: Promedios

### Estados de Loading
- Indicadores visuales durante la carga de datos
- Mensajes claros cuando no hay datos
- Feedback en operaciones CRUD

## 🔧 Tecnologías Utilizadas

### Frontend
- **React 18.2.0** - Librería de UI
- **Vite 5.0.0** - Build tool y dev server
- **Tailwind CSS 3.4.0** - Framework de CSS
- **Framer Motion 10.16.4** - Animaciones
- **Chart.js 4.4.0** - Gráficos
- **react-chartjs-2 5.2.0** - Integración de Chart.js con React

### Estado y Datos
- **Zustand 4.4.7** - Gestión de estado global
- **Axios 1.7.7** - Cliente HTTP
- **date-fns 2.30.0** - Utilidades de fecha

### Iconos y UI
- **Lucide React 0.299.0** - Iconos SVG

## 📊 Estructura de Datos

### Expense (Gasto)
```javascript
{
  id: number,
  concept: string,    // Concepto del gasto (min 3 chars)
  note: string,       // Nota adicional (opcional)
  amount: number,     // Monto (> 0)
  date: string,       // ISO date string
  employee: {         // Empleado asociado
    id: number,
    name: string,
    surname: string
  }
}
```

### Payroll (Nómina)
```javascript
{
  id: number,
  amount: number,     // Monto de la nómina
  date: string,       // ISO date string
  employee: {         // Empleado asociado
    id: number,
    name: string,
    surname: string
  }
}
```

## 🔐 Autenticación

La aplicación requiere autenticación JWT:
1. Iniciar sesión en `/login`
2. El token se almacena automáticamente
3. Todas las peticiones incluyen el token en el header `Authorization: Bearer <token>`
4. Si el token expira, el usuario es redirigido al login automáticamente

## 🐛 Solución de Problemas

### El dashboard no muestra datos
1. Verificar que el backend esté corriendo
2. Comprobar la consola del navegador por errores
3. Verificar la configuración de `VITE_API_URL`
4. Asegurarse de estar autenticado correctamente

### Los gráficos no se muestran
1. Limpiar caché del navegador
2. Verificar que hay datos disponibles en el rango de fechas seleccionado
3. Comprobar la consola por errores de Chart.js

### Errores de validación al crear gastos
- **Concepto:** Debe tener al menos 3 caracteres
- **Monto:** Debe ser un número mayor a 0
- **Fecha:** Es un campo requerido

### El filtro no funciona
1. Asegurarse de seleccionar fechas válidas (inicio < fin)
2. Limpiar filtros y volver a aplicar
3. Recargar la página si es necesario

## 📈 Optimización de Rendimiento

### Estrategias Implementadas
1. **Memoización:** Uso de `useMemo` para cálculos costosos
2. **Carga Paralela:** `Promise.all` para múltiples endpoints
3. **Filtrado en Cliente:** Evita llamadas redundantes al servidor
4. **Código Dividido:** Build optimizado con chunks separados

### Mejoras Futuras
- Implementar paginación en servidor
- Añadir virtualización para tablas grandes
- Implementar caché de datos
- Lazy loading de componentes

## 📝 Scripts Disponibles

```bash
# Desarrollo
npm run dev          # Inicia servidor de desarrollo

# Build
npm run build        # Crea build de producción
npm run preview      # Preview del build de producción

# Linting
npm run lint         # Ejecuta ESLint
npm run lint:fix     # Arregla problemas de linting automáticamente
```

## 🤝 Soporte

Para problemas o preguntas:
1. Revisar esta documentación
2. Consultar `DASHBOARD_DOCUMENTATION.md` para detalles técnicos
3. Abrir un issue en el repositorio

## 📄 Licencia

Este proyecto es parte de BizFlow ERP y está sujeto a su licencia.

---

**Última actualización:** Diciembre 2024  
**Versión:** 2.0.0
