# ExpenseNote App - Frontend

Frontend moderno, profesional y responsivo para el Sistema de Control de Gastos Empresariales.

## 🎨 Características

- **Diseño Moderno**: Gradientes premium, animaciones suaves y efectos visuales profesionales
- **Responsivo**: Funciona perfectamente en desktop, tablet y dispositivos móviles
- **Componentes Reutilizables**: Arquitectura basada en componentes con Framer Motion
- **Gestión de Estado**: Zustand para manejo eficiente del estado
- **Autenticación JWT**: Integración segura con API backend
- **Dark Mode**: Tema oscuro profesional con paleta de colores mejorada

## 🚀 Tecnologías

- **React 18** - Librería UI moderna
- **Vite** - Build tool rápido
- **Tailwind CSS** - Estilos utilitarios
- **Framer Motion** - Animaciones fluidas
- **Zustand** - State management ligero
- **Axios** - Cliente HTTP
- **Lucide React** - Iconografía moderna

## 📦 Instalación

```bash
cd frontend
npm install
```

## 🛠 Desarrollo

```bash
npm run dev
```

La aplicación se ejecutará en `http://localhost:3000`

## 🔨 Build para Producción

```bash
npm run build
```

Los archivos compilados se guardarán en la carpeta `dist/`

## 📁 Estructura del Proyecto

```
frontend/
├── src/
│   ├── pages/              # Páginas principales
│   │   ├── LoginPage.jsx
│   │   ├── DashboardPage.jsx
│   │   ├── ExpensesPage.jsx
│   │   ├── PayrollPage.jsx
│   │   └── SettingsPage.jsx
│   ├── components/         # Componentes reutilizables
│   │   ├── Layout.jsx
│   │   ├── Button.jsx
│   │   ├── Input.jsx
│   │   └── Card.jsx
│   ├── services/           # Servicios API
│   │   └── api.js
│   ├── store/              # State management (Zustand)
│   │   └── authStore.js
│   ├── App.jsx             # Componente principal
│   ├── main.jsx            # Entry point
│   └── index.css           # Estilos globales
├── package.json
├── vite.config.js
├── tailwind.config.js
└── README.md
```

## 🎯 Variables de Entorno

Crea un archivo `.env` basado en `.env.example`:

```env
VITE_API_URL=http://localhost:8080/api/v1
```

## 🔐 Autenticación

La autenticación se realiza mediante JWT tokens:

1. Login/Signup envía credenciales al backend
2. Backend retorna un JWT token
3. Token se almacena en Zustand store (persistido en localStorage)
4. Cada petición incluye el token en el header `Authorization: Bearer <token>`

## 📱 Páginas Implementadas

### Login
- Interfaz de login elegante
- Validación de formularios
- Manejo de errores

### Dashboard
- Estadísticas en tiempo real
- Tabla de gastos recientes
- Tarjetas informativas con gradientes

### Gastos
- Gestión de gastos (CRUD)
- Filtros y búsqueda
- Exportación de datos

### Nómina
- Visualización de nóminas
- Historial de pagos

### Configuración
- Gestión de perfil
- Preferencias de usuario

## 🎨 Diseño

### Paleta de Colores
- **Primario**: Azul (Gradientes de Blue 500 - Indigo 700)
- **Secundario**: Púrpura (Gradientes de Purple 500 - Pink 700)
- **Accent**: Verde/Teal (Gradientes de Green 500 - Teal 700)
- **Fondo**: Slate 900-950 (Tema oscuro profesional)

### Animaciones
- Transiciones suaves con Framer Motion
- Efectos hover en botones y tarjetas
- Animaciones de entrada (fade-in-up, slide-in-right)

## 🔄 Integración con Backend

La aplicación se conecta con Spring Boot backend en:

```
http://localhost:8080/api/v1
```

### Endpoints principales

- `POST /auth/login` - Iniciar sesión
- `POST /auth/signup` - Registrarse
- `GET /expenses` - Obtener gastos
- `POST /expenses` - Crear gasto
- `PUT /expenses/{id}` - Actualizar gasto
- `DELETE /expenses/{id}` - Eliminar gasto
- `GET /payroll` - Obtener nóminas
- `GET /employees` - Obtener empleados

## 🧪 Testing

(En desarrollo)

```bash
npm run test
```

## 📝 Linting

```bash
npm run lint
npm run lint:fix
```

## 🐛 Troubleshooting

### El frontend no se conecta con el backend
1. Verifica que el backend esté corriendo en puerto 8080
2. Revisa la variable `VITE_API_URL` en `.env`
3. Asegúrate de que CORS esté habilitado en Spring Boot

### Errores de Tailwind
1. Ejecuta `npm run build` primero
2. Limpia la cache de Vite: `rm -rf node_modules/.vite`
3. Reinstala: `npm install`

## 📄 Licencia

Este proyecto está bajo la licencia GPLv3.

## 👨‍💻 Autores

- ExpenseNote App Team

---

**Versión**: 2.0.0  
**Última actualización**: 2025
