# Dashboard de Gastos y Nóminas - Documentación

## Resumen
Este documento describe la implementación del dashboard completo con gráficos, KPIs y filtros avanzados para la gestión de gastos y nóminas.

## Arquitectura

### Componentes Principales

#### 1. **DashboardPage** (`/src/pages/DashboardPage.jsx`)
Panel principal con vista general de gastos y nóminas.

**Características:**
- 4 KPIs principales: Gasto Total, Gastos del Mes, Nómina del Mes, Gasto Promedio
- Gráfico de línea con tendencia mensual (últimos 6 meses)
- Gráfico de dona con top 5 categorías de gastos
- Tabla de gastos recientes (últimos 5)
- Filtros avanzados: rango de fechas, estado

**Rendimiento:**
- Uso de `useMemo` para cálculos de gráficos y datos filtrados
- Carga optimizada con `Promise.all` para múltiples APIs

#### 2. **ExpensesPage** (`/src/pages/ExpensesPage.jsx`)
Gestión completa de gastos con CRUD.

**Características:**
- Listado completo de gastos con paginación
- Crear, editar y eliminar gastos
- Aprobar/rechazar gastos (flujo de aprobación)
- Filtros avanzados: búsqueda por texto, rango de fechas, estado
- Modal responsive para crear/editar
- Validación de formularios

**Validaciones:**
- Concepto: mínimo 3 caracteres
- Monto: debe ser mayor a 0
- Fecha: requerida

#### 3. **PayrollPage** (`/src/pages/PayrollPage.jsx`)
Visualización y análisis de nóminas.

**Características:**
- 4 KPIs: Total Nómina, Nómina del Mes, Número de Empleados, Promedio
- Gráfico de barras con tendencia mensual
- Gráfico de barras horizontal con top 10 empleados por total pagado
- Tabla completa de historial de nómina
- Filtros: búsqueda por empleado, rango de fechas

### Componentes Reutilizables

#### Charts (`/src/components/charts/`)

1. **LineChart.jsx**
   - Gráfico de líneas con Chart.js
   - Configuración de colores para tema oscuro
   - Props: `data`, `options`, `height`

2. **BarChart.jsx**
   - Gráfico de barras vertical/horizontal
   - Soporte para múltiples datasets
   - Props: `data`, `options`, `height`

3. **DoughnutChart.jsx**
   - Gráfico de dona para distribución
   - Leyenda a la derecha
   - Props: `data`, `options`, `height`

#### Filters (`/src/components/filters/`)

1. **DateRangeFilter.jsx**
   - Selector de rango de fechas
   - Props: `startDate`, `endDate`, `onStartDateChange`, `onEndDateChange`, `label`

2. **SearchFilter.jsx**
   - Campo de búsqueda de texto
   - Icono de búsqueda integrado
   - Props: `value`, `onChange`, `placeholder`, `label`

3. **SelectFilter.jsx**
   - Dropdown para selección
   - Soporte para opciones dinámicas
   - Props: `value`, `onChange`, `options`, `label`, `placeholder`

### Stores (Zustand)

#### authStore.js
Gestión de estado global para autenticación, gastos y nóminas.

**Stores disponibles:**
1. `useAuthStore` - Autenticación de usuario
2. `useExpenseStore` - Estado de gastos
3. `usePayrollStore` - Estado de nóminas

**Métodos de ExpenseStore:**
- `setExpenses(expenses)` - Establecer lista completa
- `addExpense(expense)` - Agregar nuevo gasto
- `updateExpense(id, updatedExpense)` - Actualizar gasto existente
- `removeExpense(id)` - Eliminar gasto
- `setLoading(loading)` - Estado de carga
- `setError(error)` - Gestión de errores

**Métodos de PayrollStore:**
- Similar a ExpenseStore pero para nóminas
- `setPayrolls`, `addPayroll`, `updatePayroll`, `removePayroll`

### Servicios API

#### api.js
Cliente HTTP con Axios para integración con backend.

**Servicios de Gastos:**
```javascript
expenseService.getAll()                // Obtener todos los gastos
expenseService.getById(id)             // Obtener por ID
expenseService.getByStatus(status)     // Filtrar por estado
expenseService.create(data)            // Crear nuevo
expenseService.update(id, data)        // Actualizar
expenseService.delete(id)              // Eliminar
expenseService.approve(id)             // Aprobar
expenseService.reject(id)              // Rechazar
```

**Servicios de Nómina:**
```javascript
payrollService.getAll()                    // Obtener todas las nóminas
payrollService.getById(id)                 // Obtener por ID
payrollService.getByEmployee(employeeId)   // Por empleado
payrollService.create(data)                // Crear
payrollService.update(id, data)            // Actualizar
payrollService.delete(id)                  // Eliminar
```

## Optimización de Rendimiento

### Estrategias Implementadas

1. **Memoización**
   - Uso de `useMemo` para cálculos costosos de datos de gráficos
   - Filtrado de datos memoizado para evitar recalculos innecesarios

2. **Carga Paralela**
   - `Promise.all` para cargar múltiples endpoints simultáneamente
   - Reducción del tiempo de carga inicial

3. **Estados de Carga**
   - Indicadores visuales durante la carga de datos
   - Mejor experiencia de usuario

4. **Filtrado en Cliente**
   - Filtros aplicados en el cliente usando `useMemo`
   - Evita llamadas redundantes al servidor

## Lógica de Filtrado

