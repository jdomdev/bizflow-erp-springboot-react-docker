# Postman Collection - BizFlow ERP

Esta carpeta contiene la colección de Postman para probar los endpoints de la API de BizFlow ERP.

## Archivo

- **bizflow_erp_app.postman_collection.json**: Colección completa con todos los endpoints del sistema

## Cómo Importar

### Opción 1: Postman Desktop o Web
1. Abre Postman (desktop o https://web.postman.co/)
2. Click en **Import** (botón superior izquierdo)
3. Arrastra el archivo `bizflow_erp_app.postman_collection.json` o selecciónalo
4. Click en **Import**

### Opción 2: Extensión de VS Code
1. Abre la extensión de Postman en VS Code
2. Click en **Import** en el panel de colecciones
3. Selecciona el archivo `bizflow_erp_app.postman_collection.json`

## Configuración de Entornos

La aplicación tiene 3 entornos disponibles:

### Producción (prod)
- **Base URL**: `http://localhost:8181`
- **Database Port**: `5442`

### Desarrollo (dev)
- **Base URL**: `http://localhost:8282`
- **Database Port**: `5433`

### Test (test)
- **Base URL**: `http://localhost:8383`
- **Database Port**: `5434`

## Uso Recomendado

1. **Autenticación**: Ejecuta primero el endpoint de login para obtener el token JWT
2. **Headers**: El token debe incluirse en el header `Authorization: Bearer {token}`
3. **Endpoints Principales**:
   - `/api/v1/auth/*` - Autenticación y registro
   - `/api/employees` - Gestión de empleados
   - `/api/positions` - Puestos de trabajo
   - `/api/payrolls` - Nóminas
   - `/api/expenses` - Gastos

## Notas

- Asegúrate de que los contenedores Docker estén corriendo antes de probar los endpoints
- Para pruebas locales, usa el perfil correspondiente en `docker-compose.yml`
- Los SQL scripts de inicialización se encuentran en `/sql/`

## Última Actualización

- **Fecha**: 15 de diciembre de 2025
- **Versión**: 1.0
- **Rama**: chore/multi-env-db-config