### DashboardPage
```javascript
// Filtros disponibles: fecha inicio, fecha fin, estado
const filteredExpenses = useMemo(() => {
  return expenses.filter((expense) => {
    const expDate = new Date(expense.date);
    
    if (startDate && expDate < new Date(startDate)) return false;
    if (endDate && expDate > new Date(endDate)) return false;
    if (statusFilter && expense.status !== statusFilter) return false;
    
    return true;
  });
}, [expenses, startDate, endDate, statusFilter]);
```

### ExpensesPage
```javascript
// Filtros: búsqueda de texto, rango de fechas, estado
const filteredExpenses = useMemo(() => {
  return expenses.filter((expense) => {
    const matchesSearch = 
      expense.concept?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      expense.note?.toLowerCase().includes(searchTerm.toLowerCase());
    
    const expDate = new Date(expense.date);
    const matchesDateRange = 
      (!startDate || expDate >= new Date(startDate)) &&
      (!endDate || expDate <= new Date(endDate));
    
    const matchesStatus = !statusFilter || expense.status === statusFilter;

    return matchesSearch && matchesDateRange && matchesStatus;
  });
}, [expenses, searchTerm, startDate, endDate, statusFilter]);
```

### PayrollPage
```javascript
// Filtros: búsqueda de empleado, rango de fechas
const filteredPayrolls = useMemo(() => {
  return payrolls.filter((payroll) => {
    const employee = employees.find((e) => e.id === payroll.employee?.id);
    const employeeName = employee ? `${employee.name} ${employee.surname}` : '';
    
    const matchesSearch = employeeName.toLowerCase().includes(searchTerm.toLowerCase());
    
    const payDate = new Date(payroll.date);
    const matchesDateRange = 
      (!startDate || payDate >= new Date(startDate)) &&
      (!endDate || payDate <= new Date(endDate));

    return matchesSearch && matchesDateRange;
  });
}, [payrolls, employees, searchTerm, startDate, endDate]);
```

## Validación de Datos

### Validación en Frontend (ExpensesPage)

```javascript
const validateForm = () => {
  const errors = {};
  
  if (!formData.concept || formData.concept.trim().length < 3) {
    errors.concept = 'El concepto debe tener al menos 3 caracteres';
  }
  if (!formData.amount || parseFloat(formData.amount) <= 0) {
    errors.amount = 'El monto debe ser mayor a 0';
  }
  if (!formData.date) {
    errors.date = 'La fecha es requerida';
  }

  setFormErrors(errors);
  return Object.keys(errors).length === 0;
};
```

### Validación en Backend
- La validación del backend se maneja a través de las anotaciones de Jakarta Validation
- Ver entidades `Expense` y `Payroll` para restricciones completas

## Responsiveness

### Breakpoints de Tailwind CSS
- `md:` - 768px (tablets)
- `lg:` - 1024px (desktops pequeños)
- `xl:` - 1280px (desktops grandes)

### Estrategias de Diseño Responsivo
1. Grid responsivo que se adapta a diferentes tamaños de pantalla
2. Tablas con scroll horizontal en móviles
3. Modales con ancho máximo y padding adaptativo
4. Gráficos con `maintainAspectRatio: false` para adaptabilidad

## Tecnologías Utilizadas

- **React 18.2.0** - Framework UI
- **Chart.js 4.4.0** - Librería de gráficos
- **react-chartjs-2 5.2.0** - Wrapper de React para Chart.js
- **Framer Motion 10.16.4** - Animaciones
- **Zustand 4.4.7** - Gestión de estado
- **Axios 1.7.7** - Cliente HTTP
- **Tailwind CSS 3.4.0** - Estilos
- **Lucide React 0.299.0** - Iconos

## Instalación de Dependencias

```bash
cd frontend
npm install
npm install chart.js@4.4.0 react-chartjs-2@5.2.0
```

## Build y Deploy

```bash
# Desarrollo
npm run dev

# Build de producción
npm run build

# Preview de producción
npm run preview
```

## Estructura de Archivos

```
frontend/
├── src/
│   ├── components/
│   │   ├── charts/
│   │   │   ├── LineChart.jsx
│   │   │   ├── BarChart.jsx
│   │   │   └── DoughnutChart.jsx
│   │   ├── filters/
│   │   │   ├── DateRangeFilter.jsx
│   │   │   ├── SearchFilter.jsx
│   │   │   └── SelectFilter.jsx
│   │   ├── Button.jsx
│   │   ├── Card.jsx
│   │   └── Input.jsx
│   ├── pages/
│   │   ├── DashboardPage.jsx
│   │   ├── ExpensesPage.jsx
│   │   └── PayrollPage.jsx
│   ├── services/
│   │   └── api.js
│   ├── store/
│   │   └── authStore.js
│   └── App.jsx
└── package.json
```

## Mejoras Futuras

1. **Paginación en Servidor**
   - Implementar paginación en el backend para grandes volúmenes de datos
   - Agregar controles de paginación en la UI

2. **Exportación de Datos**
   - Implementar exportación a Excel/CSV
   - Exportación de gráficos como imágenes

3. **Más Filtros**
   - Filtros por empleado en gastos
   - Filtros por categoría/concepto predefinidos
   - Filtros por rango de montos

4. **Notificaciones en Tiempo Real**
   - WebSockets para actualizaciones en vivo
   - Notificaciones push para aprobaciones pendientes

5. **Tests**
   - Tests unitarios con Jest
   - Tests de integración con React Testing Library
   - Tests E2E con Cypress

## Soporte y Mantenimiento

Para preguntas o problemas, consultar:
- Repositorio: https://github.com/jdomdev/bizflow-erp-springboot-react-docker
- Issues: https://github.com/jdomdev/bizflow-erp-springboot-react-docker/issues

---

**Última actualización:** Diciembre 2024
**Versión:** 2.0.0
